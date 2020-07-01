package location_search;

// IStoreWriter
public interface IStoreWriter {

    public void upsertRecord(Record record);

    public Record getRecord(long userID);

    public void commit();

    public void close();
}