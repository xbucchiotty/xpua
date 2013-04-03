package actor;

import file.Location;
import org.jongo.MongoCollection;

public class LocationsCollection extends MongoCollectionActor<Location> {
    @Override
    protected String getCollectionName() {
        return "locations";
    }

    @Override
    protected void indexCollection(MongoCollection mongoCollection) {
        mongoCollection.ensureIndex("{artistName : 1}");
    }

}
