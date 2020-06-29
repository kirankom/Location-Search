import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import location_search.Compressor;

public class CompressorTest {

    private Compressor compressor = new Compressor();
    private List<Long> times;

    @Test
    public void testTimeCompression() {
        System.out.println(getCompressedTimes().length);
    }

    @Test
    public void testTimeDecompression() {
        compressor.decompressTimestamps(getCompressedTimes());
    }

    public byte[] getCompressedTimes() {

        times = new ArrayList<Long>();
        times.add(1416593801893L);
        times.add(1416593928116L);
        times.add(1416594249921L);
        times.add(1416594373099L);
        times.add(1416594497165L);
        times.add(1416594620993L);
        times.add(1416594860982L);
        times.add(1416594984988L);

        byte[] compressedTimes = compressor.compressTimestamps(times, 1416593801893L);

        return compressedTimes;

    }

}