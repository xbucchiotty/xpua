package file;

import com.google.common.base.Function;
import org.jongo.marshall.jackson.oid.ObjectId;

import static java.lang.Double.parseDouble;

public class Location {

    @ObjectId // auto
    private String _id;

    private String locationId;
    private String artistName;
    private Double longitude;
    private Double latitude;

    public Location() {
    }

    public Location(String locationId, String artistName, Double longitude, Double latitude) {
        this.locationId = locationId;
        this.artistName = artistName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static Function<String[], Location> toLocation() {
        return new Function<String[], Location>() {
            @Override
            public Location apply(String[] source) {
                return new Location(source[3], source[4], parseDouble(source[1]), parseDouble(source[2]));
            }
        };
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}