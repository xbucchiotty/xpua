package actor.message;

import actor.Message;

public class Done implements Message {

    public final String message;

    public Done(String message) {
        this.message = message;
    }
}
