package actor

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout

import com.mongodb.casbah.Imports._
import scala.concurrent.Future

import util.{MongoCollections, Configuration}
import base._
import scala.slick.driver.SQLiteDriver.simple._
import file.Locations


class PreloaderActor extends Actor {

  implicit val timeout = Timeout(60000)

  import context.dispatcher

  private val artistLoader = context.actorFor("akka://LoadingSystem/user/artistLoader")
  private val writer = context.actorFor("akka://LoadingSystem/user/mongoWriter")
  private val fileReader = context.actorFor("akka://LoadingSystem/user/fileReader")
  private val collectionCleaner = context.actorFor("akka://LoadingSystem/user/collectionCleaner")

  private val songReader = context.actorFor("akka://LoadingSystem/user/songReader")
  private val termOrTagReader = context.actorFor("akka://LoadingSystem/user/termOrTagReader")
  private val similaritiesReader = context.actorFor("akka://LoadingSystem/user/similaritiesReader")

  def receive = {
    case Go => {
      for {
        locations <- loadLocations()
        songs <- loadSongs()
        similarities <- loadSimilarities()
        tags <- loadTags()
        terms <- loadTerms()
      }
      yield (artistLoader ! Go)
    }
  }

  def loadLocations(): Future[Message] = {
    for {
      _ <- ask(collectionCleaner, Clean(MongoCollections.locations))
      fileLoaded <- ask(fileReader, LoadFile("subset_artist_location.txt")).mapTo[FileLoaded]
      objs <- toMongo(fileLoaded)
      result <- ask(writer, Write(objs, MongoCollections.locations)).mapTo[Message] if result == Done
    } yield (result)
  }

  def toMongo(fileLoaded: FileLoaded): Future[List[MongoDBObject]] = {
    Future.successful(fileLoaded.lines.map(Locations.fromFile(_)))
  }

  private def loadSongs(): Future[Message] = {
    def all(): (Session) => List[MongoDBObject] = {
      implicit session => {
        val query = for (song <- Songs) yield (song)
        query.mapResult(Song.toMongo(_)).list()
      }
    }

    for {
      clean <- ask(collectionCleaner, Clean(MongoCollections.songs))
      terms <- ask(songReader, Extract(all())).mapTo[Extracted[List[MongoDBObject]]]
      status <- ask(writer, Write(terms.data, MongoCollections.songs)).mapTo[Message]
    }
    yield (status)
  }

  private def loadSimilarities(): Future[Message] = {
    def all(): (Session) => List[MongoDBObject] = {
      implicit session => {
        val query = for (artist <- ArtistSimilarities) yield (artist)
        query.mapResult(ArtistSimilarity.toMongo(_)).list()
      }
    }

    for {
      clean <- ask(collectionCleaner, Clean(MongoCollections.similaritites))
      similarities <- ask(similaritiesReader, Extract(all())).mapTo[Extracted[List[MongoDBObject]]]
      status <- ask(writer, Write(similarities.data, MongoCollections.similaritites)).mapTo[Message]
    }
    yield (status)
  }

  private def loadTags(): Future[Message] = {
    def all(): (Session) => List[MongoDBObject] = {
      implicit session => {
        val query = for (tagByArtist <- TagsByArtist) yield (tagByArtist)
        query.mapResult(TagByArtist.toMongo(_)).list()
      }
    }

    for {
      clean <- ask(collectionCleaner, Clean(MongoCollections.tags))
      tags <- ask(termOrTagReader, Extract(all())).mapTo[Extracted[List[MongoDBObject]]]
      status <- ask(writer, Write(tags.data, MongoCollections.tags)).mapTo[Message]
    }
    yield (status)
  }

  private def loadTerms(): Future[Message] = {
    def all: (Session) => List[MongoDBObject] = {
      implicit session => {
        val query = for (temByArtist <- TermsByArtist) yield (temByArtist)
        query.mapResult(TermByArtist.toMongo(_)).list()
      }
    }

    for {
      clean <- ask(collectionCleaner, Clean(MongoCollections.terms))
      terms <- ask(termOrTagReader, Extract(all)).mapTo[Extracted[List[MongoDBObject]]]
      status <- ask(writer, Write(terms.data, MongoCollections.terms)).mapTo[Message]
    }
    yield (status)

  }


}