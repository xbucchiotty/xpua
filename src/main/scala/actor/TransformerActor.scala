package actor

import akka.actor.Actor
import com.mongodb.casbah.Imports._

class TransformerActor extends Actor with akka.actor.ActorLogging {

  def receive = {
    case Transform(objects, f) => {
      sender ! Transformed(transform(objects, f))
    }
  }

  def transform[T](objects: List[Array[String]], f: (Array[String] => T)): List[T] = {
    objects.map(x => f.apply(x)).toList
  }

}

