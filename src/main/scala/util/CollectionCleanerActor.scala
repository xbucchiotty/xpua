package util

import akka.actor.Actor
import com.mongodb.casbah.MongoDB
import actor.{Cleaned, Clean}

class CollectionCleanerActor extends Actor {

  def receive = {
    case Clean(db, collection) => {
      println("[CLEAN] : start %s".format(collection.name()))

      val mongoCollection = db(collection.name())
      mongoCollection.dropIndexes()
      mongoCollection.dropCollection()
      mongoCollection.drop()
      sender ! Cleaned

      println("[CLEAN] : end %s".format(collection.name()))
    }
  }

}

