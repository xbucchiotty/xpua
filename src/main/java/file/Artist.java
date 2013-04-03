package file;

import org.jongo.marshall.jackson.oid.ObjectId;

public class Artist {

    @ObjectId // auto
    private String _id;

    private String id;
    private String mbid;
    private String trackId;
    private String name;

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


    public static Artist toArtist(String[] source) {
        return new Artist(source[0], source[1], source[2], source[3]);
    }
}
