package file

import _root_.util.Reader
import com.mongodb.casbah.Imports._

case class Track(trackId: String, song: String, artistName: String, title: String)

object Track {

  implicit object ReaderFromFile extends Reader[Track] {

    def read(source: Array[String]): Track = {
      Track(source(0), source(1), source(2), if (source.size > 3) source(3) else "")
    }
  }

  implicit def toMongo(Track: Track): DBObject = {
    MongoDBObject("trackId" -> Track.trackId,
      "song" -> Track.song,
      "artistName" -> Track.artistName,
      "title" -> Track.title)
  }

  def byArtistName(artistName: String): MongoDBObject = {
    MongoDBObject("artistName" -> artistName)
  }


}

