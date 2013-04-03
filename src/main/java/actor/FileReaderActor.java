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
        if (message instanceof LoadFile) {
            getSender().tell(parse(((LoadFile) message).fileName));
        } else {
            unhandled(message);
        }
    }

    private List<String[]> parse(String fileName) throws IOException {
        File file = new File(directory, fileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        List<String[]> lines = newArrayList();
        while (bufferedReader.ready()) {
            lines.add(toArray(on(SEPARATOR).split(bufferedReader.readLine()), String.class));
        }

        return lines;

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

    /*


  def receive = {
    case LoadFile(fileName) => {
      val result = parse(fileName)
      sender ! FileLoaded(result)
    }
  }

  def parse(fileName: String): List[Array[String]] = {
    val linesIterator = fromFile(new File(directory, fileName), encoding).getLines()
    (for (line <- linesIterator) yield (linesIterator.next().split(sep))).toList
  }
     */

}
