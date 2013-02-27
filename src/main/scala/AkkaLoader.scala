import actor._
import akka.actor.{Props, ActorSystem}
import akka.routing.{RoundRobinRouter, SmallestMailboxRouter}
import com.typesafe.config.ConfigFactory


object AkkaLoader extends App {

  val config = ConfigFactory.parseString( """
    akka.loglevel = DEBUG
      akka.actor.debug {
        receive = on
         lifecycle = on
    }

    akka{
      event-handlers = ["akka.event.Logging$DefaultLogger"]
    }
                                          """)

  val system = ActorSystem("LoadingSystem", config)

  val progressListener = system.actorOf(Props[ProgressListenerActor], name = "progressListener")

  val fileReader = system.actorOf(Props[FileReaderActor].withRouter(SmallestMailboxRouter(nrOfInstances = 2)), name = "fileReader")
  val fileTransformer = system.actorOf(Props[TransformerActor].withRouter(RoundRobinRouter(nrOfInstances = 5)), name = "fileTransformer")
  val collectionCleaner = system.actorOf(Props[CollectionCleanerActor].withRouter(SmallestMailboxRouter(nrOfInstances = 2)), name = "collectionCleaner")
  val writer = system.actorOf(Props[MongoWriterActor].withRouter(SmallestMailboxRouter(nrOfInstances = 2)), name = "mongoWriter")
  val databaseReader = system.actorOf(Props[DatabaseReaderActor].withRouter(SmallestMailboxRouter(nrOfInstances = 2)), name = "databaseReader")

  val actorLoader = system.actorOf(Props[ActorLoader], "actorLoader")
  val fileWorker = system.actorOf(Props[FileWorker].withRouter(RoundRobinRouter(nrOfInstances = 5)), "fileWorker")

  val mainWorker = system.actorOf(Props[MainWorker], "mainWorker")

  mainWorker ! Go
}

