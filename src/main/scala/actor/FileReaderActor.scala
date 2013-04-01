package actor

import io.Source._
import java.io.File
import akka.actor.Actor
import util.Configuration

class FileReaderActor extends Actor {

  private val sep: String = "<SEP>"
  private val encoding: String = "UTF-8"
  private val additionalFiles: String = Configuration.additionalFiles
  private var directory: File = null


  override def preStart() {
    directory = new File(getClass.getClassLoader.getResource(additionalFiles).toURI)

    if (!directory.isDirectory) {
      throw new IllegalStateException(s"$additionalFiles path must exists")
    }
  }

  def receive = {
    case LoadFile(fileName) => {
      val result = parse(fileName)
      sender ! FileLoaded(result)
    }
  }

  def parse(fileName: String): List[Array[String]] = {
    val linesIterator = fromFile(new File(directory, fileName), encoding).getLines()
    (for (line <- linesIterator) yield (linesIterator.next().split(sep))).toList
  }

}

