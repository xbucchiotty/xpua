package actor

import util.MongoCollection
import com.mongodb.casbah.Imports._

sealed trait Message

case class Load(fileName: String) extends Message

case class Loaded(objects: Traversable[Array[String]]) extends Message

case class Transform(objects: Traversable[Array[String]], f: (Array[String] => MongoDBObject)) extends Message

case class Transformed(objects: Traversable[MongoDBObject]) extends Message

case class Write(objects: Traversable[MongoDBObject], db: MongoDB, collection: MongoCollection) extends Message

case class Clean(db: MongoDB, collection: MongoCollection) extends Message

object Cleaned extends Message

case class Go(fileName: String, f: (Array[String] => MongoDBObject), db: MongoDB, collection: MongoCollection) extends Message