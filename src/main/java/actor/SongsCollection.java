package actor;

import base.Song;
import org.jongo.MongoCollection;

public class SongsCollection extends MongoCollectionActor<Song> {
    @Override
    protected String getCollectionName() {
        return "songs";
    }

    @Override
    protected void indexCollection(MongoCollection mongoCollection) {
        mongoCollection.ensureIndex("{ artistName : 1 }");
    }
}
