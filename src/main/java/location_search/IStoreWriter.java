package location_search;

/**
 * Interface for the StorageWriter class.
 * 
 * @author Meet Vora
 * @since June 29th, 2020
 */
public interface IStoreWriter {

    public void upsertRecord(Record record);

    public Record getRecord(long userID);

    public void commit();

    public void close();

    public Iterable<Coordinate> search(long userID, long startTime, long endTime);
}