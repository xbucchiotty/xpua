package actor

import io.Source._
import java.io.File
import akka.actor.Actor
import util.Configuration

class FileReaderActor extends Actor {

  private val sep: String = "<SEP>"
  private val encoding: String = "UTF-8"
  private val additionalFiles: String = Configuration.additionalFiles

  def receive = {
    case LoadFile(fileName) => {
      sender ! FileLoaded(parse(fileName))
    }
  }

  def parse(fileName: String): List[Array[String]] = {
    val linesIterator = fromFile(new File(additionalFiles, fileName), encoding).getLines()

    (for (line <- linesIterator) yield (linesIterator.next().split(sep))).toList
  }

}

