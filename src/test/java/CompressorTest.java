import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;

import location_search.Compressor;
import location_search.Coordinate;

public class CompressorTest extends Tester {

    private Compressor compressor = new Compressor();

    @Test
    public void testTimeCompression() {
        System.out.println(getCompressedTimes().length);
    }

    @Test
    public void testCoordinateCompressionAndEncodingWriter() {
        byte[] compressedCoordinates = record.getCoordinates();
        System.out.println("AFTER: " + compressedCoordinates.length);
        compressor.compressCoordinates(testCoordinates);
    }

    @Test
    public void testAppendTimestamps() {
        byte[] originalData = record.getTimes();
        byte[] newData = compressor.appendTimestamps(originalData, testTimes, record.getFirstTimestamp());

        System.out.println("BEFORE: " + originalData.length);
        System.out.println("AFTER: " + newData.length);
        assertTrue(newData.length > originalData.length);
    }

    public void testAppendCoordinates() {

    }

    @Test
    // public void testTimeDecompression() {
    // compressor.decompressTimestamps(getCompressedTimes());
    // }

    public byte[] getCompressedTimes() {

        byte[] compressedTimes = compressor.compressTimestamps(times, 1416593801893L);

        return compressedTimes;
    }
}