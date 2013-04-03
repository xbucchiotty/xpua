package base;

import org.jongo.marshall.jackson.oid.ObjectId;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TagByArtist {

    @ObjectId // auto
    private String _id;

    private String artist_id;

    private String mbtag;

    public TagByArtist() {
    }

    public TagByArtist(String artist_id, String mbtag) {
        this.artist_id = artist_id;
        this.mbtag = mbtag;
    }

    public static RowMapper<TagByArtist> toTagByArtist() {
        return new RowMapper<TagByArtist>() {
            @Override
            public TagByArtist mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new TagByArtist(rs.getString("artist_id"),rs.getString("mbtag"));
            }
        };
    }

    public String getMbtag() {
        return mbtag;
    }

    public void setMbtag(String mbtag) {
        this.mbtag = mbtag;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
