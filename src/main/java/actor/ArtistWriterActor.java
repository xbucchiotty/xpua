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
import com.google.common.base.Function;
import file.Artist;
import file.Location;

import static akka.dispatch.Futures.sequence;
import static akka.pattern.Patterns.ask;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static file.Artist.toArtist;

public class ArtistWriterActor extends UntypedActor {

    private static final Timeout timeout = Timeout.apply(10000);

    private ActorRef progressListener = getContext().actorFor("akka://LoadingSystem/user/progressListener");

    private ActorRef artistsCollection = getContext().actorFor("akka://LoadingSystem/user/artists");
    private ActorRef locationsCollection = getContext().actorFor("akka://LoadingSystem/user/locations");
    private ActorRef similarititesCollection = getContext().actorFor("akka://LoadingSystem/user/similaritites");
    private ActorRef songsCollection = getContext().actorFor("akka://LoadingSystem/user/songs");
    private ActorRef tagsCollection = getContext().actorFor("akka://LoadingSystem/user/tags");
    private ActorRef termsCollection = getContext().actorFor("akka://LoadingSystem/user/terms");

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof LoadArtist) {
            final Artist artist = toArtist(((LoadArtist) message).source);

            Future<Object> location = locationOf(artist).onSuccess(
                    new OnSuccess<Location>() {
                        @Override
                        public void onSuccess(Location result) throws Throwable {
                            if (result != null) {
                                artist.setLocation(result);
                            }
                        }
                    }
            ).map(new Mapper<Location, Object>() {
            });

            Future<Object> songs = songsOf(artist).onSuccess(
                    new OnSuccess<Iterable<Song>>() {
                        @Override
                        public void onSuccess(Iterable<Song> result) throws Throwable {
                            if (result != null) {
                                artist.setSongs(newArrayList(result));
                            }
                        }
                    }
            ).map(new Mapper<Iterable<Song>, Object>() {
            });

            Future<Object> similars = similarsOf(artist).onSuccess(
                    new OnSuccess<Iterable<ArtistSimilarity>>() {
                        @Override
                        public void onSuccess(Iterable<ArtistSimilarity> result) throws Throwable {
                            if (result != null) {
                                artist.setSimilars(from(result).transform(new Function<ArtistSimilarity, String>() {
                                    @Override
                                    public String apply(ArtistSimilarity artistSimilarity) {
                                        return artistSimilarity != null ? artistSimilarity.getSimilar() : null;
                                    }
                                }).toImmutableList());
                            }
                        }
                    }
            ).map(new Mapper<Iterable<ArtistSimilarity>, Object>() {
            });


            Future<Object> tags = tagsOf(artist).onSuccess(
                    new OnSuccess<Iterable<TagByArtist>>() {
                        @Override
                        public void onSuccess(Iterable<TagByArtist> result) throws Throwable {
                            artist.setTags(from(result).transform(new Function<TagByArtist, String>() {
                                @Override
                                public String apply(TagByArtist tagByArtist) {
                                    return tagByArtist.getMbtag();
                                }
                            }).toImmutableList());
                        }
                    }
            ).map(new Mapper<Iterable<TagByArtist>, Object>() {
            });

            Future<Object> terms = termsOf(artist).onSuccess(
                    new OnSuccess<Iterable<TermByArtist>>() {
                        @Override
                        public void onSuccess(Iterable<TermByArtist> result) throws Throwable {
                            artist.setTerms(from(result).transform(new Function<TermByArtist, String>() {
                                @Override
                                public String apply(TermByArtist termByArtist) {
                                    return termByArtist.getTerm();
                                }
                            }).toImmutableList());
                        }
                    }
            ).map(new Mapper<Iterable<TermByArtist>, Object>() {
            });

            Future<Iterable<Object>> details = sequence(newArrayList(location, songs, similars, tags, location, terms), getContext().dispatcher());

            details.onComplete(new OnComplete() {
                @Override
                public void onComplete(Throwable failure, Object success) throws Throwable {
                    Future status = ask(artistsCollection, new Write(new Artist[]{artist}), timeout);
                    status.onComplete(toProgressListener(artist.getName()));
                }
            });

        } else {
            unhandled(message);
        }
    }

    private Future<Location> locationOf(Artist artist) {
        return ask(locationsCollection, new FindOne<Location>("{artistName: #}", Location.class, artist.getName()), timeout)
                .map(new Mapper<Object, Location>() {
                    @Override
                    public Location apply(Object input) {
                        return (Location) input;
                    }
                });
    }

    private Future<Iterable<Song>> songsOf(Artist artist) {
        return ask(songsCollection, new Find<Song>("{artistName: #}", Song.class, artist.getName()), timeout)
                .map(new Mapper<Object, Iterable<Song>>() {
                    @Override
                    public Iterable<Song> apply(Object input) {
                        return (Iterable<Song>) input;
                    }
                });
    }

    private Future<Iterable<ArtistSimilarity>> similarsOf(Artist artist) {
        return ask(similarititesCollection, new Find<ArtistSimilarity>("{target : #}", ArtistSimilarity.class, artist.getName()), timeout)
                .map(new Mapper<Object, Iterable<ArtistSimilarity>>() {
                    @Override
                    public Iterable<ArtistSimilarity> apply(Object input) {
                        return (Iterable<ArtistSimilarity>) input;
                    }
                });
    }

    private Future<Iterable<TagByArtist>> tagsOf(Artist artist) {
        return ask(tagsCollection, new Find<TagByArtist>("{artist_id: #}", TagByArtist.class, artist.getId()), timeout)
                .map(new Mapper<Object, Iterable<TagByArtist>>() {
                    @Override
                    public Iterable<TagByArtist> apply(Object input) {
                        return (Iterable<TagByArtist>) input;
                    }
                });
    }

    private Future<Iterable<TermByArtist>> termsOf(Artist artist) {
        return ask(termsCollection, new Find<TermByArtist>("{artist_id: #}", TermByArtist.class, artist.getId()), timeout)
                .map(new Mapper<Object, Iterable<TermByArtist>>() {
                    @Override
                    public Iterable<TermByArtist> apply(Object input) {
                        return (Iterable<TermByArtist>) input;
                    }
                });
    }

    private OnComplete<Object> toProgressListener(final String successfulMessage) {
        return new OnComplete<Object>() {
            @Override
            public void onComplete(Throwable failure, Object success) throws Throwable {
                if (failure != null) {
                    progressListener.tell(failure);
                } else {
                    progressListener.tell(new Done(successfulMessage));
                }
            }
        };
    }
}
