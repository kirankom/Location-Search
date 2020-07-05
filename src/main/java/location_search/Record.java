package location_search;

import java.util.Iterator;

/**
 * Represents a database record of one user that contains their user ID,
 * timestamps, and location data.
 * 
 * @author Meet Vora
 * @since June 29th 2020
 */
public class Record {

    /** User ID of this user. */
    private long _userID;

    /** First timestamp recorded of this user. */
    private long _firstTimestamp;

    /** All timestamps of this user compressed into a byte array. */
    private byte[] _times;

    /** All coordinates of this user encoded and compressed into a byte array. */
    private byte[] _coordinates;

    public Record(long userID, long firstTimestamp, byte[] times, byte[] coordinates) {
        checkInput(firstTimestamp, times, coordinates);
        checkNumDataPoints(firstTimestamp, times, coordinates);
        this._userID = userID;
        this._firstTimestamp = firstTimestamp;
        this._times = times;
        this._coordinates = coordinates;
    }

    /* ================== Helper Methods ================== */

    /**
     * Ensures that first timestamp is not negative, and that some data is provided
     * in the byte arrays.
     * 
     * @param firstTimestamp first timestamp in data
     * @param times          all timestamps in compressed byte form (GZIP format)
     * @param coordinates    all coordinates in compressed byte form (GZIP format)
     */
    private void checkInput(double firstTimestamp, byte[] times, byte[] coordinates) {
        if (firstTimestamp < 0) {
            throw new IllegalArgumentException("Time must be positive.");
        }
        if (times.length == 0 || coordinates.length == 0) {
            throw new IllegalArgumentException("Timestamp and coordinate arrays must not be empty.");
        }
    }

    /**
     * Ensures that the number of timestamps and coordinates in the compressed times
     * and coordinates arrays respectively are the same.
     * 
     * @param firstTimestamp first timestamp in data
     * @param times          all timestamps in compressed byte form (GZIP format)
     * @param coordinates    all coordinates in compressed byte form (GZIP format)
     */
    private void checkNumDataPoints(long firstTimestamp, byte[] times, byte[] coordinates) {
        Compressor compressor = new Compressor();
        Iterator<Long> timesIter = compressor.decompressTimestamps(times, firstTimestamp).iterator();
        Iterator<Coordinate> coordinateIter = compressor.decompressCoordinates(coordinates).iterator();

        while (timesIter.hasNext() && coordinateIter.hasNext()) {
            timesIter.next();
            coordinateIter.next();
        }

        if (timesIter.hasNext() || coordinateIter.hasNext()) {
            throw new IllegalArgumentException("The number of timestamps must equal the number of coordinates.");
        }
    }

    /* ================== Getter Methods ================== */

    public long getUserID() {
        return _userID;
    }

    public long getFirstTimestamp() {
        return _firstTimestamp;
    }

    public byte[] getTimes() {
        return _times;
    }

    public byte[] getCoordinates() {
        return _coordinates;
    }

}