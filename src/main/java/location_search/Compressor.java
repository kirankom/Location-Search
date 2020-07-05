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

/**
 * Compresses lat/long coordinates wrapped in a Coordinate class and timestamps
 * into byte arrays.
 * 
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
     * Compresses timestamps into byte array.
     * 
     * @param times          Iterable instance containing all timestamps
     * @param firstTimestamp first timestamp of data
     * @return compressed byte array (GZIP format)
     */
    public byte[] compressTimestamps(Iterable<Long> times, long firstTimestamp) {
        return compress(serializeBitmap(addToBitmap(times, firstTimestamp)));
    }

    /**
     * Compresses lat/long data into byte array.
     * 
     * @param coordinates Iterable instance containing all coordinates
     * @return compressed byte array (GZIP format)
     */
    public byte[] compressCoordinates(Iterable<Coordinate> coordinates) {
        return compress(encodeLocation(coordinates));
    }

    /**
     * Decompresses the compressed timestamps.
     * 
     * @param compressedTimes compressed times (GZIP format)
     * @param firstTimestamp  first timestamp of data
     * @return Iterable instance containing all timestamps
     */
    public Iterable<Long> decompressTimestamps(byte[] compressedTimes, long firstTimestamp) {
        if (firstTimestamp < 0) {
            throw new IllegalArgumentException("Time must be positive.");
        }

        RoaringBitmap bitmap = deserializeBitmap(decompress(compressedTimes));
        int[] zeroedData = bitmap.toArray();

        List<Long> originalData = new ArrayList<Long>();
        for (int time : zeroedData) {
            originalData.add((long) (time + firstTimestamp));
        }
        return originalData;
    }

    /**
     * Decompresses and decodes the given byte array of compressed lat/long data.
     * Returns an Iterable of Coordinates.
     * 
     * @param compressedCoordinates compressed byte array of lat/long coordinates
     *                              (GZIP format)
     * @return an Iterable of Coordinates
     */
    public Iterable<Coordinate> decompressCoordinates(byte[] compressedCoordinates) {
        return decodeLocation(decompress(compressedCoordinates));
    }

    /**
     * Appends the new timestamps to the given original time data, and orders them
     * in ascending order automatically. Ensures no repeating timestamps are
     * inputted in the new times.
     * 
     * @param originalData   original timestamp data stored in byte array
     * @param newTimes       new timestamps to append to originalData
     * @param firstTimestamp first timestamp of data
     * @return new concatenated byte array
     */
    public byte[] appendTimestamps(byte[] originalData, Iterable<Long> newTimes, long firstTimestamp) {
        RoaringBitmap origBitmap = deserializeBitmap(decompress(originalData));
        RoaringBitmap newBitmap = addToBitmap(newTimes, firstTimestamp);

        if (!RoaringBitmap.and(origBitmap, newBitmap).isEmpty()) {
            throw new IllegalArgumentException("Overlapping timestamps between the old and new data are not allowed.");
        }

        RoaringBitmap concatenated = RoaringBitmap.or(origBitmap, newBitmap);
        return compress(serializeBitmap(concatenated));
    }

    /**
     * Appends the new coordinates to the given original location data.
     * 
     * @param originalData   orignial coordinates data stored in byte array
     * @param newCoordinates new coordinates to convert and append to originalData
     * @return new concatenated byte array
     */
    public byte[] appendCoordinates(byte[] originalData, Iterable<Coordinate> newCoordinates) {
        String decodedOriginal = new String(decompress(originalData));
        String newEncoded = new String(encodeLocation(newCoordinates));

        return compress((decodedOriginal + newEncoded).getBytes(StandardCharsets.UTF_8));
    }

    /* ==================================================== */
    /* ================== Helper Methods ================== */
    /* ==================================================== */

    /**
     * Encodes the lat/long data (stored in Coordinate instances) into a String.
     * Returns a byte array of the encoded String.
     * 
     * @param coordinates Iterable instance containing all lat/long Coordinates
     * @return encoded byte array
     */
    private byte[] encodeLocation(Iterable<Coordinate> coordinates) {
        if (!coordinates.iterator().hasNext()) {
            throw new IllegalArgumentException("Iterable of coordinates must not be empty.");
        }

        for (Coordinate co : coordinates) {
            try {
                _encoder.write(co.getLon(), co.getLat());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        byte[] data = _writer.toString().getBytes(StandardCharsets.UTF_8);
        _writer.getBuffer().setLength(0);

        return data;
    }

    /**
     * Decodes the given encoded and decompressed byte array into latitude and
     * longitude values stored in an Iterable of Coordinates.
     * 
     * @param decompressedEncodedStr byte array of the decompressed encoded String
     * @return Iterable of Coordinates
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
            }
        } catch (InvalidShapeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return coordinates;
    }

    /**
     * Returns a compressed (GZIP format) version of the given byte array using
     * CompressorOutputStream.
     * 
     * @param arr byte array to be compressed
     * @return compressed byte array (GZIP format)
     */
    private byte[] compress(byte[] arr) {

        if (arr.length == 0) {
            throw new IllegalArgumentException("Data arrays must not be empty");
        }

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
     * Decompresses the given compressed array using CompressorInputStream.
     * 
     * @param arr compressed byte array (GZIP format)
     * @return decompressed byte array
     */
    private byte[] decompress(byte[] arr) {

        if (arr.length == 0) {
            throw new IllegalArgumentException("Data arrays must not be empty");
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CompressorInputStream decompressor;
        byte[] data = null;
        try {
            decompressor = new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.GZIP,
                    new ByteArrayInputStream(arr));
            IOUtils.copy(decompressor, output);
            data = output.toByteArray();
        } catch (CompressorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Subtracts the first timestamp from all timestamp entries and adds data to
     * Roaringbitmap instance.
     * 
     * @param times     Iterable instance containing all timestamps
     * @param firstTime first timestamp of data
     * @return RoaringBitmap instance containing timestamps
     */
    private RoaringBitmap addToBitmap(Iterable<Long> times, long firstTimestamp) {
        if (firstTimestamp < 0) {
            throw new IllegalArgumentException("Time must be positive.");
        }
        if (!times.iterator().hasNext()) {
            throw new IllegalArgumentException("Iterable of timestamps must not be empty.");
        }

        RoaringBitmap bitmap = new RoaringBitmap();
        for (long i : times) {
            bitmap.add((int) (i - firstTimestamp));
        }
        return bitmap;
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
