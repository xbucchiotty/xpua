package base

import com.mongodb.casbah.Imports._
import file.{Locations, ArtistDTO}
import scala.slick.driver.SQLiteDriver.simple._
import Database.threadLocalSession
import util.{MongoCollections}


class Artist(val id: String, val mbid: String, val trackId: String, val name: String)

object Artist {


  implicit def toMongo(artist: Artist): DBObject = {
    MongoDBObject("id" -> artist.id,
      "mbid" -> artist.mbid,
      "trackId" -> artist.trackId,
      "name" -> artist.name)
  }
}


case class Artists(db: MongoDB) {

  private lazy val artists = db(MongoCollections.artists.name())

  def load() {

    /*util.FileReader("subset_unique_artists.txt").parseAndApply {
      artistDTO: ArtistDTO => {
        val locationFromTemp = Locations(db).findByArtistName(artistDTO.name)

        val artistDetailBuilder = MongoDBObject.newBuilder

        locationFromTemp.map(location => {
          location -= ("artistName")
          artistDetailBuilder += "location" -> location
        })

        DatabaseReaderActor("subset_track_metadata.db").database.withSession {
          val songs: List[Song] = (for (song <- Songs if song.artistId === artistDTO.id) yield (song)).list()
          val songList = songs.foldLeft(MongoDBList.newBuilder)((songs, song) => {

            val songForMongo = Song.toMongo(song)
            songForMongo -= "artistId"
            songForMongo -= "artistMbid"
            songForMongo -= "artistName"

            songs += songForMongo
          })

          artistDetailBuilder += "songs" -> songList.result()
        }

        DatabaseReaderActor("subset_artist_similarity.db").database.withSession {
          val similars: List[String] = (for (artist <- ArtistSimilarities if artist.target === artistDTO.id) yield (artist.similar)).list()
          val similarityList = similars.foldLeft(MongoDBList.newBuilder)((similarity, similar) => {
            similarity += MongoDBObject("id" -> similar)
            similarity
          })

          artistDetailBuilder += "similars" -> similarityList.result()
        }

        DatabaseReaderActor("subset_artist_term.db").database.withSession {
          val tags: List[String] = (for (tagByArtist <- TagsByArtist if tagByArtist.artistId === artistDTO.id) yield (tagByArtist.tag)).list()
          val tagList = tags.foldLeft(MongoDBList.newBuilder)((tags, tag) => {
            tags += MongoDBObject("mbtag" -> tag)
            tags
          })

          artistDetailBuilder += "mbtags" -> tagList.result()
        }

        DatabaseReaderActor("subset_artist_term.db").database.withSession {
          val terms: List[String] = (for (termByArtist <- TermsByArtist if termByArtist.artistId === artistDTO.id) yield (termByArtist.term)).list()
          val termList = terms.foldLeft(MongoDBList.newBuilder)((terms, term) => {
            terms += MongoDBObject("term" -> term)
            terms
          })

          artistDetailBuilder += "terms" -> termList.result()
        }

        artists += (artistDetailBuilder.result() ++ new Artist(artistDTO.id, artistDTO.mbid, artistDTO.trackId, artistDTO.name))
      }
    }
    println("[OK] : artists (%s elements)".format(artists.size))*/
  }
}