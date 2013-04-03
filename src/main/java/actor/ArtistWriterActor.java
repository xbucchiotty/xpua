package actor;

import actor.message.*;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.Future;
import akka.dispatch.Mapper;
import akka.dispatch.OnComplete;
import akka.util.Timeout;
import base.ArtistSimilarity;
import base.Song;
import base.TagByArtist;
import base.TermByArtist;
import file.Artist;
import file.Location;

import static akka.pattern.Patterns.ask;
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
            Artist artist = toArtist(((LoadArtist) message).source);

            /*Future<Location> location = locationOf(artist);
            Future<Iterable<Song>> songs = songsOf(artist);
            Future<Iterable<ArtistSimilarity>> similars = similarsOf(artist);
            Future<Iterable<TagByArtist>> tags = tagsOf(artist);
            Future<Iterable<TermByArtist>> terms = termsOf(artist);*/

            Future<Object> status = ask(artistsCollection, new Write(new Artist[]{artist}), timeout);

            status.onComplete(toProgressListener(artist.getName()));


        } else {
            unhandled(message);
        }
    }

    private Future<Location> locationOf(Artist artist) {
        return ask(locationsCollection, new FindOne<Location>(String.format("{artistName: %s}", artist.getName()), Location.class), timeout)
                .map(new Mapper<Object, Location>() {
                    @Override
                    public Location apply(Object input) {
                        return (Location) input;
                    }
                });
    }

    private Future<Iterable<Song>> songsOf(Artist artist) {
        return ask(songsCollection, new Find<Song>(String.format("{artistName: %s}", artist.getName()), Song.class), timeout)
                .map(new Mapper<Object, Iterable<Song>>() {
                    @Override
                    public Iterable<Song> apply(Object input) {
                        return (Iterable<Song>) input;
                    }
                });
    }

    private Future<Iterable<ArtistSimilarity>> similarsOf(Artist artist) {
        return ask(similarititesCollection, new Find<ArtistSimilarity>(String.format("{target : %s}", artist.getName()), ArtistSimilarity.class), timeout)
                .map(new Mapper<Object, Iterable<ArtistSimilarity>>() {
                    @Override
                    public Iterable<ArtistSimilarity> apply(Object input) {
                        return (Iterable<ArtistSimilarity>) input;
                    }
                });
    }

    private Future<Iterable<TagByArtist>> tagsOf(Artist artist) {
        return ask(tagsCollection, new Find<TagByArtist>(String.format("{artist_id: %s}", artist.getId()), TagByArtist.class), timeout)
                .map(new Mapper<Object, Iterable<TagByArtist>>() {
                    @Override
                    public Iterable<TagByArtist> apply(Object input) {
                        return (Iterable<TagByArtist>) input;
                    }
                });
    }

    private Future<Iterable<TermByArtist>> termsOf(Artist artist) {
        return ask(termsCollection, new Find<TermByArtist>(String.format("{artist_id: %s}", artist.getId()), TermByArtist.class), timeout)
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
