package actor.message;

import actor.Message;

public class LoadFile implements Message {

    public final String fileName;

    public LoadFile(String fileName) {
        this.fileName = fileName;
    }
}
