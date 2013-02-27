package actor

import akka.actor.Actor
import akka.util.Timeout
import akka.pattern.{ask, pipe}
import concurrent.{Future, Await}
import concurrent.duration.Duration
import java.util.concurrent.TimeUnit.SECONDS
import com.mongodb.casbah.commons.MongoDBObject

class FileWorker extends Actor {

  private val fileReader = context.actorFor("akka://LoadingSystem/user/fileReader")
  private val fileTransformer = context.actorFor("akka://LoadingSystem/user/fileTransformer")
  private val collectionCleaner = context.actorFor("akka://LoadingSystem/user/collectionCleaner")
  private val writer = context.actorFor("akka://LoadingSystem/user/mongoWriter")

  implicit val timeout = Timeout(15000)

  import context.dispatcher

  def receive = {
    case message: LoadFromFile => {
      println("Loading %s".format(message.fileName))

      val transformResponseFuture = for {
        readResponse <- readFile(message)
        transformResponse <- transformObjectToMongo(readResponse, message)
      } yield (transformResponse)

      val collectionCleaningStatusFuture = cleanCollection(message)

      val futureOfWrite = for {
        transformResponse <- transformResponseFuture
        cleaningStatus <- collectionCleaningStatusFuture if cleaningStatus == CollectionCleaned
        write <- writeCollections(transformResponse, message)
      } yield (write)

      Await.result(futureOfWrite pipeTo writer, Duration(15, SECONDS))

      sender ! Done
    }

  }

  def writeCollections(transformResponse: Transformed[MongoDBObject], message: LoadFromFile): Future[Message] = {
    ask(writer, Write(transformResponse.objects, message.db, message.collection)).mapTo[Message]
  }

  def cleanCollection(message: LoadFromFile): Future[Message] = {
    ask(collectionCleaner, CleanCollection(message.db, message.collection)).mapTo[Message]
  }

  def transformObjectToMongo(readResponse: FileLoaded, message: LoadFromFile): Future[Transformed[MongoDBObject]] = {
    ask(fileTransformer, Transform(readResponse.objects, message.f)).mapTo[Transformed[MongoDBObject]]
  }

  def readFile(message: LoadFromFile): Future[FileLoaded] = {
    ask(fileReader, LoadFile(message.fileName)).mapTo[FileLoaded]
  }
}
