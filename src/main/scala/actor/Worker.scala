package actor

import akka.actor.{Props, OneForOneStrategy, Actor}
import akka.actor.SupervisorStrategy.Restart
import akka.pattern.{ask, pipe}
import util.{TransformerActor, FileReaderActor, CollectionCleanerActor}
import akka.util.Timeout

class Worker extends Actor with akka.actor.ActorLogging {

  import context.dispatcher

  implicit val timeout = Timeout(1000)

  override val supervisorStrategy = OneForOneStrategy() {
    case _ => Restart
  }

  val fileReader = context.actorOf(Props[FileReaderActor], name = "fileReader")
  val fileTransformer = context.actorOf(Props[TransformerActor], name = "fileTransformer")
  val collectionCleaner = context.actorOf(Props[CollectionCleanerActor], name = "collectionCleaner")
  val loader = context.actorOf(Props[MongoWriterActor], name = "mongoLoader")


  def receive = {
    case Go(fileName, f, db, collection) => {

      val transformationResult = ask(fileReader, Load(fileName))
        .mapTo[Message]
        .map(message => message match {
        case Loaded(obj) => Transform(obj, f)
      }).pipeTo(fileTransformer)(sender = self)
        .mapTo[Transformed]

      val collectionCleaningStatus = ask(collectionCleaner, Clean(db, collection)).mapTo[Message]

      val writeOrder = for {o <- transformationResult
                            c <- collectionCleaningStatus if c == Cleaned}
      yield (Write(o.objects, db, collection))

      loader ! writeOrder
    }
    case message: Message => {
      println(message)
    }


  }


}
