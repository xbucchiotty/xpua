package actor

import akka.actor.{PoisonPill, Actor}

class ProgressListenerActor extends Actor {

  private var counter = 0
  private var objective = 0
  private var startTime = System.currentTimeMillis

  def receive = {
    case StartListener(obj) => {
      this.objective = obj
      this.startTime = System.currentTimeMillis
    }

    case Done => {
      counter += 1
      if (counter % 50 == 0) {
        println("Progression: %3.0f%% %5d/%5d".format((counter.toDouble / objective.toDouble) * 100, counter, objective))
      } else {
        if (counter == objective) {
          println("Done loading %s artists in %5d(ms)".format(objective, System.currentTimeMillis - startTime))
          context.parent ! PoisonPill
        }
      }

    }
  }
}
