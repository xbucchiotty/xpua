package actor

import scala.slick.driver.SQLiteDriver.simple._
import util.Configuration
import akka.actor.Actor

case class DatabaseReaderActor(databaseName: String) extends Actor {

  private lazy val additionalFiles = Configuration.additionalFiles

  val driver = "org.sqlite.JDBC"

  private lazy val database = Database.forURL(databaseUrl(databaseName), driver = driver)

  private var session: Session = null

  override def preStart() {
    super.preStart()
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
        session:Session => {
          val result = f(session)

          sender ! Extracted(result)
        }
      }
    }
  }

  def databaseUrl(databaseName: String): String = {
    "jdbc:sqlite://%s/%s".format(additionalFiles, databaseName)
  }
}
