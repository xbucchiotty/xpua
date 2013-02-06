package model

import com.mongodb.casbah.Imports._
import dto.ArtistDTO
import scala.slick.driver.SQLiteDriver.simple._
import slick.session.Database
import Database.threadLocalSession
import util.{DatabaseReader, MongoCollections, CollectionCleaner}


class Artist(val id: String, val mbid: String, val trackId: String, val name: String)

object Artist {


  implicit def toMongo(artist: Artist): DBObject = {
    MongoDBObject("artistId" -> artist.id,
      "mbid" -> artist.mbid,
      "trackId" -> artist.trackId,
      "name" -> artist.name)
  }
}


case class Artists(db: MongoDB, locations: Locations) {

  private lazy val artists = db(MongoCollections.artists)

  def load() {
    CollectionCleaner(artists).clean()

    util.FileReader("subset_unique_artists.txt").parse {
      artistDTO: ArtistDTO => {
        val locationFromTemp = locations.findByArtistName(artistDTO.name)

        val artistDetailBuilder = MongoDBObject.newBuilder

        locationFromTemp.map(location => {
          location -= ("artistName")
          artistDetailBuilder += "location" -> location
        })

        DatabaseReader("subset_track_metadata.db").database.withSession {
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

        DatabaseReader("subset_artist_similarity.db").database.withSession {
          val similars: List[String] = (for (artist <- ArtistSimilarities if artist.target === artistDTO.id) yield (artist.similar)).list()
          val similarityList = similars.foldLeft(MongoDBList.newBuilder)((similarity, similar) => {
            similarity += MongoDBObject("artistId" -> similar)
            similarity
          })

          artistDetailBuilder += "similars" -> similarityList.result()
        }

        DatabaseReader("subset_artist_term.db").database.withSession {
          val tags: List[String] = (for (tagByArtist <- TagsByArtist if tagByArtist.artistId === artistDTO.id) yield (tagByArtist.tag)).list()
          val tagList = tags.foldLeft(MongoDBList.newBuilder)((tags, tag) => {
            tags += MongoDBObject("mbtag" -> tag)
            tags
          })

          artistDetailBuilder += "mbtags" -> tagList.result()
        }

        DatabaseReader("subset_artist_term.db").database.withSession {
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
    println("[OK] : artists (%s elements)".format(artists.size))
  }
}