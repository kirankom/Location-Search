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
        // sw.commit();

        Record record2 = new Record(2, 4, new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 });
        sw.upsertRecord(record2);

        Record record3 = new Record(2, 1111111, new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 });
        sw.upsertRecord(record3);
        sw.commit();
        System.out.println("DONE!");
    }

    @Test
    public void fullTest() throws FileNotFoundException, IOException, ParseException {
        // Encoder encoder = new Encoder(_writer);
        List<Long> times = new ArrayList<Long>();
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        Long userID = 1234L;

        JSONObject jsonObj = (JSONObject) new JSONParser().parse(new FileReader(filename));

        // should change "locations" to whatever the name of the list in the
        // json file is
        JSONArray jsonArray = (JSONArray) jsonObj.get("locations");

        for (Object obj : jsonArray) {
            JSONObject location = (JSONObject) obj;

            long timestamp = Long.parseLong((String) location.get("timestampMs"));

            double lat = ((Long) location.get("latitudeE7") * 1.0) / 1e7;
            double lon = ((Long) location.get("longitudeE7") * 1.0) / 1e7;

            times.add(timestamp);
            coordinates.add(new Coordinate(lat, lon));
        }
        Long firstTimestamp = times.get(0);
        // Long firstTimestamp = times.get(1) - times.get(0);

        byte[] compressedTimes = compressor.compressTimestamps(times, firstTimestamp);
        byte[] compressedCoordinates = compressor.compressCoordinates(coordinates);

        Record record = new Record(userID, firstTimestamp, compressedTimes, compressedCoordinates);
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