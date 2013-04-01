package actor

import scala.slick.driver.SQLiteDriver.simple._
import util.Configuration
import akka.actor.Actor
import java.io.File

case class DatabaseReaderActor(databaseName: String) extends Actor {

  val driver = "org.sqlite.JDBC"

  private var session: Session = null
  private var database: Database = null

  override def preStart() {
    super.preStart()

    val additionalFiles = Configuration.additionalFiles
    val directory = new File(getClass.getClassLoader.getResource(additionalFiles).toURI)

    if (!directory.isDirectory) {
      throw new IllegalStateException(s"$additionalFiles path must exists")
    }

    database = Database.forURL(databaseUrl(directory, databaseName), driver = driver)
    session = database.createSession()
  }

  override def postStop() {
    super.postStop()
    if (session != null) {
      session.close()
    }
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    super.preRestart(reason, message)
    if (session != null) {
      session.close()
    }

  }

  override def postRestart(reason: Throwable) {
    super.postRestart(reason)
    session = database.createSession()
  }

  def receive = {
    case Extract(f) => {

      database.withTransaction {
        session: Session => {
          val result = f(session)

          sender ! Extracted(result)
        }
      }
    }
  }

  def databaseUrl(directory: File, databaseName: String): String = {
    "jdbc:sqlite://%s/%s".format(directory.getAbsolutePath, databaseName)
  }
}
