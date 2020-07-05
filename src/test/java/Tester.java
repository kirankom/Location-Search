import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import location_search.Compressor;
import location_search.Coordinate;
import location_search.Record;

public class Tester {

    String filename = "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/test.json";

    Long userID = 12345L;
    List<Long> times = new ArrayList<Long>();
    List<Coordinate> coordinates = new ArrayList<Coordinate>();
    List<Long> testTimes = setTestTimes();
    List<Coordinate> testCoordinates = setTestCoordinates();
    List<Coordinate> appendCoordinates = setAppendCoordinatesList();
    List<Long> appendTimes = setAppendTimesList();
    Record record = parse();

    public Record parse() {
        // Encoder encoder = new Encoder(_writer);
        Compressor compressor = new Compressor();

        JSONObject jsonObj = null;
        try {
            jsonObj = (JSONObject) new JSONParser().parse(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }

        // should change "locations" to the name of the list in the json file
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

        return new Record(userID, firstTimestamp, compressedTimes, compressedCoordinates);
    }

    @Test
    public void testRecord() {
        Compressor compressor = new Compressor();
        Record test = new Record(1, 2, compressor.compressTimestamps(testTimes, testTimes.get(0)),
                compressor.compressCoordinates(testCoordinates));
        System.out.println("WORKS");
    }

    private List<Long> setTestTimes() {
        List<Long> test = new ArrayList<Long>();
        test.add(1416593801893L);
        test.add(1416593928116L);
        // test.add(1416593928119L);
        // test.add(1416594249921L);
        // test.add(1416594373099L);
        // test.add(1416594497165L);
        // test.add(1416594620993L);
        // test.add(1416594860982L);
        // test.add(1416594984988L);
        return test;
    }

    private List<Long> setAppendTimesList() {
        List<Long> list = new ArrayList<Long>();
        list.add(1416593802000L);
        list.add(1416594984988L);// 1416594984988
        list.add(1416594984988L);// 1416594984988
        list.add(1416594984988L);// 1416594984988
        list.add(1416594984988L);// 1416594984988
        list.add(1416594984988L);// 1416594984988
        list.add(1416606663541L);
        list.add(1416606911766L);
        return list;
    }

    private List<Coordinate> setTestCoordinates() {
        List<Coordinate> test = new ArrayList<Coordinate>();
        test.add(new Coordinate(37.3152817, -122.0486348));
        test.add(new Coordinate(37.3153348, -122.0488006));
        return test;
    }

    private List<Coordinate> setAppendCoordinatesList() {
        List<Coordinate> list = new ArrayList<Coordinate>();
        list.add(new Coordinate(47.2354, -119.236435));
        list.add(new Coordinate(27.2354636, -110.236435));
        list.add(new Coordinate(7.236, -10.2335));
        return list;
    }

}