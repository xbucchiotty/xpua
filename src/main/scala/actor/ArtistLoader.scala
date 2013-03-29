package actor

import akka.actor.Actor
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.mongodb.casbah.Imports._
import concurrent.Future
import file.{Locations, ArtistDTO}
import util.{Configuration, MongoCollections}
import base._


class ArtistLoader extends Actor {

  implicit val timeout = Timeout(30000)

  import context.dispatcher

  private lazy val db = Configuration.db

  private val fileReader = context.actorFor("akka://LoadingSystem/user/fileReader")
  private val collectionCleaner = context.actorFor("akka://LoadingSystem/user/collectionCleaner")
  private val writer = context.actorFor("akka://LoadingSystem/user/mongoWriter")
  private val progressListener = context.actorFor("akka://LoadingSystem/user/progressListener")

  def receive = {
    case Go => {
      val artists = for {
        cleanStatus <- ask(collectionCleaner, Clean(MongoCollections.artists)).mapTo[Message]
        artists <- ask(fileReader, LoadFile("subset_unique_artists.txt")).mapTo[FileLoaded].map(toArtist(_)) if cleanStatus == CollectionCleaned
      } yield (artists)

      artists.map(artists => {
        progressListener ! StartListener(artists.size)
        artists.map(artist => {
          context.parent ! LoadArtist(artist)
        })
      })
    }

    case LoadArtist(artist) => {

      for {
        location <- findArtistLocation(artist)
        songs <- songsOf(artist)
        similarities <- similarsOf(artist)
        tags <- tagsOf(artist)
        terms <- termsOf(artist)
      }
      yield {
        val artistDetailBuilder = MongoDBObject.newBuilder

        location.map(
          location => {
            artistDetailBuilder += "location" -> location
          }
        )

        songs.map {
          song => {
            artistDetailBuilder += "songs" -> song
          }
        }

        similarities.map {
          similarities => {
            artistDetailBuilder += "similarities" -> similarities
          }
        }

        tags.map {
          tags => {
            artistDetailBuilder += "mbtags" -> tags
          }
        }

        terms.map {
          terms => {
            artistDetailBuilder += "terms" -> terms
          }
        }

        ask(writer, Write(List(artist.toMongo() ++ artistDetailBuilder.result()), MongoCollections.artists)).pipeTo(progressListener)
      }
    }
  }

  private def toArtist(fileLoaded: FileLoaded): List[ArtistDTO] = {
    fileLoaded.lines.map(ArtistDTO(_))
  }

  private def findArtistLocation(artist: ArtistDTO): Future[Option[MongoCollection#T]] = {
    Future.successful(db(MongoCollections.locations.name()).findOne(Locations.byArtistName(artist.name)))
  }

  private def songsOf(artist: ArtistDTO): Future[Option[MongoCollection#T]] = {
    Future.successful(db(MongoCollections.songs.name()).findOne(Song.byArtistName(artist.name)))
  }

  private def similarsOf(artist: ArtistDTO): Future[Option[MongoCollection#T]] = {
    Future.successful(db(MongoCollections.similaritites.name()).findOne(ArtistSimilarity.byTarget(artist.name)))
  }

  private def tagsOf(artist: ArtistDTO): Future[Option[MongoCollection#T]] = {
    Future.successful(db(MongoCollections.tags.name()).findOne(TagByArtist.byArtistId(artist.id)))
  }

  private def termsOf(artist: ArtistDTO): Future[Option[MongoCollection#T]] = {
    Future.successful(db(MongoCollections.terms.name()).findOne(TermByArtist.byArtistId(artist.id)))
  }
}