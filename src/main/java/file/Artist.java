package file;

import base.Song;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.Collection;

public class Artist {

    @ObjectId // auto
    private String _id;

    private String id;
    private String mbid;
    private String trackId;
    private String name;

    private Location location;
    private Collection<Song> songs;
    private Collection<String> similars;
    private Collection<String> terms;
    private Collection<String> tags;

    public Artist() {
    }

    public Artist(String id, String mbid, String trackId, String name) {
        this.id = id;
        this.mbid = mbid;
        this.trackId = trackId;
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Collection<Song> getSongs() {
        return songs;
    }

    public void setSongs(Collection<Song> songs) {
        this.songs = songs;
    }

    public Collection<String> getSimilars() {
        return similars;
    }

    public void setSimilars(Collection<String> similars) {
        this.similars = similars;
    }

    public Collection<String> getTerms() {
        return terms;
    }

    public void setTerms(Collection<String> terms) {
        this.terms = terms;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public static Artist toArtist(String[] source) {
        return new Artist(source[0], source[1], source[2], source[3]);
    }
}
