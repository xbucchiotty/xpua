package base;

import org.jongo.marshall.jackson.oid.ObjectId;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Song {

    @ObjectId // auto
    private String _id;

    private String trackId;
    private String title;
    private String songId;
    private String release;
    private String artistId;
    private String artistMbid;
    private String artistName;
    private Double duration;
    private Double artistFamiliarity;
    private Double artistHotttnesss;
    private Integer year;

    public Song() {
    }

    public Song(String trackId, String title, String songId, String release, String artistId, String artistMbid, String artistName, Double duration, Double artistFamiliarity, Double artistHotttnesss, Integer year) {
        this.trackId = trackId;
        this.title = title;
        this.songId = songId;
        this.release = release;
        this.artistId = artistId;
        this.artistMbid = artistMbid;
        this.artistName = artistName;
        this.duration = duration;
        this.artistFamiliarity = artistFamiliarity;
        this.artistHotttnesss = artistHotttnesss;
        this.year = year;
    }

    public static RowMapper<Song> toSong() {
        return new RowMapper<Song>() {
            @Override
            public Song mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Song(
                        rs.getString("track_id"),
                        rs.getString("title"),
                        rs.getString("song_id"),
                        rs.getString("release"),
                        rs.getString("artist_id"),
                        rs.getString("artist_mbid"),
                        rs.getString("artist_name"),
                        rs.getDouble("duration"),
                        rs.getDouble("artist_familiarity"),
                        rs.getDouble("artist_hotttnesss"),
                        rs.getInt("year"));
            }
        };
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistMbid() {
        return artistMbid;
    }

    public void setArtistMbid(String artistMbid) {
        this.artistMbid = artistMbid;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getArtistFamiliarity() {
        return artistFamiliarity;
    }

    public void setArtistFamiliarity(Double artistFamiliarity) {
        this.artistFamiliarity = artistFamiliarity;
    }

    public Double getArtistHotttnesss() {
        return artistHotttnesss;
    }

    public void setArtistHotttnesss(Double artistHotttnesss) {
        this.artistHotttnesss = artistHotttnesss;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
