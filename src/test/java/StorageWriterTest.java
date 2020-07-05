import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import location_search.StorageWriter;
import location_search.Compressor;
import location_search.Record;
import location_search.Coordinate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.roaringbitmap.RoaringBitmap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

public class StorageWriterTest extends Tester {

    private Compressor compressor = new Compressor();
    private StorageWriter sw = new StorageWriter();

    @Test
    public void testUpsert() {
        Record record1 = new Record(1, 7, new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 });
        sw.upsertRecord(record1);

        Record record2 = new Record(2, 4, new byte[] { 7, 8, 9 }, new byte[] { 10, 11, 12 });
        sw.upsertRecord(record2);

        Record record3 = new Record(2, 11111, new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 });
        sw.upsertRecord(record3);
        sw.commit();
        System.out.println("DONE!");
    }

    @Test
    public void testGetRecord() {
        // gets Record from super class
        sw.upsertRecord(record);
        sw.commit();
        System.out.println("STORAGE COMPLETE!");

        Record newRecord = sw.getRecord(userID);
        assertEquals(newRecord.getUserID(), record.getUserID());
        assertEquals(newRecord.getFirstTimestamp(), record.getFirstTimestamp());
        assertArrayEquals(newRecord.getTimes(), record.getTimes());
        assertArrayEquals(newRecord.getCoordinates(), record.getCoordinates());

        // invalid user ID test -- throws IllegalArgExcp, so passes!
        // Record errRecord = sw.getRecord(1477431);

        System.out.println("CHECK COMPLETE!");
    }

    @Test
    public void testSearch() {
        List<Long> searchTimes = new ArrayList<Long>();
        List<Coordinate> searchCo = new ArrayList<Coordinate>();

        searchTimes.add(1416593801893L);
        searchCo.add(new Coordinate(37.3153775, -122.0485567));

        searchTimes.add(1416593928116L);
        searchCo.add(new Coordinate(32.3153775, -117.0485567));

        searchTimes.add(1416594249921L);
        searchCo.add(new Coordinate(27.3153775, -112.0485567));

        // startTime
        searchTimes.add(1416594373099L);
        searchCo.add(new Coordinate(22.3153775, -107.0485567));

        searchTimes.add(1416594497165L);
        searchCo.add(new Coordinate(17.3153775, -102.0485567));

        searchTimes.add(1416594620993L);
        searchCo.add(new Coordinate(12.3153775, -97.0485567));

        searchTimes.add(1416594860982L);
        searchCo.add(new Coordinate(7.3153775, -92.0485567));

        // endTime
        searchTimes.add(1416594984988L);
        searchCo.add(new Coordinate(2.3153775, -87.0485567));

        Record test = new Record(7, searchTimes.get(0), compressor.compressTimestamps(searchTimes, searchTimes.get(0)),
                compressor.compressCoordinates(searchCo));
        sw.upsertRecord(test);
        sw.commit();

        Iterable<Coordinate> searchResult = sw.search(7, 1416594373099L, 1416594984988L);
        for (Coordinate co : searchResult) {
            System.out.println(co);
        }
        System.out.println("DONE");
    }
}
// String other = "IF EXISTS (SELECT user_ID FROM " + _tableName + " WHERE
// user_ID = ?) INSERT INTO " + _tableName
// + "(user_ID, first_timestamp, timestamps, coordinates) VALUES (?, ?, ?, ?)
// ELSE UPDATE " + _tableName
// + " SET first_timestamp = ?, timestamps = ?, coordinates = ?" + "WHERE
// user_ID = ?";