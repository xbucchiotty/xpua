package model

import com.mongodb.casbah.Imports._
import util.Writer

case class Location(latitude: Double, longitude: Double)

object Location {

  object FromMongo {
    def parse(location: MongoDBObject): Location = {
      Location(location.getAs[Double]("longitude").get, location.getAs[Double]("latitude").get)
    }
  }

  implicit object ToMongo extends Writer[Location, MongoDBObject] {
    def apply(location: Location): MongoDBObject = {
      MongoDBObject("longitude" -> location.longitude,
        "latitude" -> location.latitude)
    }
  }
}