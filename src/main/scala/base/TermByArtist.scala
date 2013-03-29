package base

import scala.slick.driver.SQLiteDriver.simple._
import com.mongodb.casbah.Imports._

case class TermByArtist(artistId: String, term: String)

object TermByArtist {
  def toMongo(termByArtist: TermByArtist): MongoDBObject = {
    MongoDBObject(
      "artist_id" -> termByArtist.artistId,
      "term" -> termByArtist.term)
  }

  def byArtistId(artistId: String): MongoDBObject = {
    MongoDBObject("artist_id" -> artistId)
  }
}
object TermsByArtist extends Table[TermByArtist]("artist_term") {
  def artistId = column[String]("artist_id")

  def term = column[String]("term")

  def * = artistId ~ term <>(TermByArtist.apply _, TermByArtist.unapply _)
}

