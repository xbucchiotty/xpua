package actor

import akka.actor.Actor
import com.mongodb.casbah.Imports._
import util.MongoCollection

class MongoWriterActor extends Actor {

  def receive = {
    case Write(objects, db, collection) => {
      println("[WRITE] : start %s".format(collection.name()))
      write(objects, db, collection)
      println("[WRITE]***end %s".format(collection.name()))
    }
    case x => println("Woops %s".format(x))
  }

  def write[T](objects: List[MongoDBObject], db: MongoDB, collection: MongoCollection) {
    objects.map(obj => {
      db(collection.name()) += obj
    })
  }
}

