package util

trait Reader[T] {

  def read(source: Array[String]): T
}