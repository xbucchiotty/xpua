import com.mongodb.casbah.Imports._
import model.{Artist, ArtistDTO, LocationByArtistId}

object Hello {

  lazy val mongoClient = MongoClient("192.168.56.201")
  lazy val dbTemporaire = mongoClient("sixtheam-temp")
  lazy val db = mongoClient("sixtheam")

  lazy val temp_locations = dbTemporaire("location")
  lazy val temp_artists = dbTemporaire("artist")

  def main(args: Array[String]) {

    temp_locations.drop()
    temp_locations.dropCollection()
    temp_locations.dropIndexes()
    temp_artists.drop()
    temp_artists.dropCollection()
    temp_artists.dropIndexes()

    val startLocation = System.currentTimeMillis()
    loadLocations()
    println("Locations: %d in:%s ms".format(temp_locations.size, (System.currentTimeMillis() - startLocation)))

    val startArtist = System.currentTimeMillis()
    loadArtists()
    println("Artists: %d in:%s ms".format(temp_artists.size, (System.currentTimeMillis() - startArtist)))

  }

  private def loadLocations() {
    util.Parser("subset_artist_location.txt").parse {
      location: LocationByArtistId =>
        temp_locations += LocationByArtistId.ToMongo(location)
    }
  }

  private def loadArtists() {
    util.Parser("subset_unique_artists.txt").parse {
      artistDTO: ArtistDTO => {
        val locationFromTemp = temp_locations.findOne(MongoDBObject("artistName" -> artistDTO.name))
        locationFromTemp.map {
          location =>
            val artist = Artist(artistDTO.id, artistDTO.hash, artistDTO.trackId, artistDTO.name, model.Location.FromMongo.parse(location))
            temp_artists += Artist.ToMongo(artist)
            temp_locations.remove(location)
        }
      }

    }
  }


}
