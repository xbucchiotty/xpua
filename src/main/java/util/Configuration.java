package util;

import com.mongodb.DB;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

public abstract class Configuration {

    public static final String additionalFiles = System.getProperty("additionalFiles", "AdditionalFiles/");

    public static final String mongohost = System.getProperty("mongo.host", "localhost");

    public static DB getDb() throws UnknownHostException {
        return new Mongo(mongohost).getDB("xpua");
    }
}
