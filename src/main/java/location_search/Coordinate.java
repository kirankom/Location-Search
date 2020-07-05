package location_search;

/**
 * Represents a lat/long coordinate pair.
 * 
 * @author Meet Vora
 * @since June 29th 2020
 */
public class Coordinate {

    /** Latitude of this coordinate. */
    private double lat;

    /** Longitude of this coordinate. */
    private double lon;

    public Coordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /* ================== Getter Methods ================== */

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "LAT: " + getLat() + "  LON: " + getLon();
    }

}