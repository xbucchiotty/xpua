package dto

import util.{MongoCollections, CollectionCleaner, Reader}
import com.mongodb.casbah.Imports._

case class YearOfTrack(year: Int, trackId: String)


object YearOfTrack {

  def byTrackId(trackId: String): MongoDBObject = {
    MongoDBObject("trackId" -> trackId)
  }


  implicit object ReaderFromFile extends Reader[YearOfTrack] {
    def read(source: Array[String]): YearOfTrack = {
      YearOfTrack(source(0).toInt, source(1))
    }
  }

  implicit def toMongo(yearDTO: YearOfTrack): DBObject = {
    MongoDBObject("trackId" -> yearDTO.trackId,
      "year" -> yearDTO.year)
  }

}

case class Years(db: MongoDB) {

  private lazy val years = db(MongoCollections.years)

  def load() {
    CollectionCleaner(years).clean()
    years.createIndex(MongoDBObject("trackId" -> 1))
    util.FileReader("subset_tracks_per_year.txt").parse {
      year: YearOfTrack => years += year
    }
    println("[OK] : years (%s elements)".format(years.size))
  }
}
