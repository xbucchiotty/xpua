package util

import akka.actor.Actor
import com.mongodb.casbah.MongoDB
import actor.{Clean, Write}

case class CollectionCleaner(db: MongoDB) extends Actor {

  def receive = {
    case Clean(coll) => clean(coll)
    sender ! Write
  }

  def clean(collectionName: String) {
    val collection = db(collectionName)
    collection.dropIndexes()
    collection.dropCollection()
    collection.drop()
  }
}

