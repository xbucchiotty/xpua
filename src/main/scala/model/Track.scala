package model

import util.{Writer, Reader}
import com.mongodb.casbah.Imports._

case class Track(trackId: String, unknown: String, title: String)

object Track {

  implicit object ToMongo extends Writer[Track, MongoDBObject] {
    def apply(track: Track): MongoDBObject = {
      MongoDBObject("trackId" -> track.trackId,
        "unknown" -> track.unknown,
        "title" -> track.title)

    }
  }

  object FromMongo {
    def parse(track: MongoDBObject): Track = {
      Track(track.getAs[String]("trackId").get, track.getAs[String]("unknown").get, track.getAs[String]("title").get)
    }
  }

  def byArtistName(artistName: String): MongoDBObject = {
    MongoDBObject("artistName" -> artistName)
  }


}