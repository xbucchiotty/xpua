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


  def receive = {
    //TODO: IMPLEMENTS ME : each lines is a list of tokenized string with separator <SEP>
  }

  override def preStart() {
    directory = new File(getClass.getClassLoader.getResource(additionalFiles).toURI)

    if (!directory.isDirectory) {
      throw new IllegalStateException(s"$additionalFiles path must exists")
    }
  }


}

