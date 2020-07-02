package location_search;

import java.util.Iterator;

// import org.locationtech.spatial4j.io.PolyshapeWriter.Encoder;
// import org.roaringbitmap.RoaringBitmap;

public interface ICompress {

    byte[] compressTimestamps(Iterable<Long> times, long firstTime);

    byte[] compressCoordinates(Iterable<Coordinate> coordiantes);

    /** Add timestamps in iterable to end of byte array */
    byte[] appendTimestamps(byte[] times, Iterable<Long> iterTimes, long firstTimestamp);

    byte[] appendCoordiantes(byte[] coordinates, Iterable<Coordinate> iterCoordinates);

    Iterable<Long> decompressTimestamps(byte[] compressedTimes, long firstTimestamp);

    Iterable<Coordinate> decompressCoordinates(byte[] coordianteArray);

}