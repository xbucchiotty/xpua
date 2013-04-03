package actor.message;

import actor.Message;

import java.util.List;

public class Extracted<T> implements Message {

    public final List<T> data;

    public Extracted(List<T> data) {
        this.data = data;
    }
}
