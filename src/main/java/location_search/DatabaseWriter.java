package location_search;

import org.locationtech.spatial4j.exception.InvalidShapeException;
import org.locationtech.spatial4j.io.PolyshapeWriter.Encoder;
import org.locationtech.spatial4j.shape.Point;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import org.roaringbitmap.RoaringBitmap;

/**
 * Grabs and saves the user location data -- timestamps, latitude, and longitude
 * -- into a database
 * 
 * @author Meet Vora
 * @since June 4th, 2020
 */
public class DatabaseWriter {

    /** The userID of this user. */
    private int _userID;
    /** The JSON file path to parse. */
    private String _filename;
    /** The first timestamp recorded in the location data. */
    private Long _firstTimestamp;

    public DatabaseWriter(int userID, String filename) {
        _userID = userID;
        _filename = filename;
        _firstTimestamp = 0L;
    }

    /**
     * Parses each data point consisting of a timestamp, latitude, and longitude
     * from the file _filename and stores it in the inputted instances.
     * 
     * @param writer StringWriter instance that stores encoded String
     * @param times  List object containing all timestamps stored in json file
     * @return RoaringBitmap instance that compresses timestamps
     */
    public RoaringBitmap parser(StringWriter writer, List<Long> times) {
        try {
            Encoder encoder = new Encoder(writer);
            JSONObject jsonObj = (JSONObject) new JSONParser().parse(new FileReader(_filename));

            JSONArray jsonArray = (JSONArray) jsonObj.get("locations");

            for (Object obj : jsonArray) {
                JSONObject location = (JSONObject) obj;

                long timestamp = Long.parseLong((String) location.get("timestampMs"));

                double lat = ((Long) location.get("latitudeE7") * 1.0) / 1e7;

                double lon = ((Long) location.get("longitudeE7") * 1.0) / 1e7;

                times.add(timestamp);
                encoder.write(lon, lat);
            }
        } catch (FileNotFoundException e) {
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        } catch (ParseException e) {
            System.exit(1);
        }
        _firstTimestamp = times.get(0);
        return DataUtils.addTimestamps(times, _firstTimestamp);
    }

    /**
     * Adds the user ID, the first timestamp, the compressed byte array of
     * timestamps, and the encoded latitude/longitude String into a database as one
     * entry.
     */
    public void databaseAdder() {
        String json_file = "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/test.json";
        List<Long> times = new ArrayList<Long>();
        StringWriter writer = new StringWriter();

        RoaringBitmap bitmap = parser(writer, times);
        byte[] data = DataUtils.serializeBitmap(bitmap);

        String encoding = writer.toString();
    }
}