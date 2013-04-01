package file

import com.mongodb.casbah.Imports._
import actor.MongoCollectionActor

case class Artist(id: String, mbid: String, trackId: String, name: String) {
  def toMongo: DBObject = {
    MongoDBObject("id" -> id,
      "mbid" -> mbid,
      "trackId" -> trackId,
      "name" -> name)
  }
}

object Artist {
  def apply(source: Array[String]): Artist = Artist(source(0), source(1), source(2), source(3))
}

class ArtistsCollection extends MongoCollectionActor {
  val name = "artists"

  def indexCollection() {
  }
}
