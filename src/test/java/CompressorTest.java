import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import java.util.ArrayList;

import location_search.Compressor;
import location_search.Coordinate;

public class CompressorTest extends Tester {

    private Compressor compressor = new Compressor();
    private List<Coordinate> appendCoordinates = setAppendCoordinatesList();
    private List<Long> appendTimes = setAppendTimesList();

    @Test
    public void testTimeCompression() {
        // Passed
        System.out.println(record.getTimes().length);
    }

    @Test
    public void testCoordinateCompressionAndEncodingWriter() {
        // Passed
        byte[] compressedCoordinates = record.getCoordinates();
        System.out.println("AFTER: " + compressedCoordinates.length);
        compressor.compressCoordinates(testCoordinates);
    }

    @Test
    public void testTimeDecompression() {
        // Passed
        long firstTime = times.get(0);
        byte[] compressedTimes = compressor.compressTimestamps(times, firstTime);
        Iterable<Long> decompressedTimes = compressor.decompressTimestamps(compressedTimes, firstTime);
        int counter = 0;
        for (long time : decompressedTimes) {
            // System.out.println("DE: " + time + " | OR: " + times.get(counter));
            // subtracted 10000000 to make assertEquals work
            assertEquals(time - 10000000, times.get(counter) - 10000000);
            counter++;
        }
        System.out.println("DONE!");
    }

    @Test
    public void testCoordinateDecompression() {
        // Passed
        Iterable<Coordinate> decompressedCoordinates = compressor.decompressCoordinates(record.getCoordinates());
        int counter = 0;
        for (Coordinate co : decompressedCoordinates) {
            System.out.println(co + " | " + coordinates.get(counter));
            assertEquals(co.getLat(), coordinates.get(counter).getLat(), 0.00001);
            assertEquals(co.getLon(), coordinates.get(counter).getLon(), 0.00001);
            counter++;
        }
        System.out.println("CORRECT!");
    }

    @Test
    public void testAppendTimestamps() {
        // Passed
        byte[] originalData = compressor.compressTimestamps(testTimes, testTimes.get(0));
        byte[] newData = compressor.appendTimestamps(originalData, appendTimes, testTimes.get(0));

        System.out.println("BEFORE: " + originalData.length);
        System.out.println("AFTER: " + newData.length);

        for (long origTime : compressor.decompressTimestamps(originalData, testTimes.get(0))) {
            System.out.println(origTime);
        }

        System.out.println("-----------------------------------");

        for (long newTime : compressor.decompressTimestamps(newData, testTimes.get(0))) {
            System.out.println(newTime);
        }

        System.out.println("ALL DONE");

        assertTrue(newData.length > originalData.length);
    }

    @Test
    public void testAppendCoordinates1() {
        // Passed
        byte[] originalData = compressor.compressCoordinates(testCoordinates);
        byte[] newData = compressor.appendCoordiantes(originalData, appendCoordinates);
        System.out.println(originalData.length + " | " + newData.length);

        Iterable<Coordinate> newList = compressor.decompressCoordinates(newData);

        for (Coordinate c : testCoordinates) {
            System.out.println(c);
        }

        System.out.println("-----------------------------------");

        for (Coordinate co : newList) {
            System.out.println(co);
        }
        System.out.println("DONE HERE!");
    }

    @Test
    public void testAppendCoordinates2() {
        // Passed
        byte[] originalData = compressor.compressCoordinates(coordinates);
        byte[] newData = compressor.appendCoordiantes(originalData, appendCoordinates);
        System.out.println(originalData.length + " | " + newData.length);

        Iterable<Coordinate> newList = compressor.decompressCoordinates(newData);

        int counter1 = 0;

        for (Coordinate c : coordinates) {
            System.out.println(c);
            counter1++;
        }

        System.out.println("COUNTER = " + counter1);
        System.out.println("-----------------------------------");

        int counter2 = 0;
        for (Coordinate co : newList) {
            System.out.println(co);
            counter2++;
        }
        System.out.println("COUNTER = " + counter2);
        assertEquals(counter2, counter1 + 3);
        System.out.println("DONE HERE!");
    }

    private List<Coordinate> setAppendCoordinatesList() {
        List<Coordinate> list = new ArrayList<Coordinate>();
        list.add(new Coordinate(47.2354, -119.236435));
        list.add(new Coordinate(27.2354636, -110.236435));
        list.add(new Coordinate(7.236, -10.2335));
        return list;
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
}