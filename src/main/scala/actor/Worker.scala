package actor

import akka.actor.{Props, OneForOneStrategy, Actor}
import akka.actor.SupervisorStrategy.Restart
import akka.pattern.ask
import akka.util.Timeout
import akka.routing.{SmallestMailboxRouter, RoundRobinRouter}

class Worker extends Actor with akka.actor.ActorLogging {

  var currentProgress: Int = 0
  var startTime: Long = 0L

  import context.dispatcher

  override def preStart() {
    super.preStart()
    currentProgress = 0
    startTime = System.currentTimeMillis()
  }


  override def postStop() {
    super.postStop()
    println("Done in %s ms".format((System.currentTimeMillis() - startTime)))
  }

  implicit val timeout = Timeout(1000)

  override val supervisorStrategy = OneForOneStrategy() {
    case _ => Restart
  }

  val fileReader = context.actorOf(Props[FileReaderActor].withRouter(SmallestMailboxRouter(nrOfInstances = 2)), name = "fileReader")
  val fileTransformer = context.actorOf(Props[TransformerActor].withRouter(RoundRobinRouter(nrOfInstances = 5)), name = "fileTransformer")
  val collectionCleaner = context.actorOf(Props[CollectionCleanerActor].withRouter(SmallestMailboxRouter(nrOfInstances = 2)), name = "collectionCleaner")
  val writer = context.actorOf(Props[MongoWriterActor].withRouter(SmallestMailboxRouter(nrOfInstances = 2)), name = "mongoWriter")


  def receive = {
    case Go(fileName, f, db, collection) => {

      currentProgress = currentProgress + 1

      val transformResponse = ask(fileReader, Load(fileName))
        .mapTo[Loaded]
        .flatMap {
        case Loaded(objs) => ask(fileTransformer, Transform(objs, f))
      }.mapTo[Transformed]

      val collectionCleaningStatus = ask(collectionCleaner, Clean(db, collection)).mapTo[Message]

      val w = for {o <- transformResponse
                   c <- collectionCleaningStatus if c == Cleaned
      }
      yield Write(o.objects, db, collection)

      w.onSuccess {
        case w: Write => {
          writer ! w
        }
      }
    }

    case Done => {
      currentProgress = currentProgress - 1

      if (currentProgress == 0) {
        context.stop(context.parent)
      }
    }
  }


}
