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

}

