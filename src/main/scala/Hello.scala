import com.mongodb.casbah.Imports._
import dto._
import model.Artist

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

        val tracks = temp_tracks.find(TrackDTO.byArtistName(artistDTO.name))
          .foldLeft(MongoDBList.newBuilder)((tracksElement, track) => {

          track -= ("artistName")

          track.getAs[String]("trackId").map(trackId => {
            temp_year.findOne(YearOfTrack.byTrackId(trackId)).map(year => {
              year -= ("trackId")
              track ++ year
            })
          })

          tracksElement += track
          tracksElement
        })


        artistDetailBuilder += "tracks" -> tracks.result()

        temp_artists += (artistDetailBuilder.result() ++ new Artist(artistDTO.id, artistDTO.hash, artistDTO.trackId, artistDTO.name))
      }
    }
  }

  private def cleanCollection(collection: MongoCollection) {
    collection.dropIndexes()
    collection.dropCollection()
    collection.drop()
  }

}
