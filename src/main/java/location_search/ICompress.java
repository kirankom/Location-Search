package location_search;

/**
 * Interface for the Compressor class.
 * 
 * @author Meet Vora
 * @since June 29th, 2020
 */
public interface ICompress {

    byte[] compressTimestamps(Iterable<Long> times, long firstTime);

    byte[] compressCoordinates(Iterable<Coordinate> coordiantes);

    /** Add timestamps in iterable to end of byte array */
    byte[] appendTimestamps(byte[] times, Iterable<Long> iterTimes, long firstTimestamp);

    byte[] appendCoordiantes(byte[] coordinates, Iterable<Coordinate> iterCoordinates);

    Iterable<Long> decompressTimestamps(byte[] compressedTimes, long firstTimestamp);

    Iterable<Coordinate> decompressCoordinates(byte[] coordianteArray);

}