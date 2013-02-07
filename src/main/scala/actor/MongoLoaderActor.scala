package actor

import akka.pattern.ask
import akka.actor._
import akka.util.Timeout
import akka.actor.Actor
import util.FileReader
import com.mongodb.casbah.Imports._

class MongoLoaderActor extends Actor {

  def receive = {
    case Load(info) => {
      println("Start loading %s".format(info))
      load(info)
    }
    case Write(info) => {
      write(info)
      println("Finish load %s".format(info))
    }
  }

  protected def load[T](info: ProcessInfo) {
    implicit val timeout = Timeout(1000)
    import context.dispatcher

    val response = context.actorFor("../collectionCleaner") ? Clean(info.collection.name())
    response.mapTo[Message]
      .onSuccess {
      case Cleaned => self ! Write(info)
    }
  }


  def write(process: ProcessInfo) {
    val beansForMongo = FileReader(process.fileName) parseAndApply process.toMongo

    beansForMongo.map(bean => {
      process.db(process.collection.name()) += bean
    })
  }
}

