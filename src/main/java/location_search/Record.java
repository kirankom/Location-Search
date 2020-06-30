package location_search;

public class Record {

    private long _userID;
    private long _firstTimestamp;
    private byte[] _times;
    private byte[] _coordinates;

    public Record(long userID, long firstTimestamp, byte[] times, byte[] coordinates) {
        this._userID = userID;
        this._firstTimestamp = firstTimestamp;
        this._times = times;
        this._coordinates = coordinates;
    }

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