import com.mongodb.casbah.Imports._
import dto.{TrackDTO, LocationDTO, ArtistDTO}
import dto.TrackDTO.toMongo
import dto.LocationDTO.toMongo
import model.Artist.ToMongo
import model.Artist

object Hello {

  lazy val mongoClient = MongoClient("192.168.56.201")
  lazy val dbTemporaire = mongoClient("sixtheamtemp")
  lazy val db = mongoClient("sixtheam")

  lazy val temp_locations = dbTemporaire("location")
  lazy val temp_tracks = dbTemporaire("track")
  lazy val temp_artists = dbTemporaire("artist")

  def main(args: Array[String]) {

    temp_locations.dropIndexes()
    temp_locations.dropCollection()
    temp_locations.drop()

    temp_tracks.dropIndexes()
    temp_tracks.dropCollection()
    temp_tracks.drop()

    temp_artists.dropIndexes()
    temp_artists.dropCollection()
    temp_artists.drop()

    loadLocations()
    loadTracks()

    val startArtist = System.currentTimeMillis()
    loadArtists()
    println("Artists: %d en %s ms".format(temp_artists.size, (System.currentTimeMillis() - startArtist)))

    println("Il reste %d locations et %d tracks".format(temp_locations.size, temp_tracks.size))

  }

  private def loadLocations() {
    util.Parser("subset_artist_location.txt").parse {
      location: LocationDTO =>
        temp_locations += location
    }
  }

  private def loadTracks() {
    util.Parser("subset_unique_tracks.txt").parse {
      trackDTO: TrackDTO =>
        temp_tracks += trackDTO
    }
  }

  private def loadArtists() {
    util.Parser("subset_unique_artists.txt").parse {
      artistDTO: ArtistDTO => {
        val locationFromTemp = temp_locations.findOne(LocationDTO.byArtistName(artistDTO.name))

        val artistDetailBuilder = MongoDBObject.newBuilder

        if (locationFromTemp.isDefined) {
          locationFromTemp.get.remove("artistName")
          artistDetailBuilder += "location" -> locationFromTemp.get
        }

        val tracks = temp_tracks.find(TrackDTO.byArtistName(artistDTO.name))
          .foldLeft(MongoDBList.newBuilder)((tracksElement, track) => {
          temp_tracks.remove(track)
          track.remove("artistName")
          tracksElement += track
          tracksElement
        })

        artistDetailBuilder += "tracks" -> tracks.result()

        val artist = artistDetailBuilder.result() ++ new Artist(artistDTO.id, artistDTO.hash, artistDTO.trackId, artistDTO.name)

        temp_artists += artist
      }

    }
  }

}
