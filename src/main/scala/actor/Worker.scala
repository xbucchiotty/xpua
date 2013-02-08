package actor

import akka.actor.{Props, OneForOneStrategy, Actor}
import akka.actor.SupervisorStrategy.Restart
import akka.pattern.ask
import util.{TransformerActor, FileReaderActor, CollectionCleanerActor}
import akka.util.Timeout

class Worker extends Actor {

  import context.dispatcher

  implicit val timeout = Timeout(1000)

  override val supervisorStrategy = OneForOneStrategy() {
    case _ => Restart
  }

  val fileReader = context.actorOf(Props[FileReaderActor], name = "fileReader")
  val fileTransformer = context.actorOf(Props[TransformerActor], name = "fileTransformer")
  val collectionCleaner = context.actorOf(Props[CollectionCleanerActor], name = "collectionCleaner")
  val loader = context.actorOf(Props[MongoWriterActor], name = "mongoLoader")


  def receive = {
    case Go(fileName, f, db, collection) => {
      ask(fileReader, Load(fileName)) map (readResponse => {
        readResponse match {
          case Loaded(objects) => {
            ask(fileTransformer, Transform(objects, f)) map (transformResponse => {
              transformResponse match {
                case Transformed(objectsForMongo) => {
                  ask(collectionCleaner, Clean(db, collection)) map (cleanResponse => {
                    cleanResponse match {
                      case Cleaned => {
                        loader ! Write(objectsForMongo, db, collection)
                      }
                    }
                  })
                }
              }
            })

          }
        }
      })
    }
  }


}
