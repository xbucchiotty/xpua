package base

import scala.slick.driver.SQLiteDriver.simple._
import com.mongodb.casbah.Imports._
import actor.MongoCollectionActor

case class TermByArtist(artistId: String, term: String) {
  def toMongo: MongoDBObject = {
    MongoDBObject(
      "artist_id" -> artistId,
      "term" -> term)
  }
}

object TermByArtist {

  def byArtistId(artistId: String): MongoDBObject = {
    MongoDBObject("artist_id" -> artistId)
  }

}

object TermsByArtist extends Table[TermByArtist]("artist_term") {
  def artistId = column[String]("artist_id")

  def term = column[String]("term")

  def * = artistId ~ term <>(TermByArtist.apply _, TermByArtist.unapply _)
}


class TermsByArtistCollection extends MongoCollectionActor {
  val name = "terms"

  def indexCollection() {
    coll.ensureIndex("artist_id")
  }
}
