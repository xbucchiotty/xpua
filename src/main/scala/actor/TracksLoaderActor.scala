package actor

import com.mongodb.casbah.Imports._
import util.{FileReader, MongoCollections}
import akka.util.Timeout
import file.TrackDTO

case class TracksLoaderActor(db: MongoDB) extends MongoLoaderActor {

  private lazy val tracks = db(MongoCollections.tracks)
  implicit val timeout = Timeout(1000)

  def write() {
    tracks.createIndex(MongoDBObject("artistName" -> 1))
    FileReader("subset_unique_tracks.txt").parse {
      trackDTO: TrackDTO =>
        tracks += trackDTO
    }
  }

  protected def mongoCollectionName: String = MongoCollections.tracks
}
