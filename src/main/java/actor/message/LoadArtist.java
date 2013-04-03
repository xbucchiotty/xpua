package actor.message;

import actor.Message;

public class LoadArtist implements Message{
    public final String[] source;

    public LoadArtist(String[] source) {
        this.source = source;
    }
}
