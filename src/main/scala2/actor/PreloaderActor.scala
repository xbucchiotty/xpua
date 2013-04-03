package actor

import akka.actor.{ActorRef, Actor}
import akka.pattern.ask
import akka.util.Timeout

import com.mongodb.casbah.Imports._
import scala.concurrent.Future

import base._
import scala.slick.driver.SQLiteDriver.simple._
import file.Location


class PreloaderActor extends Actor {

  implicit val timeout = Timeout(120000)

  import context.dispatcher

  private val artistLoader = context.actorFor("akka://LoadingSystem/user/artistLoader")
  private val fileReader = context.actorFor("akka://LoadingSystem/user/fileReader")

  private val songReader = context.actorFor("akka://LoadingSystem/user/songReader")
  private val termOrTagReader = context.actorFor("akka://LoadingSystem/user/termOrTagReader")
  private val similaritiesReader = context.actorFor("akka://LoadingSystem/user/similaritiesReader")

  private val locationsCollection = context.actorFor("akka://LoadingSystem/user/locations")
  private val similarititesCollection = context.actorFor("akka://LoadingSystem/user/similaritites")
  private val songsCollection = context.actorFor("akka://LoadingSystem/user/songs")
  private val tagsCollection = context.actorFor("akka://LoadingSystem/user/tags")
  private val termsCollection = context.actorFor("akka://LoadingSystem/user/terms")

  private val progressListener = context.actorFor("akka://LoadingSystem/user/progressListener")

  def receive = {
    case Go => {

      progressListener ! StartListener(5)

      val locations: Future[Message] = loadLocations()

      for {
        _ <- loadSongs()
        _ <- loadSimilarities()
        _ <- loadTags()
        _ <- loadTerms()
        _ <- locations
      }
      yield (artistLoader.tell(Go, sender = self))
    }
  }

  def loadLocations(): Future[Message] = {
    val locations = ask(fileReader, LoadFile("subset_artist_location.txt"))
      .mapTo[FileLoaded]
      .map(fileLoaded => fileLoaded.lines.map(Location.apply(_)))

    ProgressListener("locations", progressListener)(for {
      beans <- locations
      status <- ask(locationsCollection, Write(beans)).mapTo[Message]
    } yield (status))
  }

  private def loadSongs(): Future[Message] = {
    load("songs", songReader, songsCollection) {
      implicit session => {
        val query = for (song <- Songs) yield (song)
        query.mapResult(_.toMongo).list()
      }
    }
  }

  private def loadSimilarities(): Future[Message] = {
    load("similarities", similaritiesReader, similarititesCollection) {
      implicit session => {
        val query = for (artist <- ArtistSimilarities) yield (artist)
        query.mapResult(_.toMongo).list()
      }
    }
  }

  private def loadTags(): Future[Message] = {
    load("tags", termOrTagReader, tagsCollection) {
      implicit session => {
        val query = for (tagByArtist <- TagsByArtist) yield (tagByArtist)
        query.mapResult(_.toMongo).list()
      }
    }
  }

  private def loadTerms(): Future[Message] = {
    load("terms", termOrTagReader, termsCollection) {
      implicit session => {
        val query = for (temByArtist <- TermsByArtist) yield (temByArtist)
        query.mapResult(_.toMongo).list()
      }
    }
  }

  private def load(message: String, reader: ActorRef, writer: ActorRef)(all: (Session) => List[MongoDBObject]): Future[Message] = {
    ProgressListener(message, progressListener)(for {
      beans <- ask(reader, Extract(all)).mapTo[Extracted[List[MongoDBObject]]]
      status <- ask(writer, Write(beans.data)).mapTo[Message]
    }
    yield (status))
  }
}