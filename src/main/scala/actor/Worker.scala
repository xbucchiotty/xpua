package actor

import akka.actor.{Props, OneForOneStrategy, Actor}
import akka.actor.SupervisorStrategy.Restart
import akka.pattern.ask
import util.{TransformerActor, FileReaderActor, CollectionCleanerActor}
import akka.util.Timeout
import akka.routing.{SmallestMailboxRouter, RoundRobinRouter}

class Worker extends Actor with akka.actor.ActorLogging {

  import context.dispatcher

  implicit val timeout = Timeout(1000)

  override val supervisorStrategy = OneForOneStrategy() {
    case _ => Restart
  }

  val fileReader = context.actorOf(Props[FileReaderActor], name = "fileReader")
  val fileTransformer = context.actorOf(Props[TransformerActor].withRouter(RoundRobinRouter(nrOfInstances = 5)), name = "fileTransformer")
  val collectionCleaner = context.actorOf(Props[CollectionCleanerActor], name = "collectionCleaner")
  val loader = context.actorOf(Props[MongoWriterActor].withRouter((SmallestMailboxRouter(nrOfInstances = 2))), name = "mongoLoader")


  def receive = {
    case Go(fileName, f, db, collection) => {

      val transformResponse = ask(fileReader, Load(fileName))
        .mapTo[Loaded]
        .flatMap {
        case Loaded(objs) => ask(fileTransformer, Transform(objs, f))
      }.mapTo[Transformed]

      val collectionCleaningStatus = ask(collectionCleaner, Clean(db, collection)).mapTo[Message]

      for {o <- transformResponse
           c <- collectionCleaningStatus if c == Cleaned
           w <- ask(loader, Write(o.objects, db, collection))}
      yield w


    }
    case message => println(message)

  }


}
