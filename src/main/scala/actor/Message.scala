package actor

sealed trait Message


object Load extends Message
object Write extends Message
case class Clean(name: String) extends Message