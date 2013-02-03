package model

import util.Writer
import com.mongodb.casbah.Imports._

case class Location(latitude: Double, longitude: Double)

object Location{

  implicit object ToMongo extends Writer[Location, MongoDBObject] {
    def apply(location: Location): MongoDBObject = {
      MongoDBObject(
        "longitude" -> location.longitude,
        "latitude" -> location.latitude)

    }
  }
}