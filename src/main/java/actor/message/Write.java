package actor.message;

import actor.Message;

public class Write implements Message {

    public final Object[] objects;

    public Write(Object[] objects) {
        this.objects = objects;
    }
}
