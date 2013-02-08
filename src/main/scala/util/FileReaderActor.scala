package util

import io.Source._
import java.io.File
import akka.actor.Actor
import actor.{Loaded, Load}

class FileReaderActor extends Actor {

  private val sep: String = "<SEP>"
  private val encoding: String = "UTF-8"
  private val additionalFiles: String = Configuration.additionalFiles

  def receive = {
    case Load(fileName) => {
      println("[READ] : start %s".format(fileName))
      sender ! Loaded(parse(fileName))
      println("[READ] : end %s".format(fileName))
    }
  }

  def parse(fileName: String): Traversable[Array[String]] = {
    val linesIterator = fromFile(new File(additionalFiles, fileName), encoding).getLines()

    var temp = Vector.empty[Array[String]]

    while (linesIterator.hasNext) {
      temp = temp :+ linesIterator.next().split(sep)
    }
    temp
  }

}

