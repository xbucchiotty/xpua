package file

import com.mongodb.casbah.Imports._


object Locations {

  def fromFile: (Array[String]) => MongoDBObject = {
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

