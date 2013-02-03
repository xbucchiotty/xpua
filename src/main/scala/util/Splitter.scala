package util

import io.Source._
import java.io.File

case class Splitter (fileName: String){

  private val sep: String = "<SEP>"
  private val encoding: String = "UTF-8"
  private val directory: String = "/Users/xbucchiotty/Downloads/xpua/AdditionalFiles/"

  def parse[T](func: (T => Unit))(implicit reader: Reader[T]) {
    val linesIterator = fromFile(new File(directory, fileName), encoding).getLines()

    while (linesIterator.hasNext) {
      val nextLine = linesIterator.next()
      val obj = reader.read(nextLine.split(sep))

      func.apply(obj)
    }
  }
}
