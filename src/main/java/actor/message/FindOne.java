package actor.message;

import actor.Message;

public class FindOne<T> implements Message {

    public final String query;
    public final Class<T> clazz;
    public final Object[] args;

    public FindOne(String query, Class<T> clazz, Object... args) {
        this.query = query;
        this.clazz = clazz;
        this.args = args;
    }
}
