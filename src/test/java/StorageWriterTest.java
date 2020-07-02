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

public class StorageWriterTest extends Tester {

    private Compressor compressor = new Compressor();
    private StorageWriter sw = new StorageWriter();

    @Test
    public void testInsertCmd() {
        Record record1 = new Record(1, 7, new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 });
        sw.upsertRecord(record1);

        Record record2 = new Record(2, 4, new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 });
        sw.upsertRecord(record2);

        Record record3 = new Record(2, 11111, new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 });
        sw.upsertRecord(record3);
        sw.commit();
        System.out.println("DONE!");
    }

    @Test
    public void fullTest() {
        // gets Record from super class
        sw.upsertRecord(record);
        sw.commit();
        System.out.println("STORAGE COMPLETE!");

        Record newRecord = sw.getRecord(userID);
        assertEquals(newRecord.getUserID(), record.getUserID());
        assertEquals(newRecord.getFirstTimestamp(), record.getFirstTimestamp());
        assertArrayEquals(newRecord.getTimes(), record.getTimes());
        assertArrayEquals(newRecord.getCoordinates(), record.getCoordinates());
        System.out.println("CHECK COMPLETE!");
    }
}
// String other = "IF EXISTS (SELECT user_ID FROM " + _tableName + " WHERE
// user_ID = ?) INSERT INTO " + _tableName
// + "(user_ID, first_timestamp, timestamps, coordinates) VALUES (?, ?, ?, ?)
// ELSE UPDATE " + _tableName
// + " SET first_timestamp = ?, timestamps = ?, coordinates = ?" + "WHERE
// user_ID = ?";