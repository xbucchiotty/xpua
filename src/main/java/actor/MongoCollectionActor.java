package actor;

import actor.message.Done;
import actor.message.Find;
import actor.message.FindOne;
import actor.message.Write;
import akka.actor.UntypedActor;
import com.mongodb.DB;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import util.Configuration;

import java.net.UnknownHostException;

public abstract class MongoCollectionActor<T> extends UntypedActor {

    private MongoCollection collection;

    protected abstract String getCollectionName();

    protected abstract void indexCollection(MongoCollection mongoCollection);


    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Write) {
            collection.insert(((Write) message).objects);
            getSender().tell(new Done(getCollectionName()));
        } else {
            tryFind(message);
        }
    }

    private void tryFind(Object message) {
        if (message instanceof Find) {
            Find<T> find = (Find) message;
            getSender().tell(collection.find(find.query).as(find.clazz));
        } else if (message instanceof FindOne) {
            FindOne<T> findOne = (FindOne<T>) message;
            getSender().tell(collection.findOne(findOne.query).as(findOne.clazz));
        } else {
            unhandled(message);
        }
    }

    @Override
    public void preStart() {
        super.preStart();

        try {
            DB db = Configuration.getDb();
            Jongo jongo = new Jongo(db);
            collection = jongo.getCollection(getCollectionName());
            collection.drop();
            indexCollection(collection);

        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }
}
