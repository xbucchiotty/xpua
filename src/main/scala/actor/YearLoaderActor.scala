package actor

import com.mongodb.casbah.Imports._
import util.MongoCollections
import akka.util.Timeout
import file.YearOfTrack


case class YearLoaderActor(db: MongoDB) extends MongoLoaderActor {

  private lazy val years = db(MongoCollections.years)
  implicit val timeout = Timeout(1000)


  def write() {
    util.FileReader("subset_tracks_per_year.txt").parse {
      year: YearOfTrack => years += year
    }
  }

  protected def mongoCollectionName: String = MongoCollections.years
}
