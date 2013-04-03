package base;

import org.jongo.marshall.jackson.oid.ObjectId;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistSimilarity {

    @ObjectId // auto
    private String _id;

    private String target;

    private String similar;

    public ArtistSimilarity() {
    }

    public ArtistSimilarity(String target, String similar) {
        this.target = target;
        this.similar = similar;
    }

    public static RowMapper<ArtistSimilarity> toArtistSimilarity() {
        return new RowMapper<ArtistSimilarity>() {
            @Override
            public ArtistSimilarity mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new ArtistSimilarity(rs.getString("target"),rs.getString("similar"));
            }
        };
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSimilar() {
        return similar;
    }

    public void setSimilar(String similar) {
        this.similar = similar;
    }
}
