package actor

import akka.actor.Actor

class CollectionCleanerActor extends Actor {

  def receive = {
    case CleanCollection(db, collection) => {
      val mongoCollection = db(collection.name())
      mongoCollection.dropIndexes()
      mongoCollection.dropCollection()
      mongoCollection.drop()
      sender ! CollectionCleaned
    }
  }

}

