// package location_search;

// import java.io.FileReader;
// import java.io.StringWriter;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Map;

// import com.google.gson.JsonArray;

// import org.json.simple.JSONArray;
// import org.json.simple.JSONObject;
// import org.json.simple.parser.*;
// import org.roaringbitmap.RoaringBitmap;

// public class Parser {

// /**
// * Parses each data point consisting of a timestamp, latitude, and longitude
// * from the file _filename and stores it in the inputted instances.
// *
// * @param writer StringWriter instance that stores encoded String
// * @param times List object containing all timestamps stored in json file
// * @return RoaringBitmap instance that compresses timestamps
// */
// public static RoaringBitmap parser(StringWriter writer, List<Long> times) {
// try {
// Encoder encoder = new Encoder(writer);
// JSONObject jsonObj = (JSONObject) new JSONParser().parse(new
// FileReader(_filename));

// JSONArray jsonArray = (JSONArray) jsonObj.get("locations");

// for (Object obj : jsonArray) {
// JSONObject location = (JSONObject) obj;

// long timestamp = Long.parseLong((String) location.get("timestampMs"));

// double lat = ((Long) location.get("latitudeE7") * 1.0) / 1e7;

// double lon = ((Long) location.get("longitudeE7") * 1.0) / 1e7;

// times.add(timestamp);
// encoder.write(lon, lat);
// }
// } catch (FileNotFoundException e) {
// System.exit(1);
// } catch (IOException e) {
// System.exit(1);
// } catch (ParseException e) {
// System.exit(1);
// }
// _firstTimestamp = times.get(0);
// return DataUtils.addTimestamps(times, _firstTimestamp);
// }
// }