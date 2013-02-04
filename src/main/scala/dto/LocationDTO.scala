package dto

import util.Reader
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._

case class LocationDTO(locationId: String, artistName: String, latitude: Double, longitude: Double)

object LocationDTO {

  implicit object ReaderFromFile extends Reader[LocationDTO] {
    def read(source: Array[String]): LocationDTO = {
      LocationDTO(source(3), source(4), source(1).toDouble, source(2).toDouble)
    }
  }

  implicit def toMongo(location: LocationDTO): DBObject = {
    MongoDBObject("locationId" -> location.locationId,
      "artistName" -> location.artistName,
      "longitude" -> location.longitude,
      "latitude" -> location.latitude)
  }

  def byArtistName(artistName: String): MongoDBObject = {
    MongoDBObject("artistName" -> artistName)
  }
}