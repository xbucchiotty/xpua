package file

import _root_.util.Reader
import com.mongodb.casbah.Imports._

case class TrackDTO(trackId: String, song: String, artistName: String, title: String)

object TrackDTO {

  implicit object ReaderFromFile extends Reader[TrackDTO] {

    def read(source: Array[String]): TrackDTO = {
      TrackDTO(source(0), source(1), source(2), if (source.size > 3) source(3) else "")
    }
  }

  implicit def toMongo(trackDTO: TrackDTO): DBObject = {
    MongoDBObject("trackId" -> trackDTO.trackId,
      "song" -> trackDTO.song,
      "artistName" -> trackDTO.artistName,
      "title" -> trackDTO.title)
  }

  def byArtistName(artistName: String): MongoDBObject = {
    MongoDBObject("artistName" -> artistName)
  }


}

