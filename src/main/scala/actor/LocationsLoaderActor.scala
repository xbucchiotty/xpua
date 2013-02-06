package actor

import com.mongodb.casbah.Imports._
import akka.util.Timeout
import file.LocationDTO
import util.{FileReader, MongoCollections}

case class LocationsLoaderActor(db: MongoDB) extends MongoLoaderActor {

  implicit val timeout = Timeout(1000)

  def write() {
    val locationsColl = db(MongoCollections.locations)
    locationsColl.createIndex(MongoDBObject("artistName" -> 1))

    FileReader("subset_artist_location.txt").parse {
      location: LocationDTO => locationsColl += location
    }

  }

  protected def mongoCollectionName: String = MongoCollections.locations
}
