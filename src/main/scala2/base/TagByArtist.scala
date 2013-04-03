package base

import scala.slick.driver.SQLiteDriver.simple._
import com.mongodb.casbah.Imports._
import actor.MongoCollectionActor

case class TagByArtist(artistId: String, mbtag: String) {
  def toMongo: MongoDBObject = {
    MongoDBObject(
      "artist_id" -> artistId,
      "mbtag" -> mbtag)
  }
}

object TagByArtist {

  def byArtistId(artistId: String): MongoDBObject = {
    MongoDBObject("artist_id" -> artistId)
  }

}

object TagsByArtist extends Table[TagByArtist]("artist_mbtag") {
  def artistId = column[String]("artist_id")

  def tag = column[String]("mbtag")

  def * = artistId ~ tag <>(TagByArtist.apply _, TagByArtist.unapply _)
}

class TagsByArtistCollection extends MongoCollectionActor {
  val name = "tags"

  def indexCollection() {
    coll.ensureIndex("artist_id")
  }
}

