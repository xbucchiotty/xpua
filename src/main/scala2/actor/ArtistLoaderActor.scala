package actor

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout
import concurrent.Future


class ArtistLoaderActor extends Actor {

  implicit val timeout = Timeout(10000)
  private val readFileTimeout = 30000

  import context.dispatcher

  private val fileReader = context.actorFor("akka://LoadingSystem/user/fileReader")
  private val artistWriter = context.actorFor("akka://LoadingSystem/user/artistWriter")

  private val progressListener = context.actorFor("akka://LoadingSystem/user/progressListener")

  def receive = {
    case Go => {
      val artists: Future[List[Array[String]]] = for {
        artists <- ask(fileReader, LoadFile("subset_unique_artists.txt"))(Timeout(readFileTimeout))
          .mapTo[FileLoaded]
          .map(_.lines)
      } yield (artists)

      artists.map(artists => {
        progressListener ! StartListener(artists.size)
        artists.map(artist => {
          artistWriter.tell(LoadArtist(artist), sender = progressListener)
        })
      })
    }
  }
}