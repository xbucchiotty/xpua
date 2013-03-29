package base

import scala.slick.driver.SQLiteDriver.simple._
import com.mongodb.casbah.Imports._

case class TagByArtist(artistId: String, mbtag: String)

object TagByArtist {
  def toMongo(tagByArtist: TagByArtist): MongoDBObject = {
    MongoDBObject(
      "artist_id" -> tagByArtist.artistId,
      "mbtag" -> tagByArtist.mbtag)
  }

  def byArtistId(artistId: String): MongoDBObject = {
    MongoDBObject("artist_id" -> artistId)
  }
}

object TagsByArtist extends Table[TagByArtist]("artist_mbtag") {
  def artistId = column[String]("artist_id")

  def tag = column[String]("mbtag")

  def * = artistId ~ tag <>(TagByArtist.apply _, TagByArtist.unapply _)
}

