package actor;

import base.TermByArtist;
import org.jongo.MongoCollection;

public class TermsByArtistCollection extends MongoCollectionActor<TermByArtist> {
    @Override
    protected String getCollectionName() {
        return "terms";
    }

    @Override
    protected void indexCollection(MongoCollection mongoCollection) {
        mongoCollection.ensureIndex("{ artist_id : 1 }");
    }

}
