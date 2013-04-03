package actor

import com.mongodb.casbah.Imports.MongoDBObject
import slick.session.Session

sealed trait Message

object Go extends Message

case class LoadFile(fileName: String) extends Message

case class LoadArtist(source: Array[String]) extends Message

case class Extract[T](f: (Session => T)) extends Message

case class Extracted[T](data: T) extends Message

case class FileLoaded(lines: List[Array[String]]) extends Message

case class Write(objects: List[MongoDBObject]) extends Message

case class StartListener(objective: Int) extends Message

case class Find(query: MongoDBObject) extends Message

case class FindOne(query: MongoDBObject) extends Message

case class Done(message: String) extends Message