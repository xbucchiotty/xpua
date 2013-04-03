package actor;

import akka.actor.UntypedActor;
import akka.util.Timeout;

public class ArtistLoaderActor extends UntypedActor {

    private static Timeout readFileTimeout = Timeout.apply(30000);

    @Override
    public void onReceive(Object message) throws Exception {
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
