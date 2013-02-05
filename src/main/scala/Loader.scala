import com.mongodb.casbah.Imports._
import dto._
import model._
import util.Configuration

object Loader {

  lazy val mongoClient = MongoClient(Configuration.mongohost)
  lazy val db = mongoClient("sixtheam")

  def main(args: Array[String]) {

    println("Start loading data into mongo...")

    Tags(db).load()
    Terms(db).load()

    val locations = Locations(db)
    locations.load()

    Years(db).load()
    Tracks(db).load()
    Artists(db, locations).load()

    println("==>DONE")
  }
}
