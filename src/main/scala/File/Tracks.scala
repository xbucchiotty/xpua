package file

import com.mongodb.casbah.Imports._

object Tracks {

  def byArtistName(artistName: String): MongoDBObject = {
    MongoDBObject("artistName" -> artistName)
  }


}

