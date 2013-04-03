package actor;

import file.Artist;
import org.jongo.MongoCollection;

public class ArtistsCollection extends MongoCollectionActor<Artist>{

    @Override
    protected String getCollectionName() {
        return "artists";
    }

    @Override
    protected void indexCollection(MongoCollection mongoCollection) {
        //NOTHING TO DO
    }

}
