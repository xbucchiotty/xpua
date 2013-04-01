import actor._
import actor.DatabaseReaderActor
import akka.actor.{Props, ActorSystem}
import akka.routing.{RoundRobinRouter, SmallestMailboxRouter}
import base._
import com.typesafe.config.ConfigFactory
import file.{ArtistsCollection, LocationsCollection}


object XPUALoader extends App {

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

  system.actorOf(Props[ProgressListenerActor], name = "progressListener")

  system.actorOf(Props[FileReaderActor], name = "fileReader")
  system.actorOf(Props(DatabaseReaderActor("subset_track_metadata.db")), name = "songReader")
  system.actorOf(Props(DatabaseReaderActor("subset_artist_term.db")), name = "termOrTagReader")
  system.actorOf(Props(DatabaseReaderActor("subset_artist_similarity.db")), name = "similaritiesReader")

  system.actorOf(Props[ArtistsCollection], name = "artists")
  system.actorOf(Props[LocationsCollection], name = "locations")
  system.actorOf(Props[ArtistSimilaritiesCollection], name = "similaritites")
  system.actorOf(Props[SongsCollection], name = "songs")
  system.actorOf(Props[TagsByArtistCollection], name = "tags")
  system.actorOf(Props[TermsByArtistCollection], name = "terms")

  system.actorOf(Props[ArtistWriterActor], "artistWriter")
  system.actorOf(Props[ArtistLoaderActor], "artistLoader")
  val preloader = system.actorOf(Props[PreloaderActor], "preloader")

  preloader ! Go
}

