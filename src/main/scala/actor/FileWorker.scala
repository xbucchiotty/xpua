package actor

import akka.actor.Actor
import akka.util.Timeout
import akka.pattern.{ask, pipe}
import concurrent.Await
import concurrent.duration.Duration
import java.util.concurrent.TimeUnit.SECONDS

class FileWorker extends Actor {

  private val fileReader = context.actorFor("akka://LoadingSystem/user/fileReader")
  private val fileTransformer = context.actorFor("akka://LoadingSystem/user/fileTransformer")
  private val collectionCleaner = context.actorFor("akka://LoadingSystem/user/collectionCleaner")
  private val writer = context.actorFor("akka://LoadingSystem/user/mongoWriter")

  implicit val timeout = Timeout(5000)

  import context.dispatcher

  def receive = {
    case message: LoadFromFile => {
      val transformResponseFuture = for {
        readResponse <- ask(fileReader, LoadFile(message.fileName)).mapTo[FileLoaded]
        transformResponse <- ask(fileTransformer, Transform(readResponse.objects, message.f)).mapTo[Transformed]
      } yield (transformResponse)

      val collectionCleaningStatusFuture = ask(collectionCleaner, CleanCollection(message.db, message.collection)).mapTo[Message]

      val futureOfWrite = for {transformResponse <- transformResponseFuture
                               cleaningStatus <- collectionCleaningStatusFuture if cleaningStatus == CollectionCleaned
                               write <- ask(writer, Write(transformResponse.objects, message.db, message.collection)).mapTo[Message]
      } yield (write)

      Await.result(futureOfWrite pipeTo writer, Duration(5, SECONDS))

      sender ! Done

    }

    case x => println("dead %s".format(x))


  }


}
