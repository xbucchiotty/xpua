package file

import com.mongodb.casbah.Imports._

case class ArtistDTO(id: String, mbid: String, trackId: String, name: String) {

  def toMongo(): MongoDBObject = {
    MongoDBObject("id" -> id,
      "mbid" -> mbid,
      "trackId" -> trackId,
      "name" -> name)
  }
}

object ArtistDTO {
  def apply(source: Array[String]): ArtistDTO = ArtistDTO(source(0), source(1), source(2), source(3))
}