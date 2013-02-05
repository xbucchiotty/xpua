package model

import com.mongodb.casbah.Imports._

class Artist(val id: String, val mbid: String, val trackId: String, val name: String)

object Artist {


  implicit def toMongo(artist: Artist): DBObject = {
    MongoDBObject("id" -> artist.id,
      "mbid" -> artist.mbid,
      "trackId" -> artist.trackId,
      "name" -> artist.name)

  }

}

