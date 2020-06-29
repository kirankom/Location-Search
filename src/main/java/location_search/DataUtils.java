package location_search;

import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.context.SpatialContextFactory;
import org.locationtech.spatial4j.shape.impl.BufferedLineString;
import org.locationtech.spatial4j.io.PolyshapeWriter.Encoder;
import org.locationtech.spatial4j.io.PolyshapeReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.FileReader;

import org.roaringbitmap.RoaringBitmap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import org.locationtech.spatial4j.exception.InvalidShapeException;

import java.io.StringWriter;
import java.io.Writer;
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

    public DataUtils() {

    }

    /**
     * Encodes the given latitude and longitude as a String, and stores it in the
     * provided StringWriter instance.
     * 
     * @param lat    Latitude of user
     * @param lon    Longitude of user
     * @param writer StringWriter instance that stores encoded location
     */
    public static void encodeLocation(double lat, double lon, StringWriter writer) throws IOException {
        // StringWriter writer = new StringWriter();
        Encoder encoder = new Encoder(writer);
        encoder.write(lon, lat);
    }

    /**
     * Decodes the given encoded String into latitude and longitude values.
     * 
     * @param compressedEncodedStr A byte array of the compressed encoded String
     * @throws InvalidShapeException
     * @throws IOException
     * @throws java.text.ParseException
     */
    public static void decodeLocation(byte[] compressedEncodedStr)
            throws InvalidShapeException, IOException, java.text.ParseException {

        String encodedStr = new String(compressedEncodedStr);

        SpatialContextFactory factory = new SpatialContextFactory();
        SpatialContext s = SpatialContext.GEO;

        PolyshapeReader reader = new PolyshapeReader(s, factory);
        BufferedLineString shape = (BufferedLineString) reader.read("1" + encodedStr);

        for (Point point : shape.getPoints()) {
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
    public static RoaringBitmap addTimestamps(Iterable<Long> times, long firstTime) {
        RoaringBitmap bitmap = new RoaringBitmap();

        for (long i : times) {
            bitmap.add((int) (i - firstTime));
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
     * @throws IOException
     */
    public static RoaringBitmap deserializeBitmap(byte[] data) throws IOException {
        RoaringBitmap bitmap = new RoaringBitmap();
        bitmap.deserialize(ByteBuffer.wrap(data));
        return bitmap;
    }

}