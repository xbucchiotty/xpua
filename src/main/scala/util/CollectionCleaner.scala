package util

import akka.actor.Actor
import com.mongodb.casbah.MongoDB
import actor.{Cleaned, Clean, Write}

case class CollectionCleaner(db: MongoDB) extends Actor {

  def receive = {
    case Clean(collectionName) => {
      println("Start clearing %s".format(collectionName))

      val collection = db(collectionName)
      collection.dropIndexes()
      collection.dropCollection()
      collection.drop()
      sender ! Cleaned

      println("End clearing %s".format(collectionName))
    }
  }

}

