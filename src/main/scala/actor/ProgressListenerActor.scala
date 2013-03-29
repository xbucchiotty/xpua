package actor

import akka.actor.{PoisonPill, Actor}

class ProgressListenerActor extends Actor {

  private var successCount = 0
  private var errorCount = 0
  private var objective = 0
  private var startTime = System.currentTimeMillis

  def receive = {
    case StartListener(obj) => {
      this.objective = obj
      this.startTime = System.currentTimeMillis
    }

    case Done => {
      successCount += 1
      printStatus()
      stop()
    }
  }


  override def unhandled(message: Any) {
    println(s"Error $message")
    errorCount += 1
    printStatus()
    stop()
  }

  def printStatus() {
    if (totalCount % 50 == 0 || totalCount == objective) {
      println("Progression: %3.0f%% %5d/%5d in %5d(ms) Success: %5d, Error:%5d".format(
        (totalCount.toDouble / objective.toDouble) * 100,
        totalCount,
        objective,
        (System.currentTimeMillis - startTime),
        successCount,
        errorCount)
      )
    }
  }

  def stop() {
    if (totalCount == objective) {
      context.parent ! PoisonPill
    }
  }

  private def totalCount: Int = {
    successCount + errorCount
  }
}
