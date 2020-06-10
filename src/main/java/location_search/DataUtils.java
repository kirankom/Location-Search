package location_search;

import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.impl.BufferedLineString;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.io.PolyshapeWriter.Encoder;
import org.locationtech.spatial4j.io.PolyshapeReader;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.context.SpatialContextFactory;
import org.locationtech.spatial4j.context.SpatialContext;

import org.roaringbitmap.RoaringBitmap;

import java.io.IOException;
import java.text.ParseException;
import org.locationtech.spatial4j.exception.InvalidShapeException;

import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * A collection of utiltiies to help compress, encode, decode, and serialize
 * data.
 * 
 * @author Meet Vora
 * @since June 4th, 2020
 */
public class DataUtils {

    /**
     * Encodes the given latitude and longitude as a String, and stores it in the
     * provided StringWriter instance.
     * 
     * @param lat    Latitude of user
     * @param lon    Longitude of user
     * @param writer StringWriter instance that stores encoded location
     */
    public static void encodeLocation(double lat, double lon, StringWriter writer) {
        // StringWriter writer = new StringWriter();
        Encoder encoder = new Encoder(writer);
        try {
            encoder.write(lon, lat);
        } catch (IOException e) {
            System.exit(1);
        }
    }

    /**
     * Decodes the given encoded String into latitude and longitude values.
     * 
     * @param encodedStr The encoded String
     * @throws InvalidShapeException
     * @throws IOException
     * @throws ParseException
     */
    public static void decodeLocation(String encodedStr) throws InvalidShapeException, IOException, ParseException {
        SpatialContextFactory factory = new SpatialContextFactory();
        SpatialContext s = SpatialContext.GEO;

        PolyshapeReader reader = new PolyshapeReader(s, factory);
        BufferedLineString shape = (BufferedLineString) reader.read("1" + encodedStr);

        System.out.println("Encoded String: " + encodedStr);
        System.out.println();
        int counter = 0;

        for (Point point : shape.getPoints()) {
            counter++;
            System.out.println(counter);
            System.out.println("Latitude: " + point.getY());
            System.out.println("Longitude: " + point.getX());
            System.out.println();
            // break;
        }
    }

    /**
     * Takes a list of the timestamps from all data points, subtracts the first
     * timestamp from each timestamp, and then stores each new point in a
     * RoaringBitmap to compress the data.
     * 
     * @param times     List of timestamps
     * @param firstTime First timestamp of user
     * @return RoaringBitmap instance
     */
    public static RoaringBitmap addTimestamps(List<Long> times, long firstTime) {
        RoaringBitmap bitmap = new RoaringBitmap();

        for (int i = 0; i < times.size(); i++) {
            bitmap.add((int) (times.get(i) - firstTime));
        }
        return bitmap;
    }

    /**
     * Serializes the given RoaringBitmap instance into a byte array.
     * 
     * @param bitmap RoaringBitmap instance
     * @return Serialized RoaringBitmap byte array
     */
    public static byte[] serializeBitmap(RoaringBitmap bitmap) {
        byte[] data = new byte[bitmap.serializedSizeInBytes()];
        ByteBuffer bbf = ByteBuffer.wrap(data);
        bitmap.serialize(bbf);
        return data;
    }

    /**
     * Deserializes the given a byte array into a RoaringBitmap instance.
     * 
     * @param data byte array containing serialized data
     * @return RoaringBitmap instance
     */
    public static RoaringBitmap deserializeBitmap(byte[] data) {
        RoaringBitmap bitmap = new RoaringBitmap();
        try {
            bitmap.deserialize(ByteBuffer.wrap(data));
        } catch (IOException e) {
            System.exit(2);
        }
        return bitmap;
    }
}