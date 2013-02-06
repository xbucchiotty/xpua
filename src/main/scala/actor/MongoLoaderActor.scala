package actor

import akka.pattern.{ask, pipe}
import akka.util.Timeout
import akka.actor.Actor

trait MongoLoaderActor extends Actor {

  def receive = {
    case Load => load()
    case Write => {
      write()
      println("Finished with %s".format(self.actorRef.path.toString))
    }
    case x => println("unknown %s", x)
  }

  protected def load() {
    import context.dispatcher
    implicit val timeout = Timeout(1000)
    ask(context.actorFor("../collectionCleaner"), Clean(mongoCollectionName)).mapTo[Message] pipeTo self
  }

  protected def mongoCollectionName: String

  protected def write()

}

