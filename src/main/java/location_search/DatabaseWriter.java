package location_search;

import org.locationtech.spatial4j.exception.InvalidShapeException;
import org.locationtech.spatial4j.shape.Point;

import java.io.StringWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import org.roaringbitmap.RoaringBitmap;

public class DatabaseWriter {

    private int _userID;
    private Long _firstTimestamp;
    private List<Long> _times;
    private StringWriter _writer;

    public static void main(String[] args) {
        DatabaseWriter dw = new DatabaseWriter(1);
        dw.run();
    }

    DatabaseWriter(int userID) {
        _userID = userID;
        _times = new ArrayList<Long>();
        _writer = new StringWriter();
        _firstTimestamp = 0L;
    }

    public void run() {
        // String filename =
        // "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/java/test.json";
        //
        // // userID - ready
        // // first timestamp - ready
        //
        // // byte[] array - ready
        // RoaringBitmap bitmap = parser(filename);
        // byte[] data = DataUtils.serializeBitmap(bitmap);
        //
        // // lat/longs - ready
        // String encoding = _writer.toString();
        // System.out.println("ENCODING: " + encoding);
        //
        StringWriter test = new StringWriter();
        // DataUtils.encodeLocation(41.85103, -97.67786, test);
        DataUtils.encodeLocation(-119.99120, 41.96275, test);
        System.out.println(test);

        String encodedStr = test.toString();

        try {
            Point coordinate = DataUtils.decodeLocation(new StringReader("1ezb_G~wj{U"));
            System.out.println("LONG: " + coordinate.getX() + "LAT: " + coordinate.getY());
        } catch (InvalidShapeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public RoaringBitmap parser(String filename) {
        try {

            JSONObject jsonObj = (JSONObject) new JSONParser().parse(new FileReader(filename));

            JSONArray jsonArray = (JSONArray) jsonObj.get("locations");

            for (Object obj : jsonArray) {
                JSONObject location = (JSONObject) obj;

                long timestamp = Long.parseLong((String) location.get("timestampMs"));
                System.out.println("Time = " + timestamp);

                double lat = ((Long) location.get("latitudeE7") * 1.0) / 1e7;
                System.out.println("Latitude = " + lat);

                double lon = ((Long) location.get("longitudeE7") * 1.0) / 1e7;
                System.out.println("Longitude = " + lon);
                System.out.println();

                _times.add(timestamp);
                DataUtils.encodeLocation(lat, lon, _writer);

                // String timestamp = (String) location.get("timestampMs");
            }
        } catch (FileNotFoundException e) {
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        } catch (ParseException e) {
            System.exit(1);
        }
        _firstTimestamp = _times.get(0);
        return DataUtils.addTimestamps(_times, _firstTimestamp);
    }
}