package actor.message;

import actor.Message;

public class Find<T> implements Message {

    public final String query;
    public final Class<T> clazz;
    public final Object[] args;

    public Find(String query, Class<T> clazz, Object... args) {
        this.query = query;
        this.clazz = clazz;
        this.args = args;
    }
}
