import actor._
import akka.actor.{Props, ActorSystem}
import akka.routing.{RoundRobinRouter, SmallestMailboxRouter}


object AkkaLoader extends App {

  val system = ActorSystem("LoadingSystem")

  val fileReader = system.actorOf(Props[FileReaderActor].withRouter(SmallestMailboxRouter(nrOfInstances = 2)), name = "fileReader")
  val fileTransformer = system.actorOf(Props[TransformerActor].withRouter(RoundRobinRouter(nrOfInstances = 5)), name = "fileTransformer")
  val collectionCleaner = system.actorOf(Props[CollectionCleanerActor].withRouter(SmallestMailboxRouter(nrOfInstances = 2)), name = "collectionCleaner")
  val writer = system.actorOf(Props[MongoWriterActor].withRouter(SmallestMailboxRouter(nrOfInstances = 2)), name = "mongoWriter")

  val fileWorker = system.actorOf(Props[FileWorker], "fileWorker")

  val mainWorker = system.actorOf(Props[MainWorker], "mainWorker")


  mainWorker ! Go
}

