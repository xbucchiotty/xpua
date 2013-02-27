package actor

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout

import com.mongodb.casbah.Imports._
import concurrent.Future

import util.{MongoCollections, Configuration}


class MainWorker extends Actor {

  implicit val timeout = Timeout(10000)

  import context.dispatcher

  private val fileWorker = context.actorFor("akka://LoadingSystem/user/fileWorker")
  private val actorLoader = context.actorFor("akka://LoadingSystem/user/actorLoader")

  def receive = {
    case Go => {
      val futureFilesResponses = for (fileToBeLoad <- filesToBeLoad)
      yield (ask(fileWorker, fileToBeLoad).mapTo[Message])

      Future.sequence(futureFilesResponses).onSuccess {
        case list if list.forall(_ == Done) => actorLoader ! Go
      }
    }
  }

  private lazy val mongoClient = MongoClient(Configuration.mongohost)
  private lazy val db = mongoClient("sixtheam")

  lazy val filesToBeLoad = List(tags, terms, years, locations, tracks)

  lazy val tags = LoadFromFile("subset_unique_mbtags.txt", {
    source: Array[String] => MongoDBObject("tag" -> source(0))
  }, db, MongoCollections.tags)

  lazy val terms = LoadFromFile("subset_unique_terms.txt", {
    source: Array[String] => MongoDBObject("term" -> source(0))
  }, db, MongoCollections.terms)

  lazy val years = LoadFromFile("subset_tracks_per_year.txt", {
    source: Array[String] => MongoDBObject("trackId" -> source(1), "year" -> source(0).toInt)
  }, db, MongoCollections.years)

  lazy val locations = LoadFromFile("subset_artist_location.txt", {
    source: Array[String] => MongoDBObject(
      "locationId" -> source(3),
      "artistName" -> source(4),
      "longitude" -> source(1).toDouble,
      "latitude" -> source(2).toDouble)
  }, db, MongoCollections.locations)

  lazy val tracks = LoadFromFile("subset_unique_tracks.txt", {
    source: Array[String] => MongoDBObject(
      "trackId" -> source(0),
      "song" -> source(1),
      "artistName" -> source(2),
      "title" -> (if (source.size > 3) source(3) else ""))
  }, db, MongoCollections.tracks)

}
