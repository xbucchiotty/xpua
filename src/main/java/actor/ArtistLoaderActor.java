package actor;

import actor.message.Go;
import actor.message.LoadArtist;
import actor.message.LoadFile;
import actor.message.StartListener;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.Future;
import akka.dispatch.Mapper;
import akka.util.Timeout;

import java.util.Collection;
import java.util.List;

import static akka.pattern.Patterns.ask;

public class ArtistLoaderActor extends UntypedActor {

    private static Timeout readFileTimeout = Timeout.apply(30000);

    private ActorRef fileReader = getContext().actorFor("akka://LoadingSystem/user/fileReader");
    private ActorRef artistWriter = getContext().actorFor("akka://LoadingSystem/user/artistWriter");

    private ActorRef progressListener = getContext().actorFor("akka://LoadingSystem/user/progressListener");

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Go) {
            Future<Collection<String[]>> data = ask(fileReader, new LoadFile("subset_unique_artists.txt"), readFileTimeout)
                    .map(new Mapper<Object, Collection<String[]>>() {
                        @Override
                        public Collection<String[]> apply(Object input) {
                            return (List<String[]>) input;

                        }
                    });

            data.map(new Mapper<Collection<String[]>, Void>() {
                @Override
                public Void apply(Collection<String[]> sources) {
                    progressListener.tell(new StartListener(sources.size()));

                    for (String[] source : sources) {
                        artistWriter.tell(new LoadArtist(source));
                    }

                    return null;
                }
            });
        } else {
            unhandled(message);
        }
    }
}
