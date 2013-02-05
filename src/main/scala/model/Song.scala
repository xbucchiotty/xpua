package model

import scala.slick.driver.SQLiteDriver.simple._
import com.mongodb.casbah.Imports._


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
}

object Song {
  implicit def toMongo(song: Song): DBObject = {
    MongoDBObject("trackId" -> song.trackId,
      "title" -> song.title,
      "songId" -> song.songId,
      "release" -> song.release,
      "artistId" -> song.artistId,
      "artistMbid" -> song.artistMbid,
      "artistName" -> song.artistName,
      "duration" -> song.duration,
      "artistFamiliarity" -> song.artistFamiliarity,
      "artistHotttnesss" -> song.artistHotttnesss,
      "year" -> song.year)

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
