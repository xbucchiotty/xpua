package model

import util.Writer
import com.mongodb.casbah.Imports._

case class Artist(id: String, hash: String, trackId: String, name: String, location: Location)

object Artist{


  object ToMongo extends Writer[Artist, MongoDBObject] {
    def apply(artist: Artist): MongoDBObject = {
      MongoDBObject("id" -> artist.id,
        "hash" -> artist.hash,
        "trackId" -> artist.trackId,
        "name" -> artist.name,
      "location"-> Location.ToMongo(artist.location))

    }
  }
}

