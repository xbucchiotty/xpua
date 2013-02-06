package base

import scala.slick.driver.SQLiteDriver.simple._


case class ArtistSimilarity(target: String, similar: String)


object ArtistSimilarities extends Table[ArtistSimilarity]("similarity") {
  def target = column[String]("target")

  def similar = column[String]("similar")

  def * = target ~ similar <>(ArtistSimilarity.apply _, ArtistSimilarity.unapply _)
}