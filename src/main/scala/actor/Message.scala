package actor

import util.MongoCollection
import com.mongodb.casbah.Imports._

sealed trait Message

case class Load(info: ProcessInfo) extends Message

case class Write(info: ProcessInfo) extends Message

case class Clean(name: String) extends Message

object Cleaned extends Message

case class ProcessInfo(db: MongoDB, fileName: String, collection: MongoCollection, toMongo: (Array[String] => MongoDBObject))

