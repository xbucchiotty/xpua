package base

import scala.slick.driver.SQLiteDriver.simple._
import com.mongodb.casbah.Imports._
import actor.MongoCollectionActor


case class Song(trackId: String,
                title: String,
                songId: String,
                release: String,
                artistId: String,
                artistMbid: String,
                artistName: String,
                duration: Double,
                artistFamiliarity: Double,
                artistHotttnesss: Double,
                year: Int) {

  def toMongo: MongoDBObject = {
    MongoDBObject("trackId" -> trackId,
      "title" -> title,
      "songId" -> songId,
      "release" -> release,
      "artistId" -> artistId,
      "artistMbid" -> artistMbid,
      "artistName" -> artistName,
      "duration" -> duration,
      "artistFamiliarity" -> artistFamiliarity,
      "artistHotttnesss" -> artistHotttnesss,
      "year" -> year)

  }
}

object Song {
  def byArtistName(artistName: String): MongoDBObject = {
    MongoDBObject("artistName" -> artistName)
  }
}

object Songs extends Table[Song]("songs") {
  def trackId = column[String]("track_id")

  def title = column[String]("title")

  def songId = column[String]("song_id")

  def release = column[String]("release")

  def artistId = column[String]("artist_id")

  def artistMbid = column[String]("artist_mbid")

  def artistName = column[String]("artist_name")

  def duration = column[Double]("duration")

  def artistFamiliarity = column[Double]("artist_familiarity")

  def artistHotttnesss = column[Double]("artist_hotttnesss")

  def year = column[Int]("year")

  def * = trackId ~ title ~ songId ~ release ~ artistId ~ artistMbid ~ artistName ~ duration ~ artistFamiliarity ~ artistHotttnesss ~ year <>(Song.apply _, Song.unapply _)
}

class SongsCollection extends MongoCollectionActor {
  val name = "songs"

  def indexCollection() {
    coll.ensureIndex("artistName")
  }
}