import org.junit.Test;
import location_search.StorageWriter;
import location_search.Record;

public class StorageWriterTest {

    private StorageWriter sw = new StorageWriter();

    @Test
    public void testInsertCmd() {
        Record record = new Record(1, 10, new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 });

        sw.upsertRecord(record);
        // sw.commit();
        System.out.println("DONE!");
    }

}
// String other = "IF EXISTS (SELECT user_ID FROM " + _tableName + " WHERE
// user_ID = ?) INSERT INTO " + _tableName
// + "(user_ID, first_timestamp, timestamps, coordinates) VALUES (?, ?, ?, ?)
// ELSE UPDATE " + _tableName
// + " SET first_timestamp = ?, timestamps = ?, coordinates = ?" + "WHERE
// user_ID = ?";