package actor.message;

import actor.Message;

public class FindOne<T> implements Message {

    public final String query;
    public final Class<T> clazz;

    public FindOne(String query, Class<T> clazz) {
        this.query = query;
        this.clazz = clazz;
    }
}
