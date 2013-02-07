package util

import akka.actor.Actor
import com.mongodb.casbah.MongoDB
import actor.{Cleaned, Clean, Write}

case class CollectionCleanerActor(db: MongoDB) extends Actor {

  def receive = {
    case Clean(collectionName) => {
      println("[CLEAN] : start %s".format(collectionName))

      val collection = db(collectionName)
      collection.dropIndexes()
      collection.dropCollection()
      collection.drop()
      sender ! Cleaned

      println("[CLEAN] : end %s".format(collectionName))
    }
  }

}

