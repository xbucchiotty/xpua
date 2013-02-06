package model

import com.mongodb.casbah.Imports._
import util.{MongoCollections, CollectionCleaner, Writer}
import dto.LocationDTO
import com.mongodb.casbah.MongoCollection

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

case class Locations(db: MongoDB) {
  def findByArtistName(artistName: String) = {
    val locations = db(MongoCollections.locations)

    locations.findOne(LocationDTO.byArtistName(artistName))
  }

  def load() {
    val locations = db(MongoCollections.locations)

    CollectionCleaner(locations).clean()
    locations.createIndex(MongoDBObject("loc" -> "2d"))

    locations.createIndex(MongoDBObject("artistName" -> 1))

    util.FileReader("subset_artist_location.txt").parse {
      location: LocationDTO => locations += location
    }

    println("[OK] : locations (%s elements)".format(locations.size))
  }
}