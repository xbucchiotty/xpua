package util

import akka.actor.Actor
import actor.{Transformed, Transform}
import com.mongodb.casbah.Imports._

class TransformerActor extends Actor with akka.actor.ActorLogging{

  def receive = {
    case Transform(objects, f) => {
      println("[TRANSFORM] : start")
      println("sender %s".format(sender))
      sender ! Transformed(transform(objects, f))
      println("[TRANSFORM] : end")
    }
  }

  def transform(objects: Traversable[Array[String]], f: (Array[String] => MongoDBObject)): List[MongoDBObject] = {
    objects.
      map(x => f.apply(x)).
      toList
  }

}

