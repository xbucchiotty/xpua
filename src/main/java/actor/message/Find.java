package actor.message;

import actor.Message;

public class Find<T> implements Message {

    public final String query;
    public final Class<T> clazz;

    public Find(String query, Class<T> clazz) {
        this.query = query;
        this.clazz = clazz;
    }
}
