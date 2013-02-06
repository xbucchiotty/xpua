import actor._
import actor.LocationsLoaderActor
import actor.TagsLoaderActor
import actor.TermsLoaderActor
import actor.YearLoaderActor
import akka.actor._
import com.mongodb.casbah.Imports._
import util.CollectionCleaner
import util.Configuration


object AkkaLoader extends App {
  lazy val mongoClient = MongoClient(Configuration.mongohost)
  lazy val db = mongoClient("sixtheam")

  val system = ActorSystem("LoadingSystem")
  val collectionCleanActor = system.actorOf(Props(new CollectionCleaner(db)), name = "collectionCleaner")
  val tagsActor = system.actorOf(Props(new TagsLoaderActor(db)), name = "tags")
  val termsActor = system.actorOf(Props(new TermsLoaderActor(db)), name = "termes")
  val yearsActor = system.actorOf(Props(new YearLoaderActor(db)), name = "years")
  val locationsActor = system.actorOf(Props(new LocationsLoaderActor(db)), name = "locations")
  val tracksActor = system.actorOf(Props(new TracksLoaderActor(db)), name = "tracks")

  while (true) {
    Thread.sleep(30000)
    println("GO")

    tagsActor ! Load
    termsActor ! Load
    yearsActor ! Load
    locationsActor ! Load
    tracksActor ! Load

  }


}