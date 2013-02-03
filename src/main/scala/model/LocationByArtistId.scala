package model

import util.{Writer, Reader}
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._

case class LocationByArtistId(artistId: String, location: Location)

object LocationByArtistId {

  implicit object ReaderFromFile extends Reader[LocationByArtistId] {

    def read(source: Array[String]): LocationByArtistId = {
      LocationByArtistId(source(3), Location(source(1).toDouble, source(2).toDouble))
    }
  }


  object ToMongo extends Writer[LocationByArtistId, MongoDBObject] {
    def apply(locationDTO: LocationByArtistId): MongoDBObject = {
      MongoDBObject("artistId" -> locationDTO.artistId,
        "location" -> Location.ToMongo(locationDTO.location))

    }
  }

}