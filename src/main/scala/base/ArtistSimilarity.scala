package base

import scala.slick.driver.SQLiteDriver.simple._
import com.mongodb.casbah.Imports._
import actor.MongoCollectionActor


case class ArtistSimilarity(target: String, similar: String) {
  def toMongo: MongoDBObject = {
    MongoDBObject(
      "target" -> target,
      "similar" -> similar)
  }
}


object ArtistSimilarity {

  def byTarget(target: String): MongoDBObject = {
    MongoDBObject("target" -> target)
  }

}

object ArtistSimilarities extends Table[ArtistSimilarity]("similarity") {
  def target = column[String]("target")

  def similar = column[String]("similar")

  def * = target ~ similar <>(ArtistSimilarity.apply _, ArtistSimilarity.unapply _)
}

class ArtistSimilaritiesCollection extends MongoCollectionActor {
  val name = "similaritites"

  def indexCollection() {
    coll.ensureIndex("target")
  }
}