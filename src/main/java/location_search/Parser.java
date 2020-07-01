// package location_search;

// import java.io.FileReader;
// import java.io.StringWriter;
// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Map;

// import org.json.simple.JSONArray;
// import org.json.simple.JSONObject;
// import org.json.simple.parser.*;
// import org.roaringbitmap.RoaringBitmap;

// public class Parser {

// /**
// * Parses each data point consisting of a timestamp, latitude, and longitude
// * from the file _filename and stores it in the inputted instances.
// *
// * @param filename Name of file to parse
// * @return RoaringBitmap instance that compresses timestamps
// * @throws IOException
// * @throws FileNotFoundException
// * @throws org.json.simple.parser.ParseException
// */
// public Record parse(String filename)
// throws FileNotFoundException, IOException,
// org.json.simple.parser.ParseException {

// List<Record> records = new ArrayList<Record>();

// // Encoder encoder = new Encoder(_writer);
// JSONObject jsonObj = (JSONObject) new JSONParser().parse(new
// FileReader(filename));

// // should change "locations" to whatever the name of the list in the
// // json file is
// JSONArray jsonArray = (JSONArray) jsonObj.get("locations");

// for (Object obj : jsonArray) {
// JSONObject location = (JSONObject) obj;

// long timestamp = Long.parseLong((String) location.get("timestampMs"));

// double lat = ((Long) location.get("latitudeE7") * 1.0) / 1e7;
// double lon = ((Long) location.get("longitudeE7") * 1.0) / 1e7;

// _times.add(timestamp);
// _encoder.write(lon, lat);
// }
// // _firstTimestamp = times.get(0);
// return DataUtils.addTimestamps(_times, _times.get(0));
// }
// }