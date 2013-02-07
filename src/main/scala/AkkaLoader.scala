import actor.{ProcessInfo, Load, MongoLoaderActor}
import akka.actor._
import com.mongodb.casbah.Imports._
import util.{MongoCollections, CollectionCleaner, Configuration}


object AkkaLoader extends App {
  lazy val mongoClient = MongoClient(Configuration.mongohost)
  lazy val db = mongoClient("sixtheam")

  val system = ActorSystem("LoadingSystem")
  val collectionCleanActor = system.actorOf(Props(new CollectionCleaner(db)), name = "collectionCleaner")
  val loader = system.actorOf(Props[MongoLoaderActor], name = "mongoLoader")

  val loadTags = ProcessInfo(
  db,
  "subset_unique_mbtags.txt",
  MongoCollections.tags, {
    source: Array[String] => MongoDBObject("tag" -> source(0))
  })

  loader ! Load(loadTags)

  /*tagsActor ! Load
  termsActor ! Load
  yearsActor ! Load
  locationsActor ! Load
  tracksActor ! Load*/


}