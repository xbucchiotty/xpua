package actor

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout
import concurrent.Future


class ArtistLoaderActor extends Actor {

  implicit val timeout = Timeout(10000)
  private val readFileTimeout = 30000

  //import context.dispatcher

  def receive = {
    //TODO IMPLEMENTS ME: LOAD THE FILE
    ////TODO IMPLEMENTS ME: INITIALIZE THE PROGRESS LISTENER
    //LOAD LOCATIONS AND THEN SIMILARITIES,TERMS,TAGS ON AFTER EACH OTHER BECAUSE IT'S AN SQLLITE DRIVER
    //WHEN EVERYTHING IS DONE ASK ANOTHER ACTOR TO LOAD ARTISTS ?

    /*
    artist =>  "subset_unique_artists.txt"
    location => "subset_artist_location.txt"
     songs => "subset_track_metadata.db"
     terms or tags => "subset_artist_term.db"
     similarities => "subset_artist_similarity.db"
     */
  }
}