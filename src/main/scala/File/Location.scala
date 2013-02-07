package file

import _root_.util.{MongoCollections, Reader}
import com.mongodb.casbah.Imports._


case class Location(locationId: String, artistName: String, latitude: Double, longitude: Double)

object Location {

  implicit object ReaderFromFile extends Reader[Location] {
    def read(source: Array[String]): Location = {
      Location(source(3), source(4), source(1).toDouble, source(2).toDouble)
    }
  }

  implicit def toMongo(location: Location): DBObject = {
    MongoDBObject("locationId" -> location.locationId,
      "artistName" -> location.artistName,
      "longitude" -> location.longitude,
      "latitude" -> location.latitude)
  }

}

case class Locations(db: MongoDB) {
  def findByArtistName(artistName: String) = {
    db(MongoCollections.locations.name()).findOne(MongoDBObject("artistName" -> artistName))
  }
}

