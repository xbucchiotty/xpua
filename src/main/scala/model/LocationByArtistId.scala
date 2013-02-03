package model

import util.{Writer, Reader}
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._

case class LocationByArtistId(locationId: String, artistName: String, latitude: Double, longitude: Double)

object LocationByArtistId {

  implicit object ReaderFromFile extends Reader[LocationByArtistId] {

    def read(source: Array[String]): LocationByArtistId = {
      LocationByArtistId(source(3), source(4), source(1).toDouble, source(2).toDouble)
    }
  }


  object ToMongo extends Writer[LocationByArtistId, MongoDBObject] {
    def apply(location: LocationByArtistId): MongoDBObject = {
      MongoDBObject("locationId" -> location.locationId,
        "artistName" -> location.artistName,
        "longitude" -> location.longitude,
        "latitude" -> location.latitude)

    }
  }

}