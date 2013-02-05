package model

import com.mongodb.casbah.Imports._

class Artist(val id: String, val hash: String, val trackId: String, val name: String)

object Artist {


  implicit def toMongo(artist: Artist): DBObject = {
    MongoDBObject("id" -> artist.id,
      "hash" -> artist.hash,
      "trackId" -> artist.trackId,
      "name" -> artist.name)

  }

}

