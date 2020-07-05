package location_search;

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
        this._userID = userID;
        this._firstTimestamp = firstTimestamp;
        this._times = times;
        this._coordinates = coordinates;
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