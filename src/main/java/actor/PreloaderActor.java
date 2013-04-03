package actor;

import actor.message.*;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.Future;
import akka.dispatch.Mapper;
import akka.dispatch.OnComplete;
import akka.dispatch.OnSuccess;
import akka.util.Timeout;
import base.ArtistSimilarity;
import base.Song;
import base.TagByArtist;
import base.TermByArtist;
import file.Location;

import java.util.List;

import static base.ArtistSimilarity.toArtistSimilarity;
import static base.Song.toSong;
import static base.TagByArtist.toTagByArtist;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static file.Location.toLocation;
import static akka.pattern.Patterns.ask;
import static akka.dispatch.Futures.sequence;


public class PreloaderActor extends UntypedActor {

    private static final Timeout timeout = Timeout.apply(120000);

    private ActorRef artistLoader = getContext().actorFor("akka://LoadingSystem/user/artistLoader");
    private ActorRef fileReader = getContext().actorFor("akka://LoadingSystem/user/fileReader");

    private ActorRef songReader = getContext().actorFor("akka://LoadingSystem/user/songReader");
    private ActorRef termOrTagReader = getContext().actorFor("akka://LoadingSystem/user/termOrTagReader");
    private ActorRef similaritiesReader = getContext().actorFor("akka://LoadingSystem/user/similaritiesReader");

    private ActorRef locationsCollection = getContext().actorFor("akka://LoadingSystem/user/locations");
    private ActorRef similaritiesCollection = getContext().actorFor("akka://LoadingSystem/user/similaritites");
    private ActorRef songsCollection = getContext().actorFor("akka://LoadingSystem/user/songs");
    private ActorRef tagsCollection = getContext().actorFor("akka://LoadingSystem/user/tags");
    private ActorRef termsCollection = getContext().actorFor("akka://LoadingSystem/user/terms");

    private ActorRef progressListener = getContext().actorFor("akka://LoadingSystem/user/progressListener");

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Go) {
            progressListener.tell(new StartListener(5));

            Future<Message> locations = loadLocations();
            Future<Message> details = loadSongs()
                    .flatMap(new Mapper<Message, Future<Message>>() {
                        @Override
                        public Future<Message> apply(Message parameter) {
                            return loadSimilarities();
                        }
                    }).flatMap(new Mapper<Message, Future<Message>>() {
                        @Override
                        public Future<Message> apply(Message parameter) {
                            return loadTerms();
                        }
                    }).flatMap(new Mapper<Message, Future<Message>>() {
                        @Override
                        public Future<Message> apply(Message parameter) {
                            return loadTags();
                        }
                    });

            Future<Iterable<Message>> status = sequence(newArrayList(locations, details), getContext().dispatcher());

            status.onSuccess(new OnSuccess<Iterable<Message>>() {
                @Override
                public void onSuccess(Iterable<Message> result) throws Throwable {
                    artistLoader.tell(Go.singleton);
                }
            });

        } else {
            unhandled(message);
        }
    }

    private Future<Message> loadLocations() {
        Future<Write> locations = ask(fileReader, new LoadFile("subset_artist_location.txt"), timeout)
                .map(new Mapper<Object, Write>() {
                    @Override
                    public Write apply(Object input) {
                        List<String[]> sources = (List<String[]>) input;

                        return new Write(from(sources)
                                .transform(toLocation())
                                .toArray(Location.class)
                        );
                    }
                });

        return locations.flatMap(new Mapper<Write, Future<Message>>() {
            @Override
            public Future<Message> apply(Write input) {
                return ask(locationsCollection, input, timeout)
                        .map(toMessage())
                        .onComplete(toProgressListener("locations"));
            }
        });
    }

    private Future<Message> loadSongs() {
        return load(songReader, new Extract<Song>("select * from songs", toSong()), songsCollection, "songs");
    }

    private Future<Message> loadSimilarities() {
        return load(similaritiesReader, new Extract<ArtistSimilarity>("select * from similarity", toArtistSimilarity()), similaritiesCollection, "similarities");
    }

    private Future<Message> loadTags() {
        return load(termOrTagReader, new Extract<TagByArtist>("select * from artist_mbtag", toTagByArtist()), tagsCollection, "tags");
    }

    private Future<Message> loadTerms() {
        return load(termOrTagReader, new Extract<TermByArtist>("select * from artist_term", TermByArtist.toTermByArtist()), termsCollection, "terms");
    }


    private Future<Message> load(ActorRef reader, Extract extract, final ActorRef writer, final String message) {
        Future<Write> beans = ask(reader, extract, timeout)
                .map(new Mapper<Object, Write>() {
                    @Override
                    public Write apply(Object input) {
                        return new Write(((Extracted) input).data.toArray());
                    }
                });

        return beans.flatMap(new Mapper<Write, Future<Message>>() {
            @Override
            public Future<Message> apply(Write input) {
                return ask(writer, input, timeout)
                        .map(toMessage())
                        .onComplete(toProgressListener(message));
            }
        });
    }

    private OnComplete<Message> toProgressListener(final String successfulMessage) {
        return new OnComplete<Message>() {
            @Override
            public void onComplete(Throwable failure, Message success) throws Throwable {
                if (failure != null) {
                    progressListener.tell(failure);
                } else {
                    progressListener.tell(new Done(successfulMessage));
                }
            }
        };
    }

    public static Mapper<Object, Message> toMessage() {
        return new Mapper<Object, Message>() {
            @Override
            public Message apply(Object input) {
                return (Message) input;
            }
        };
    }

}
