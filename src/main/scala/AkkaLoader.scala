import actor._
import akka.actor.{Props, ActorSystem}
import akka.routing.{RoundRobinRouter, SmallestMailboxRouter}
import com.typesafe.config.ConfigFactory


object AkkaLoader extends App {

  val config = ConfigFactory.parseString( """
    akka.loglevel = INFO
      akka.actor.debug {
        receive = on
         lifecycle = on
    }

    akka{
      event-handlers = ["akka.event.Logging$DefaultLogger"]
    }""")

  val system = ActorSystem("LoadingSystem", config)

  val progressListener = system.actorOf(Props[ProgressListenerActor], name = "progressListener")

  val fileReader = system.actorOf(Props[FileReaderActor], name = "fileReader")
  val collectionCleaner = system.actorOf(Props[CollectionCleanerActor].withRouter(SmallestMailboxRouter(nrOfInstances = 1)), name = "collectionCleaner")
  val writer = system.actorOf(Props[MongoWriterActor].withRouter(SmallestMailboxRouter(nrOfInstances = 1)), name = "mongoWriter")

  val songReader = system.actorOf(Props(DatabaseReaderActor("subset_track_metadata.db")), name = "songReader")
  val termOrTagReader = system.actorOf(Props(DatabaseReaderActor("subset_artist_term.db")), name = "termOrTagReader")
  val similaritiesReader = system.actorOf(Props(DatabaseReaderActor("subset_artist_similarity.db")), name = "similaritiesReader")

  val artistLoader = system.actorOf(Props[ArtistLoader].withRouter(RoundRobinRouter(nrOfInstances = 1)), "artistLoader")

  val preloader = system.actorOf(Props[PreloaderActor], "preloader")

  preloader ! Go
}

