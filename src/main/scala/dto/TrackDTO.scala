package dto

import util.{MongoCollections, CollectionCleaner, Reader}
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

case class Tracks(db: MongoDB) {

  private lazy val tracks = db(MongoCollections.tracks)

  def load() {
    CollectionCleaner(tracks).clean()
    tracks.createIndex(MongoDBObject("artistName" -> 1))
    util.FileReader("subset_unique_tracks.txt").parse {
      trackDTO: TrackDTO =>
        tracks += trackDTO
    }
    println("[OK] : tracks (%s elements)".format(tracks.size))
  }
}