package file

import util.MongoCollections
import com.mongodb.casbah.Imports._


object Locations {

  case class Locations(db: MongoDB) {
    def findByArtistName(artistName: String) = {
      db(MongoCollections.locations.name()).findOne(MongoDBObject("artistName" -> artistName))
    }
  }

}

