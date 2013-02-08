package util

import io.Source._
import java.io.File
import akka.actor.Actor
import actor.{Transformed, Transform, Loaded, Load}
import com.mongodb.casbah.Imports._

class TransformerActor extends Actor {

  def receive = {
    case Transform(objects, f) => {
      println("[TRANSFORM] : start")
      sender ! Transformed(transform(objects, f))
      println("[TRANSFORM] : end")
    }
  }

  def transform(objects: Traversable[Array[String]], f: (Array[String] => MongoDBObject)): Traversable[MongoDBObject] = {
    objects.map(x => f.apply(x))
  }

}

