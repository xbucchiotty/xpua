package file

import util.Reader
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



