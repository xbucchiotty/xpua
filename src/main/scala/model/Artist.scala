package model

import com.mongodb.casbah.Imports._

case class Artist(id: String, hash: String, trackId: String, name: String)

object Artist {


  implicit def toMongo(artist: Artist): DBObject = {
    MongoDBObject("id" -> artist.id,
      "hash" -> artist.hash,
      "trackId" -> artist.trackId,
      "name" -> artist.name)

  }

}

