package actor

import util.MongoCollection
import com.mongodb.casbah.Imports._

sealed trait Message

object Go extends Message

case class LoadFromFile(fileName: String, f: (Array[String] => MongoDBObject), db: MongoDB, collection: MongoCollection) extends Message

case class LoadFile(fileName: String) extends Message

case class FileLoaded(objects: List[Array[String]]) extends Message

case class Transform(objects: List[Array[String]], f: (Array[String] => MongoDBObject)) extends Message

case class Transformed(objects: List[MongoDBObject]) extends Message

case class CleanCollection(db: MongoDB, collection: MongoCollection) extends Message

object CollectionCleaned extends Message

case class Write(objects: List[MongoDBObject], db: MongoDB, collection: MongoCollection) extends Message

object Done extends Message
