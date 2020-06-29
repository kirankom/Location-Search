import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void testEncoding()
            throws InvalidShapeException, IOException, org.json.simple.parser.ParseException, java.text.ParseException {
        DatabaseWriter dw = new DatabaseWriter();
        RoaringBitmap bitmap = dw.parse(filename);

        System.out.println(DataUtils.serializeBitmap(bitmap).length);
        byte[] times = dw.compress((DataUtils.serializeBitmap(bitmap)));
        System.out.println(times.length);

        // System.out.println(Arrays.toString(times));
        // System.out.println();

        System.out.println(dw.decompress(times).length);

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

        DatabaseWriter dw = new DatabaseWriter();
        dw.databaseAdder(12, filename);

        // String json_file =
        // "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/test.json";
        // System.out.println("THIS IS THE ONE!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        // DatabaseWriter dw = new DatabaseWriter(2143423, json_file);

        // System.out.println("START OF PROGRAM!");
        // List<Long> times = new ArrayList<Long>();
        // StringWriter writer = new StringWriter();
        // DatabaseWriter dw = new DatabaseWriter(1234, filename);
        // // RoaringBitmap bitmap = dw.parser(writer, times);
        // RoaringBitmap bitmap = DataUtils.parser(writer, times, filename);
        // byte[] data = DataUtils.serializeBitmap(bitmap);
        // System.out.println("TIMES BEFORE: " + data.length);
        // System.out.println("TIMES AFTER: " + dw.convert(data).length);

        // String encoding = writer.toString();
        // // System.out.println("BEFORE: " + encoding);
        // // System.out.println();
        // System.out.println("ENCODING: " + encoding.getBytes().length);
        // DataUtils.decodeLocation(encoding.getBytes());
        // System.out.println("ENDING!");
        // System.out.println(encoding.equals(new String(encoding.getBytes())));
    }

    // @Test
    // public void testDatabase() throws ClassNotFoundException, SQLException {
    // DatabaseWriter dw = new DatabaseWriter(12345, filename);
    // String databaseName = "location_search";
    // dw.databaseAdder(databaseName);
    // }
}