package model

import scala.slick.driver.SQLiteDriver.simple._

case class TagByArtist(artistId: String, mbtag: String)

object TagsByArtist extends Table[TagByArtist]("artist_mbtag") {
  def artistId = column[String]("artist_id")

  def tag = column[String]("mbtag")

  def * = artistId ~ tag <>(TagByArtist.apply _, TagByArtist.unapply _)
}

