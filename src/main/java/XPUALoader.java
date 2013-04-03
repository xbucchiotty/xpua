import actor.*;
import actor.message.Go;
import akka.actor.*;

public class XPUALoader {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("LoadingSystem");

        system.actorOf(new Props(ProgressListenerActor.class), "progressListener");

        system.actorOf(new Props(FileReaderActor.class), "fileReader");
        system.actorOf(new Props(new DatabaseActorFactory(("subset_track_metadata.db"))), "songReader");
        system.actorOf(new Props(new DatabaseActorFactory(("subset_artist_term.db"))), "termOrTagReader");
        system.actorOf(new Props(new DatabaseActorFactory(("subset_artist_similarity.db"))), "similaritiesReader");

        system.actorOf(new Props(ArtistsCollection.class), "artists");
        system.actorOf(new Props(LocationsCollection.class), "locations");
        system.actorOf(new Props(ArtistSimilaritiesCollection.class), "similaritites");
        system.actorOf(new Props(SongsCollection.class), "songs");
        system.actorOf(new Props(TagsByArtistCollection.class), "tags");
        system.actorOf(new Props(TermsByArtistCollection.class), "terms");

        system.actorOf(new Props(ArtistWriterActor.class), "artistWriter");
        system.actorOf(new Props(ArtistLoaderActor.class), "artistLoader");
        ActorRef preloader = system.actorOf(new Props(PreloaderActor.class), "preloader");

        preloader.tell(Go.singleton);
    }


    private static class DatabaseActorFactory implements UntypedActorFactory {

        private final String databaseName;

        private DatabaseActorFactory(String databaseName) {
            this.databaseName = databaseName;
        }

        @Override
        public Actor create() throws Exception {
            return new DatabaseReaderActor(databaseName);
        }
    }


}
