import com.mongodb.casbah.Imports._
import dto._
import model._
import scala.slick.driver.SQLiteDriver.simple._
import slick.session.Database
import Database.threadLocalSession

object Hello {

  lazy val mongoClient = MongoClient("192.168.56.201")
  lazy val dbTemporaire = mongoClient("sixtheamtemp")
  lazy val db = mongoClient("sixtheam")

  lazy val temp_locations = dbTemporaire("location")
  lazy val temp_tracks = dbTemporaire("track")
  lazy val temp_artists = dbTemporaire("artist")
  lazy val temp_year = dbTemporaire("year")
  lazy val temp_tag = dbTemporaire("tag")
  lazy val temp_term = dbTemporaire("term")

  def main(args: Array[String]) {

    cleanCollection(temp_tag)
    loadTags()

    cleanCollection(temp_term)
    loadTerms()

    cleanCollection(temp_locations)
    temp_locations.createIndex(MongoDBObject("artistName" -> 1))
    loadLocations()

    cleanCollection(temp_year)
    temp_year.createIndex(MongoDBObject("trackId" -> 1))
    loadYears()

    cleanCollection(temp_tracks)
    temp_tracks.createIndex(MongoDBObject("artistName" -> 1))
    loadTracks()

    cleanCollection(temp_artists)

    val startArtist = System.currentTimeMillis()
    loadArtists()
    println("Artists: %d en %s ms".format(temp_artists.size, (System.currentTimeMillis() - startArtist)))

  }


  private def loadLocations() {
    util.Parser("subset_artist_location.txt").parse {
      location: LocationDTO =>
        temp_locations += location
    }
  }

  private def loadYears() {
    util.Parser("subset_tracks_per_year.txt").parse {
      year: YearOfTrack =>
        temp_year += year
    }
  }

  private def loadTracks() {
    util.Parser("subset_unique_tracks.txt").parse {
      trackDTO: TrackDTO =>
        temp_tracks += trackDTO
    }
  }

  private def loadTags() {
    util.Parser("subset_unique_mbtags.txt").parse {
      tag: Tag =>
        temp_tag += tag
    }
  }

  private def loadTerms() {
    util.Parser("subset_unique_terms.txt").parse {
      term: Term =>
        temp_term += term
    }
  }

  private def loadArtists() {
    util.Parser("subset_unique_artists.txt").parse {
      artistDTO: ArtistDTO => {
        val locationFromTemp = temp_locations.findOne(LocationDTO.byArtistName(artistDTO.name))

        val artistDetailBuilder = MongoDBObject.newBuilder

        locationFromTemp.map(location => {
          location -= ("artistName")
          artistDetailBuilder += "location" -> location
        })

        val directory = "/Users/xbucchiotty/Downloads/xpua/AdditionalFiles/"


        Database.forURL("jdbc:sqlite://%s%s".format(directory, "subset_track_metadata.db"), driver = "org.sqlite.JDBC").withSession {

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

        Database.forURL("jdbc:sqlite://%s%s".format(directory, "subset_artist_similarity.db"), driver = "org.sqlite.JDBC").withSession {

          val similars: List[String] = (for (artist <- ArtistSimilarities if artist.target === artistDTO.id) yield (artist.similar)).list()

          val similarityList = similars.foldLeft(MongoDBList.newBuilder)((similarity, similar) => {
            similarity += MongoDBObject("id" -> similar)
            similarity
          })

          artistDetailBuilder += "similars" -> similarityList.result()

        }
        Database.forURL("jdbc:sqlite://%s%s".format(directory, "subset_artist_term.db"), driver = "org.sqlite.JDBC").withSession {

          val tags: List[String] = (for (tagByArtist <- TagsByArtist if tagByArtist.artistId === artistDTO.id) yield (tagByArtist.tag)).list()

          val tagList = tags.foldLeft(MongoDBList.newBuilder)((tags, tag) => {
            tags += MongoDBObject("mbtag" -> tag)
            tags
          })

          artistDetailBuilder += "mbtags" -> tagList.result()
        }

        Database.forURL("jdbc:sqlite://%s%s".format(directory, "subset_artist_term.db"), driver = "org.sqlite.JDBC").withSession {

          val terms: List[String] = (for (termByArtist <- TermsByArtist if termByArtist.artistId === artistDTO.id) yield (termByArtist.term)).list()

          val termList = terms.foldLeft(MongoDBList.newBuilder)((terms, term) => {
            terms += MongoDBObject("term" -> term)
            terms
          })

          artistDetailBuilder += "terms" -> termList.result()
        }

        temp_artists += (artistDetailBuilder.result() ++ new Artist(artistDTO.id, artistDTO.mbid, artistDTO.trackId, artistDTO.name))
      }
    }
  }

  private def cleanCollection(collection: MongoCollection) {
    collection.dropIndexes()
    collection.dropCollection()
    collection.drop()
  }

}
