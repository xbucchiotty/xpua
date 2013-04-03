package actor

import akka.actor.{ActorRef, Actor}
import concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ProgressListenerActor extends Actor {

  private var successCount = 0
  private var errorCount = 0
  private var objective = 0
  private var startTime = System.currentTimeMillis

  private def scale = if ((objective / 20) > 0) (objective / 20) else 1

  def receive = {
    case StartListener(obj) => {
      successCount = 0
      errorCount = 0
      objective = obj
      startTime = System.currentTimeMillis
      printStatus("Starting...")
    }

    case Done(message) => {
      successCount += 1
      printStatus(message)
    }

    case e: Throwable => {
      unhandled(e.getMessage)
    }
  }


  override def unhandled(message: Any) {
    println(s"Error $message")
    errorCount += 1
    printStatus(message.toString)
  }

  def printStatus(message: String) {

    if (totalCount % scale == 0 || totalCount == objective) {
      println("Progression: %3.0f%% %5d/%-5d in %5d(ms) Success: %5d, Error:%5d.\t[%-15s]".format(
        (totalCount.toDouble / objective.toDouble) * 100,
        totalCount,
        objective,
        (System.currentTimeMillis - startTime),
        successCount,
        errorCount,
        message)
      )
    }
  }

  private def totalCount: Int = {
    successCount + errorCount
  }
}

object ProgressListener {
  def apply[U, T <: Future[U]](message: String, progressListener: ActorRef)(t: => Future[U])(implicit ctx: ExecutionContext): Future[U] = {
    t.onComplete {
      case Success(_) => progressListener ! Done(message)
      case Failure(e) => progressListener ! e
    }
    t
  }
}