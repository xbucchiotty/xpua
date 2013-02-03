import com.mongodb.casbah.Imports._
import model.{ArtistDTO, LocationByArtistId}

object Hello {

  lazy val mongoClient = MongoClient("192.168.56.201")
  lazy val dbTemporaire = mongoClient("sixtheam-temp")
  lazy val db = mongoClient("sixtheam")

  lazy val locations = dbTemporaire("locations")

  def main(args: Array[String]) {

    loadLocations()
    loadArtists()

    dbTemporaire.dropDatabase()

  }

  private def loadLocations() {
    util.Splitter("subset_artist_location.txt").parse {
      location: LocationByArtistId =>
        locations += LocationByArtistId.ToMongo(location)
        println(locations.size)
    }
  }

  private def loadArtists() {
    util.Splitter("subset_unique_artists.txt").parse {
      artist: ArtistDTO =>
        println(artist)
    }
  }


}
