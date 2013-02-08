import actor.{Go, Worker}
import akka.actor.{Props, ActorSystem}
import com.mongodb.casbah.Imports._
import util.{MongoCollections, Configuration}


object AkkaLoader extends App {
  val system = ActorSystem("LoadingSystem")

  lazy val mongoClient = MongoClient(Configuration.mongohost)
  lazy val db = mongoClient("sixtheam")

  val worker = system.actorOf(Props[Worker], "mainWorker")


  worker ! Go("subset_unique_mbtags.txt", {
    source: Array[String] => MongoDBObject("tag" -> source(0))
  }, db, MongoCollections.tags)

  worker ! Go("subset_unique_terms.txt", {
    source: Array[String] => MongoDBObject("term" -> source(0))
  }, db, MongoCollections.terms)

  worker ! Go("subset_tracks_per_year.txt", {
    source: Array[String] => MongoDBObject("trackId" -> source(1), "year" -> source(0).toInt)
  }, db, MongoCollections.years)

  worker ! Go("subset_artist_location.txt", {
    source: Array[String] => MongoDBObject(
      "locationId" -> source(3),
      "artistName" -> source(4),
      "longitude" -> source(1).toDouble,
      "latitude" -> source(2).toDouble)
  }, db, MongoCollections.locations)

  worker ! Go("subset_unique_tracks.txt", {
    source: Array[String] => MongoDBObject(
      "trackId" -> source(0),
      "song" -> source(1),
      "artistName" -> source(2),
      "title" -> (if (source.size > 3) source(3) else ""))
  }, db, MongoCollections.tracks)
}