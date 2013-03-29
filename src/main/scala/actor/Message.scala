package actor

import util.MongoCollection
import com.mongodb.casbah.Imports.MongoDBObject
import slick.session.Session
import file.ArtistDTO

sealed trait Message

object Go extends Message

case class LoadFile(fileName: String) extends Message

case class LoadArtist(artist: ArtistDTO) extends Message

case class Extract[T](f: (Session => T)) extends Message

case class Extracted[T](data: T) extends Message

case class FileLoaded(lines: List[Array[String]]) extends Message

case class Clean(collection: MongoCollection) extends Message

object CollectionCleaned extends Message

case class Write(objects: List[MongoDBObject], collection: MongoCollection) extends Message

case class StartListener(objective: Int) extends Message

object Done extends Message