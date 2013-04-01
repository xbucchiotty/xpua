package actor

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout
import base._
import com.mongodb.casbah.Imports._
import concurrent.Future
import file.{Location, Artist}


class ArtistWriterActor extends Actor {

  implicit val timeout = Timeout(10000)

  import context.dispatcher

  private val progressListener = context.actorFor("akka://LoadingSystem/user/progressListener")

  private val artistsCollection = context.actorFor("akka://LoadingSystem/user/artists")
  private val locationsCollection = context.actorFor("akka://LoadingSystem/user/locations")
  private val similarititesCollection = context.actorFor("akka://LoadingSystem/user/similaritites")
  private val songsCollection = context.actorFor("akka://LoadingSystem/user/songs")
  private val tagsCollection = context.actorFor("akka://LoadingSystem/user/tags")
  private val termsCollection = context.actorFor("akka://LoadingSystem/user/terms")

  def receive = {
    case LoadArtist(source) => {
      val artist: Artist = Artist(source)

      val locations = locationOf(artist)
      val songs = songsOf(artist)
      val similars = similarsOf(artist)
      val tags = tagsOf(artist)
      val terms = termsOf(artist)

      val details: Future[DBObject] = for {
        location <- locations
        songs <- songs
        similarities <- similars
        tags <- tags
        terms <- terms
      }
      yield {
        val detailBuilder = MongoDBObject.newBuilder

        location.map(detailBuilder += "location" -> _)

        detailBuilder += "songs" -> songs.toList

        detailBuilder += "similarities" -> similarities.toList

        detailBuilder += "mbtags" -> tags.toList

        detailBuilder += "terms" -> terms.toList

        detailBuilder.result()
      }

      details.map(details => artistsCollection.tell(Write(List(artist.toMongo ++ details)), sender = progressListener))

    }
  }

  private def locationOf(artist: Artist): Future[Option[BasicDBObject]] = {
    ask(locationsCollection, FindOne(Location.byArtistName(artist.name))).mapTo[Option[BasicDBObject]]
  }

  private def songsOf(artist: Artist): Future[MongoCursor] = {
    ask(songsCollection, Find(Song.byArtistName(artist.name))).mapTo[MongoCursor]
  }

  private def similarsOf(artist: Artist): Future[MongoCursor] = {
    ask(similarititesCollection, Find(ArtistSimilarity.byTarget(artist.name))).mapTo[MongoCursor]
  }

  private def tagsOf(artist: Artist): Future[MongoCursor] = {
    ask(tagsCollection, Find(TagByArtist.byArtistId(artist.id))).mapTo[MongoCursor]
  }

  private def termsOf(artist: Artist): Future[MongoCursor] = {
    ask(termsCollection, Find(TermByArtist.byArtistId(artist.id))).mapTo[MongoCursor]
  }
}