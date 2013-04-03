package actor;

import actor.message.LoadFile;
import akka.actor.UntypedActor;
import util.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Lists.newArrayList;

public class FileReaderActor extends UntypedActor {

    private static final String SEPARATOR = "<SEP>";
    private static final String path = Configuration.additionalFiles;

    private File directory = null;

    @Override
    public void onReceive(Object message) throws Exception {
        //TODO: IMPLEMENTS ME : each lines is a list of tokenized string with separator <SEP>
    }


    @Override
    public void preStart() {
        try {
            directory = new File(getClass().getClassLoader().getResource(path).toURI());
            if (!directory.isDirectory()) {
                throw new IllegalStateException(String.format("%s must exists", path));
            }
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("%s must exists", path));
        }
    }

}
