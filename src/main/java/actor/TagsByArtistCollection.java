package actor;

import base.TagByArtist;
import org.jongo.MongoCollection;

public class TagsByArtistCollection extends MongoCollectionActor<TagByArtist>{
    @Override
    protected String getCollectionName() {
        return "tags";
    }

    @Override
    protected void indexCollection(MongoCollection mongoCollection) {
        mongoCollection.ensureIndex("{ artist_id : 1 }");
    }
}
