package location_search;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.roaringbitmap.RoaringBitmap;

import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.context.SpatialContextFactory;
import org.locationtech.spatial4j.exception.InvalidShapeException;
import org.locationtech.spatial4j.shape.impl.BufferedLineString;
import org.locationtech.spatial4j.io.PolyshapeWriter.Encoder;
import org.locationtech.spatial4j.io.PolyshapeReader;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Meet Vora
 * @since June 29th, 2020
 */
public class Compressor implements ICompress {

    /**
     * StringWriter instance used by RoaringBitmap to store encoded lat/long data.
     */
    private StringWriter _writer;

    /** Encoder instance used to encode lat/longs */
    private Encoder _encoder;

    /** Byte array output stream that stores compressed data. */
    private ByteArrayOutputStream _outStream;

    public Compressor() {
        _writer = new StringWriter();
        _encoder = new Encoder(_writer);
        _outStream = new ByteArrayOutputStream();
    }

    /**
     * Compresses given timestamps into byte array. Compresses by subtracting the
     * first timestamp from all data entries, adding new data to Roaringbitmap
     * instance, then compressing that byte array further using
     * CompressorOutputStream.
     * 
     * @param times     Iterable instance containing all timestamps
     * @param firstTime first timestamp of data
     * @return compressed byte array (GZIP format)
     */
    public byte[] compressTimestamps(Iterable<Long> times, long firstTime) {

        RoaringBitmap bitmap = new RoaringBitmap();

        for (long i : times) {
            bitmap.add((int) (i - firstTime));
        }
        return compress(serializeBitmap(bitmap));
    }

    /**
     * Compresses lat/long coordinates into byte array by encoding coordinates into
     * a string, then compressing and returning the String further using
     * CompressorOutputStream.
     * 
     * @param coordinates Iterable instance containing all coordinates
     * @return compressed byte array (GZIP format)
     */
    public byte[] compressCoordinates(Iterable<Coordinate> coordinates) {
        for (Coordinate co : coordinates) {
            try {
                _encoder.write(co.getLon(), co.getLat());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        byte[] data = compress(_writer.toString().getBytes(StandardCharsets.UTF_8));

        // reset writer once coordinates are encoded, compressed, and saved
        _writer.getBuffer().setLength(0);

        return data;
    }

    /**
     * Converts the given new timestamps to a byte array and appends them to the
     * given byte array
     * 
     * @param originalData original timestamp data stored in byte array
     * @param newTimes     new timestamps to convert and append to originalData
     * @return new concatenated byte array
     */
    public byte[] appendTimestamps(byte[] originalData, Iterable<Long> newTimes, long firstTimestamp) {
        byte[] newData = compressTimestamps(newTimes, firstTimestamp);
        return ArrayUtils.addAll(originalData, newData);
    }

    /**
     * Converts the new coordinates into a byte array and appends them to the given
     * byte array.
     * 
     * @param originalData   orignial coordinates data stored in byte array
     * @param newCoordinates new coordinates to convert and append to originalData
     * @return new concatenated byte array
     */
    public byte[] appendCoordiantes(byte[] originalData, Iterable<Coordinate> newCoordinates) {
        byte[] newData = compressCoordinates(newCoordinates);
        return ArrayUtils.addAll(originalData, newData);
    }

    public Iterable<Long> decompressTimestamps(byte[] compressedTimes, long firstTimestamp) {
        byte[] decompressedData = decompress(compressedTimes);
        RoaringBitmap bitmap = deserializeBitmap(decompressedData);
        int[] zeroedData = bitmap.toArray();

        List<Long> originalData = new ArrayList<Long>();
        for (int time : zeroedData) {
            originalData.add((long) (time + firstTimestamp));
        }

        // List<Long> somedata = new ArrayList();
        // Iterator it = somedata.iterator();

        // Iterable<Long> iter = new Iterable<Long> ()
        // {

        // @Override
        // public Iterator<Long> iterator() {
        // // TODO Auto-generated method stub
        // return it;
        // }

        // }
        // ;

        return originalData;
    }

    public Iterable<Coordinate> decompressCoordinates(byte[] compressedCoordinates) {
        return decodeLocation(decompress(compressedCoordinates));
    }

    /**
     * Decodes the given encoded byte array into latitude and longitude values
     * stored as a list of Coordinates.
     * 
     * @param compressedEncodedStr A byte array of the compressed encoded String
     * @return List of Coordinates
     */
    private Iterable<Coordinate> decodeLocation(byte[] decompressedCoordinates) {

        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        String encodedStr = new String(decompressedCoordinates);

        SpatialContextFactory factory = new SpatialContextFactory();
        SpatialContext s = SpatialContext.GEO;
        PolyshapeReader reader = new PolyshapeReader(s, factory);

        try {
            BufferedLineString shape = (BufferedLineString) reader.read("1" + encodedStr);
            for (Point point : shape.getPoints()) {
                coordinates.add(new Coordinate(point.getY(), point.getX()));
                // System.out.println("Latitude: " + point.getY());
                // System.out.println("Longitude: " + point.getX());
                // System.out.println();
            }
        } catch (InvalidShapeException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return coordinates;
    }

    /**
     * Compresses and returns given encoded String as a byte array.
     * 
     * @param arr byte array to be compressed
     * @return compressed byte array (GZIP format)
     */
    private byte[] compress(byte[] arr) {

        CompressorOutputStream compressor = null;

        try {
            compressor = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP,
                    _outStream);
            compressor.write(arr);
            compressor.flush();
            compressor.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CompressorException e) {
            System.exit(1);
        }

        byte[] data = _outStream.toByteArray();
        _outStream.reset();

        return data;
    }

    /**
     * Decompresses and returns the given compressed array.
     * 
     * @param arr compressed byte array (GZIP format)
     * @return decompressed byte array
     */
    private byte[] decompress(byte[] arr) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] data = null;

        CompressorInputStream decompressor;
        try {
            decompressor = new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.GZIP,
                    new ByteArrayInputStream(arr));
            IOUtils.copy(decompressor, output);
            data = output.toByteArray();
        } catch (CompressorException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return data;
    }

    /**
     * Serializes the given RoaringBitmap instance into a byte array.
     * 
     * @param bitmap RoaringBitmap instance
     * @return Serialized RoaringBitmap byte array
     */
    private byte[] serializeBitmap(RoaringBitmap bitmap) {
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
    private RoaringBitmap deserializeBitmap(byte[] data) {
        RoaringBitmap bitmap = new RoaringBitmap();
        try {
            bitmap.deserialize(ByteBuffer.wrap(data));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return bitmap;
    }
}
