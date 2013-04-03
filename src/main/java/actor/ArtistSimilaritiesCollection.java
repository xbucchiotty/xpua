package actor;

import base.ArtistSimilarity;
import org.jongo.MongoCollection;

public class ArtistSimilaritiesCollection extends MongoCollectionActor<ArtistSimilarity>{
    @Override
    protected String getCollectionName() {
        return "similaritites";
    }

    @Override
    protected void indexCollection(MongoCollection mongoCollection) {
        mongoCollection.ensureIndex("{ target : 1 }");
    }

}
