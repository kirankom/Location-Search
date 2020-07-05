
import org.junit.Test;
import static org.junit.Assert.*;

import location_search.StorageWriter;
import location_search.Compressor;
import location_search.Record;
import location_search.Coordinate;

public class FullProgramTest extends Tester {

    StorageWriter sw = new StorageWriter();
    Compressor compressor = new Compressor();

    @Test
    // series of timestamps and lat longs and data gets updated
    public void testDataInsert() {

        sw.upsertRecord(record);

        Record record1 = new Record(1, 7, new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 });
        sw.upsertRecord(record1);

        Record record2 = new Record(2, 4, new byte[] { 7, 8, 9 }, new byte[] { 10, 11, 12 });
        sw.upsertRecord(record2);
        sw.commit();

        System.out.println("Updated!");

        Record getRecord = sw.getRecord(record.getUserID());
        Record getRecord1 = sw.getRecord(record1.getUserID());
        Record getRecord2 = sw.getRecord(record2.getUserID());

        assertArrayEquals(getRecord.getTimes(), record.getTimes());
        assertArrayEquals(getRecord.getCoordinates(), record.getCoordinates());
        assertArrayEquals(getRecord1.getTimes(), record1.getTimes());
        assertArrayEquals(getRecord1.getCoordinates(), record1.getCoordinates());
        assertArrayEquals(getRecord2.getTimes(), record2.getTimes());
        assertArrayEquals(getRecord2.getCoordinates(), record2.getCoordinates());

        for (long time : compressor.decompressTimestamps(record.getTimes(), record.getFirstTimestamp())) {
            System.out.println(time);
        }

        System.out.println("-----------------------------------");

        for (Coordinate co : compressor.decompressCoordinates(record.getCoordinates())) {
            System.out.println(co);
        }

        System.out.println("DONE WITH CHECKS");
    }

    // Updates data with today's data
    // public void test

}