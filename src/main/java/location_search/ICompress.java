package location_search;

/**
 * Interface for the Compressor class.
 * 
 * @author Meet Vora
 * @since June 29th, 2020
 */
public interface ICompress {

    byte[] compressTimestamps(Iterable<Long> times, long firstTimestamp);

    byte[] compressCoordinates(Iterable<Coordinate> coordinates);

    Iterable<Long> decompressTimestamps(byte[] compressedTimes, long firstTimestamp);

    Iterable<Coordinate> decompressCoordinates(byte[] compressedCoordinates);

    byte[] appendTimestamps(byte[] originalData, Iterable<Long> newTimes, long firstTimestamp);

    byte[] appendCoordinates(byte[] originalData, Iterable<Coordinate> newCoordinates);
}