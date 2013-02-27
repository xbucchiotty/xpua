package actor

import slick.session.Database
import util.Configuration
import akka.actor.Actor

class DatabaseReaderActor extends Actor {

  private lazy val additionalFiles = Configuration.additionalFiles

  val driver = "org.sqlite.JDBC"

  def receive = {
    case Extract(databaseName, f) => {
      val database = Database.forURL(databaseUrl(databaseName), driver = driver)
      val session = database.createSession()

      sender ! Extracted(f(session))

      session.close()
    }
  }

  def databaseUrl(databaseName: String): String = {
    "jdbc:sqlite://%s/%s".format(additionalFiles, databaseName)
  }
}
