package actor

import akka.actor.Actor
import com.mongodb.casbah.Imports._
import util.Configuration

class MongoWriterActor extends Actor {

  private lazy val db = Configuration.db

  def receive = {
    case Write(objects, collection) => {
      val coll = db(collection.name())
      objects.map(obj => {
        coll += obj
      })
      sender ! Done
    }
  }

}

