package actor

import akka.actor.Actor

class CollectionCleanerActor extends Actor {

  def receive = {
    case CleanCollection(db, collection) => {
      println("[CLEAN] : start %s".format(collection.name()))

      val mongoCollection = db(collection.name())
      mongoCollection.dropIndexes()
      mongoCollection.dropCollection()
      mongoCollection.drop()
      sender ! CollectionCleaned

      println("[CLEAN] : end %s".format(collection.name()))
    }
  }

}

