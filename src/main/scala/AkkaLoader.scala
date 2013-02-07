import actor.{ProcessInfo, Load, MongoLoaderActor}
import akka.actor._
import com.mongodb.casbah.Imports._
import util.{MongoCollections, CollectionCleanerActor, Configuration}


object AkkaLoader extends App {
  lazy val mongoClient = MongoClient(Configuration.mongohost)
  lazy val db = mongoClient("sixtheam")

  val system = ActorSystem("LoadingSystem")
  val collectionCleanActor = system.actorOf(Props(new CollectionCleanerActor(db)), name = "collectionCleaner")
  val loader = system.actorOf(Props[MongoLoaderActor], name = "mongoLoader")

  val tags = ProcessInfo(db, "subset_unique_mbtags.txt", MongoCollections.tags, {
    source: Array[String] => MongoDBObject("tag" -> source(0))
  })

  val terms = ProcessInfo(db, "subset_unique_terms.txt", MongoCollections.terms, {
    source: Array[String] => MongoDBObject("term" -> source(0))
  })

  val years = ProcessInfo(db, "subset_tracks_per_year.txt", MongoCollections.years, {
    source: Array[String] => MongoDBObject("trackId" -> source(1), "year" -> source(0).toInt)
  })

  val locations = ProcessInfo(db, "subset_artist_location.txt", MongoCollections.locations, {
    source: Array[String] => MongoDBObject(
      "locationId" -> source(3),
      "artistName" -> source(4),
      "longitude" -> source(1).toDouble,
      "latitude" -> source(2).toDouble)
  })

  val tracks = ProcessInfo(db, "subset_unique_tracks.txt", MongoCollections.tracks, {
    source: Array[String] => MongoDBObject(
      "trackId" -> source(0),
      "song" -> source(1),
      "artistName" -> source(2),
      "title" -> (if (source.size > 3) source(3) else ""))
  })

  loader ! Load(tracks)
  loader ! Load(tags)
  loader ! Load(terms)
  loader ! Load(years)
  loader ! Load(locations)


}