package base;

import org.jongo.marshall.jackson.oid.ObjectId;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TermByArtist {

    @ObjectId // auto
    private String _id;

    private String artist_id;

    private String term;


    public TermByArtist() {

    }

    public TermByArtist(String artist_id, String term) {
        this.artist_id = artist_id;
        this.term = term;
    }

    public static RowMapper<TermByArtist> toTermByArtist() {
        return new RowMapper<TermByArtist>() {
            @Override
            public TermByArtist mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new TermByArtist(rs.getString("artist_id"), rs.getString("term"));
            }
        };
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


}
