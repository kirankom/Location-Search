import org.locationtech.spatial4j.io.PolyshapeWriter.Encoder;
import org.roaringbitmap.RoaringBitmap;

public interface ICompressor {

    public byte[] compressTimestamps(Iterable<Long> times, long firstTime);

    public byte[] compressCoordinates(Iterable<Coordinate> coordiantes);

    /** Add timestamps in iterable to end of byte array */
    public byte[] appendTimestamps(byte[] times, Iterable<Long> iterTimes, long firstTimestamp);

    public byte[] appendCoordiantes(byte[] coordinates, Iterable<Coordinate> iterCoordinates);

    public Iterable<Long> decompressTimestamps(byte[] compressedTimes);

    public Iterable<Coordinate> decompressCoordinates(byte[] coordianteArray);

}