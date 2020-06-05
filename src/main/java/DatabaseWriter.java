import java.util.ArrayList;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import org.roaringbitmap.RoaringBitmap;

public class DatabaseWriter {

    public static void main(String[] args) {
        DatabaseWriter dw = new DatabaseWriter(1);
        dw.parser();
    }

    DatabaseWriter(int userID) {
        _userID = userID;
        _times = new ArrayList<>();
        _writer = new StringWriter();
        _firstTimestamp = 0L;
    }

    public void run() {
        // Iterator iter = Parser.run(filename);

    }

    public RoaringBitmap parser() {
        String filename = "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/java/test.json";

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

    private int _userID;
    private Long _firstTimestamp;
    private List<Long> _times;
    private StringWriter _writer;

}