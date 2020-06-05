// import java.util.Iterator;
// import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class DatabaseWriter {

    public static void main(String[] args) throws Exception {
        DatabaseWriter dw = new DatabaseWriter();
        dw.run();
    }

    public void run() throws Exception {
        // Iterator iter = Parser.run(filename);

    }

    public void parser() {
        String filename = "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/java/test.json";

        JSONObject jsonObj = (JSONObject) new JSONParser().parse(new FileReader(filename));

        JSONArray jsonArray = (JSONArray) jsonObj.get("locations");

        for (Object obj : jsonArray) {
            JSONObject location = (JSONObject) obj;

            long timestamp = Long.parseLong((String) location.get("timestampMs"));
            System.out.println("Time = " + timestamp);

            long lat = (Long) location.get("latitudeE7");
            System.out.println("Latitude = " + lat);

            long lon = (Long) location.get("longitudeE7");
            System.out.println("Longitude = " + lon);
            System.out.println();
            // String timestamp = (String) location.get("timestampMs");
        }

    }

}