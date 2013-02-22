package actor

import akka.actor.Actor
import com.mongodb.casbah.Imports._

class TransformerActor extends Actor with akka.actor.ActorLogging {

  def receive = {
    case Transform(objects, f) => {
      println("[TRANSFORM] : start")
      sender ! Transformed(transform(objects, f))
      println("[TRANSFORM] : end")
    }
  }

  def transform(objects: List[Array[String]], f: (Array[String] => MongoDBObject)): List[MongoDBObject] = {
    objects.map(x => f.apply(x)).toList
  }

}

