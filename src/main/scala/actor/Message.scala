package actor

import util.MongoCollection
import com.mongodb.casbah.Imports._
import slick.session.Session
import file.ArtistDTO

sealed trait Message

object Go extends Message

case class LoadFromFile(fileName: String, f: (Array[String] => MongoDBObject), db: MongoDB, collection: MongoCollection) extends Message

case class LoadFile(fileName: String) extends Message

case class LoadArtist(artist: ArtistDTO) extends Message

case class Extract[T](databaseName: String, f: (Session => T)) extends Message

case class Extracted[T](data: T) extends Message

case class FileLoaded(objects: List[Array[String]]) extends Message

case class Transform[T](objects: List[Array[String]], f: (Array[String] => T)) extends Message

case class Transformed[T](objects: List[T]) extends Message

case class CleanCollection(db: MongoDB, collection: MongoCollection) extends Message

object CollectionCleaned extends Message

case class Write(objects: List[MongoDBObject], db: MongoDB, collection: MongoCollection) extends Message

case class StartListener(objective: Int) extends Message

object Done extends Message
