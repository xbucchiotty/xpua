package file

import com.mongodb.casbah.Imports._


object Locations {

  def byArtistName(artistName: String): MongoDBObject = {
    MongoDBObject("artistName" -> artistName)
  }
}

