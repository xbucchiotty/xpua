package model

import scala.slick.driver.SQLiteDriver.simple._

case class TermByArtist(artistId: String, term: String)

object TermsByArtist extends Table[TagByArtist]("artist_term") {
  def artistId = column[String]("artist_id")

  def term = column[String]("term")

  def * = artistId ~ term <>(TagByArtist.apply _, TagByArtist.unapply _)
}

