package location_search;

/**
 * Represents a lat/long coordinate pair.
 * 
 * @author Meet Vora
 * @since June 29th 2020
 */
public class Coordinate {

    /** Latitude of this coordinate. */
    private double _lat;

    /** Longitude of this coordinate. */
    private double _lon;

    public Coordinate(double lat, double lon) {
        checkLatLon(lat, lon);
        this._lat = lat;
        this._lon = lon;
    }

    /* ================== Helper Methods ================== */

    /**
     * Ensures latitude input falls between -90 and 90 degrees inclusive, and
     * longitude input falls between -180 and 180 degrees inclusive. Otherwise,
     * throws IllegalArgumentException.
     * 
     * @param lat Latitude input
     * @param lon Longitude input
     */
    private void checkLatLon(double lat, double lon) {
        if (lat > 90 || lat < -90) {
            throw new IllegalArgumentException("Latitude input must be between -90 and 90 degrees inclusive.");
        }
        if (lon > 180 || lon < -180) {
            throw new IllegalArgumentException("Longitude input must be between -180 and 180 degrees inclusive.");
        }
    }

    @Override
    public String toString() {
        return "LAT: " + getLat() + "  LON: " + getLon();
    }

    /* ================== Getter Methods ================== */

    public double getLat() {
        return _lat;
    }

    public double getLon() {
        return _lon;
    }

}