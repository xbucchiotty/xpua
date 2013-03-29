package actor

import akka.actor.Actor
import util.Configuration

class CollectionCleanerActor extends Actor {

  private lazy val db = Configuration.db

  def receive = {
    case Clean(collection) => {
      val mongoCollection = db(collection.name())
      mongoCollection.dropCollection()
      mongoCollection.drop()
      sender ! CollectionCleaned
    }

  }

}

