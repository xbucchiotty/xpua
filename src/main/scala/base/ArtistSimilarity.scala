package base

import scala.slick.driver.SQLiteDriver.simple._
import com.mongodb.casbah.Imports._


case class ArtistSimilarity(target: String, similar: String)


object ArtistSimilarity {
  def toMongo(artistSimilarity: ArtistSimilarity): MongoDBObject = {
    MongoDBObject(
      "target" -> artistSimilarity.target,
      "similar" -> artistSimilarity.similar)
  }

  def byTarget(target: String): MongoDBObject = {
    MongoDBObject("target" -> target)
  }
}

object ArtistSimilarities extends Table[ArtistSimilarity]("similarity") {
  def target = column[String]("target")

  def similar = column[String]("similar")

  def * = target ~ similar <>(ArtistSimilarity.apply _, ArtistSimilarity.unapply _)
}