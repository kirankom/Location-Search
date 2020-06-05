import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonArray;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Parser {
    public static void main(String[] args) throws Exception {
        Parser parser = new Parser();
        String filename = "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/java/test.json";
        // src\test\java\test.json
        parser.run(filename);
    }

    public void run(String filename) throws Exception {
        JSONObject jsonObj = (JSONObject) new JSONParser().parse(new FileReader(filename));

        JSONArray jsonArray = (JSONArray) jsonObj.get("locations");

        for (Object obj : jsonArray) {
            JSONObject location = (JSONObject) obj;

            String timestamp = (String) location.get("timestampMs");
            System.out.println("TIME = " + timestamp);

            long lat = (Long) location.get("latitudeE7");
            System.out.println("Latitude = " + lat);

            long lon = (Long) location.get("longitudeE7");
            System.out.println("Longitude = " + lon);
            System.out.println();
            // String timestamp = (String) location.get("timestampMs");
        }

        // JSONObject jo = (JSONObject) obj;

        // Map address = ((Map) jo.get("address"));
        // Iterator<Map.Entry> iter1 = address.entrySet().iterator();

        // JSONArray ja = (JSONArray) jo.get("locations");

        // Iterator<JSONObject> iter = ja.iterator();
        // return iter;

        // while (iter2.hasNext()) {
        // Iterator<Map.Entry> iter1 = ((Map) iter2.next()).entrySet().iterator();

        // while (iter1.hasNext()) {
        // Map.Entry pair = iter1.next();
        // if (!pair.getKey().equals("accuracy"))
        // System.out.println(pair.getKey() + " : " + pair.getValue());
        // }
        // System.out.println();
        // }

    }
}