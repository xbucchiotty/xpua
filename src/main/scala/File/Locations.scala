package file

import com.mongodb.casbah.Imports._
import actor.MongoCollectionActor


object Location {

  def apply: (Array[String]) => MongoDBObject = {
    line => {
      MongoDBObject("locationId" -> line(3),
        "artistName" -> line(4),
        "longitude" -> line(1).toDouble,
        "latitude" -> line(2).toDouble)
    }
  }

  def byArtistName(artistName: String): MongoDBObject = {
    MongoDBObject("artistName" -> artistName)
  }
}


class LocationsCollection extends MongoCollectionActor {
  protected val name = "locations"

  def indexCollection() {
  }
}