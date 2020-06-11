import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.locationtech.spatial4j.exception.InvalidShapeException;
import org.roaringbitmap.RoaringBitmap;

import location_search.DatabaseWriter;
import location_search.DataUtils;

public class Tester {

    String filename = "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/test.json";

    @Test
    public void testParser() {
        return;
    }

    @Test
    public void testDecoding() {
        try {
            String test = "HELLO WORLD!";
            String newTest = new String(test.getBytes());
            System.out.println("old: " + test);
            System.out.println("new: " + newTest);
            // DataUtils.decodeLocation("TESTINGGG");
        } catch (InvalidShapeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (ParseException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    @Test
    public void fullProgram()
            throws InvalidShapeException, IOException, org.json.simple.parser.ParseException, java.text.ParseException {
        // String json_file =
        // "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/test.json";
        // System.out.println("THIS IS THE ONE!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        // DatabaseWriter dw = new DatabaseWriter(2143423, json_file);
        List<Long> times = new ArrayList<Long>();
        StringWriter writer = new StringWriter();

        // RoaringBitmap bitmap = dw.parser(writer, times);
        RoaringBitmap bitmap = DataUtils.parser(writer, times, filename);
        byte[] data = DataUtils.serializeBitmap(bitmap);

        String encoding = writer.toString();
        // System.out.println("BEFORE: " + encoding);
        // System.out.println();

        DataUtils.decodeLocation(encoding.getBytes());
        // System.out.println(encoding.equals(new String(encoding.getBytes())));
    }

    @Test
    public void testDatabase() throws ClassNotFoundException, SQLException {
        DatabaseWriter dw = new DatabaseWriter(12345, filename);
        dw.createTable();
    }
}