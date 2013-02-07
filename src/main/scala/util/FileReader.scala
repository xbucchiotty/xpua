package util

import io.Source._
import java.io.File

case class FileReader(fileName: String) {

  private val sep: String = "<SEP>"
  private val encoding: String = "UTF-8"
  private val additionalFiles: String = Configuration.additionalFiles

  def parseAndApply[T](reader: (Array[String] => T)): List[T] = {
    val linesIterator = fromFile(new File(additionalFiles, fileName), encoding).getLines()

    def transform(it: Iterator[String]): List[T] = {
      if (it.hasNext) {
        reader.apply(linesIterator.next().split(sep)) :: transform(it)
      }
      else {
        Nil
      }
    }
    transform(linesIterator)

  }

}

