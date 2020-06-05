
//import com.google.maps.android.PolyUtil;
import org.locationtech.spatial4j.io.PolyshapeWriter.Encoder;
import org.locationtech.spatial4j.io.PolyshapeWriter;
import org.locationtech.spatial4j.shape.ShapeFactory.PolygonBuilder;

import org.roaringbitmap.RoaringBitmap;

//import java.io.Writer;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

//public class Polyline implements PolygonBuilder {
public class DataUtils {

    private static final int FIRST_TIMESTAMP = (int) (1416593801893L / 1000);

    // Polyline() {
    // _bitmap = new RoaringBitmap();
    // }

    // stores the values in the bitmap from least to greatest, so can't exactly
    // know which number is the first timestamp just by looking at it.
    // Though can just get the max number and assume that's the first timestamp, cuz
    // it'll take about 30 years for that to not be true for the same user. But it
    // could
    // also be that if their data is more recent, then it wont be an issue.
    // Also, the higher the first timestamp is, the longer this will last

    // public static void main(String[] args) {
    // DataUtils p = new DataUtils();
    // int num = 0b11111110111011010101111000001111;
    // // // System.out.println(num);
    // // System.out.println(Integer.toBinaryString(num));
    // // System.out.println(Integer.toBinaryString(num<<1));
    // // print(num<<1);
    // double[] lats = new double[] { 373153775, 373149079, 373153102, 373377398 };
    // double[] longs = new double[] { -1220485567, -1220485080, -1220487537,
    // -1220413614 };

    // // p.storeLocation(lat, lon, writer);

    // StringWriter writer = new StringWriter();
    // for (int i = 0; i < lats.length; i++) {
    // p.encodeLocation(lats[i], longs[i], writer);
    // }
    // // System.out.println(writer);

    // List<Integer> times = new ArrayList<>();
    // times.add((int) (1504112536574L / 1000));
    // times.add((int) (1549083336929L / 1000));
    // times.add((int) (1559934248000L / 1000));
    // // times.add((int) 1504112536574L/1000);
    // // times.add((int) 1549083336929L/1000);
    // // times.add((int) 1559934248000L/1000);

    // // RoaringBitmap bitmap = p.addTimestamps(times);
    // // System.out.println(Arrays.toString(bitmap.toArray()));

    // // ByteBuffer bbf = p.serializeBitmap(bitmap);
    // // p.deserializeBitmap(bbf.array());
    // }

    static void encodeLocation(double lat, double lon, StringWriter writer) {
        // StringWriter writer = new StringWriter();
        Encoder encoder = new Encoder(writer);
        try {
            encoder.write(lat, lon);
        } catch (IOException e) {
            System.exit(1);
        }
    }

    static String decodeLocation() {
        return "";
    }

    // assumes that the first timestamp is not part of the list
    static RoaringBitmap addTimestamps(List<Long> times, long firstTime) {
        RoaringBitmap bitmap = new RoaringBitmap();

        // Since the first timestamp is a long, need to add it to the bitmap this way
        // bitmap.add(firstTime);
        // System.out.println(firstTime);

        for (int i = 0; i < times.size(); i++) {
            bitmap.add((int) (times.get(i) - firstTime));
        }

        return bitmap;
    }

    static byte[] serializeBitmap(RoaringBitmap bitmap) {
        byte[] data = new byte[bitmap.serializedSizeInBytes()];
        ByteBuffer bbf = ByteBuffer.wrap(data);

        // System.out.println("ONE: " + Arrays.toString(data));
        bitmap.serialize(bbf);
        // System.out.println("TWO: " + Arrays.toString(data));

        return data;
    }

    static RoaringBitmap deserializeBitmap(byte[] data) {
        // System.out.println(bbf.array());
        RoaringBitmap bitmap = new RoaringBitmap();
        try {
            // System.out.println("BEFORE: " + bitmap);
            bitmap.deserialize(ByteBuffer.wrap(data));
            // System.out.println("AFTER: " + bitmap);
        } catch (IOException e) {
            System.exit(2);
        }
        return bitmap;
    }

    // private RoaringBitmap _bitmap;

    // public String encode(double num) {
    //
    // // Step 2: Multiply by 1e5 and round
    // int numE5 = (int) (num * 100000);
    // // int latE5 = latitude * 100000;
    //
    // // Step 3: Take two's complement if necessary
    // if (numE5 < 0) {
    // numE5 = twosComplement(numE5);
    // }
    // // if (latE5 < 0) {
    // // latE5 = twosComplement(latE5);
    // // }
    //
    // // Step 4: Left shift by one digit
    // numE5 = numE5 << 1;
    //
    // // Step 5: Invert if num is negative
    // if (num < 0) {
    // numE5 = ~numE5;
    // }
    //
    // // Step 6:
    // int one = numE5 & 0b11111;
    // int two = numE5 & 0b1111100000;
    // int three = numE5 & 0b111110000000000;
    // int four = numE5 & 0b11111000000000000000;
    // int five = numE5 & 0b1111100000000000000000000;
    //
    // int newNum = one << 25 + two << 20 + three << 15 + four << 10 + five << 5;
    //
    // return "";
    //
    //
    // // if less than 0, take twos complement
    // }

    // public int twosComplement(int num) {
    // return ~num + 1;
    // }

}