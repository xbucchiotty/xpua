package actor.message;

import actor.Message;

public class Go implements Message {

    public static Go singleton = new Go();

    private Go() {
    }

}
