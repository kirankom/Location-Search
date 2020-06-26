package location_search;

import org.locationtech.spatial4j.exception.InvalidShapeException;
import org.locationtech.spatial4j.io.PolyshapeWriter.Encoder;
import org.locationtech.spatial4j.shape.Point;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.roaringbitmap.RoaringBitmap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.FileReader;

import java.io.IOException;
import java.text.ParseException;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;

/**
 * Grabs and saves the user location data -- timestamps, latitude, and longitude
 * -- into a database
 * 
 * User of this class makes an instance of this class for each different databse
 * they want to add stuff to
 * 
 * @author Meet Vora
 * @since June 4th, 2020
 */
public class DatabaseWriter {

    // /** The userID of this user. */
    // private int _userID;

    // /** The file path to the data file. */
    // private String _filename;

    /** Name of the database to store data in. */
    private String _databaseName;

    /** Username of database. */
    private String _username;

    /** Password of database. */
    private String _password;

    /** RoaringBitmap instance. */
    // private RoaringBitmap _bitmap;

    /**
     * StringWriter instance used by RoaringBitmap to store encoded lat/long data.
     */
    private StringWriter _writer;

    /** List of timestamps parsed from given file. */
    private List<Long> _times;

    /** Encoder instance used to encode lat/longs */
    private Encoder _encoder;

    /** Byte array input stream that stores decompressed data. */
    private ByteArrayInputStream _inStream;

    /** Byte array output stream that stores compressed data. */
    private ByteArrayOutputStream _outStream;

    /** Connection instance used to connect to database. */
    private Connection _conn;

    /** Compressor instance that compresses byte[] arrays. */
    // private CompressorOutputStream _compressor;

    public DatabaseWriter(String databaseName, String username, String password) {
        _databaseName = databaseName;
        _username = username;
        _password = password;
        // _bitmap = null;
        _writer = new StringWriter();
        _times = new ArrayList<Long>();
        _encoder = new Encoder(_writer);
        _inStream = new ByteArrayInputStream(null);
        _outStream = new ByteArrayOutputStream();
        _conn = establishConnection();
        // _compressor = setCompressor();

    }

    public DatabaseWriter() {
        this("location_search", "ls", "locationSearch");
    }

    /**
     * Creates a table in the given database. Adds the user ID, the first timestamp,
     * the compressed byte array of timestamps, and the encoded latitude/longitude
     * String into a database as one entry.
     * 
     * @param userID   userID of the user
     * @param filename name of file to read location data from
     */
    public void databaseAdder(int userID, String filename) {
        // can change varbinary to (med/LONG)blob to increase size
        String tableCommand = "CREATE TABLE IF NOT EXISTS testing123 (user_ID INTEGER PRIMARY KEY NOT NULL, first_timestamp BIGINT NOT NULL, timestamps VARBINARY(30000) NOT NULL, locations VARBINARY(30000) NOT NULL)";

        String insertStmt = "INSERT INTO testing123(user_ID, first_timestamp, timestamps, locations) VALUES (?, ?, ?, ?)";

        // String json_file =
        // "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/test.json";
        RoaringBitmap bitmap = null;

        try {

            bitmap = parse(filename);
            Long firstTimestamp = _times.get(0);

            byte[] compressedTimeData = compress(DataUtils.serializeBitmap(bitmap));
            byte[] compressedEncoding = compress(_writer.toString().getBytes());

            // create the data table
            Statement tableStmt = _conn.createStatement();
            tableStmt.executeUpdate(tableCommand);

            PreparedStatement dataEntry = _conn.prepareStatement(insertStmt);
            dataEntry.setInt(1, userID);
            dataEntry.setLong(2, firstTimestamp);
            dataEntry.setBytes(3, compressedTimeData);
            dataEntry.setBytes(4, compressedEncoding);
            dataEntry.executeUpdate();

        } catch (FileNotFoundException e) {
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        } catch (org.json.simple.parser.ParseException e) {
            System.exit(1);
        } catch (SQLException e) {
            System.exit(1);
        }

        // clear instance varibles for reusage
        _writer.getBuffer().setLength(0);
        _times.clear();
    }

    /**
     * Establishes a connection with the specified MySQL database.
     * 
     * @return database connection instance
     */
    private Connection establishConnection() {

        Connection conn = null;
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/" + _databaseName;
        // String username = "ls";
        // String password = "locationSearch";

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, _username, _password);
        } catch (ClassNotFoundException e) {
            System.exit(1);
        } catch (SQLException e) {
            System.exit(1);
        }
        return conn;
    }

    /**
     * Parses each data point consisting of a timestamp, latitude, and longitude
     * from the file _filename and stores it in the inputted instances.
     * 
     * @param filename Name of file to parse
     * @return RoaringBitmap instance that compresses timestamps
     * @throws IOException
     * @throws FileNotFoundException
     * @throws org.json.simple.parser.ParseException
     */
    RoaringBitmap parse(String filename)
            throws FileNotFoundException, IOException, org.json.simple.parser.ParseException {

        // Encoder encoder = new Encoder(_writer);
        JSONObject jsonObj = (JSONObject) new JSONParser().parse(new FileReader(filename));

        // should change "locations" to whatever the name of the list in the
        // json file is
        JSONArray jsonArray = (JSONArray) jsonObj.get("locations");

        for (Object obj : jsonArray) {
            JSONObject location = (JSONObject) obj;

            long timestamp = Long.parseLong((String) location.get("timestampMs"));

            double lat = ((Long) location.get("latitudeE7") * 1.0) / 1e7;
            double lon = ((Long) location.get("longitudeE7") * 1.0) / 1e7;

            _times.add(timestamp);
            _encoder.write(lon, lat);
        }
        // _firstTimestamp = times.get(0);
        return DataUtils.addTimestamps(_times, _times.get(0));
    }

    /**
     * Compresses and returns given encoded String as a byte array.
     * 
     * @param arr byte array to be compressed
     * @return compressed byte array
     */
    byte[] compress(byte[] arr) {

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

    byte[] decompress(byte[] arr) {
        byte[] data = new byte[arr.length];

        CompressorInputStream decompressor;
        try {
            decompressor = new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.GZIP,
                    new ByteArrayInputStream(data));
            decompressor.read();
        } catch (CompressorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }

    // /**
    // * Instantiates a CompressorOutputStream instance.
    // *
    // * @return CompressorOutputStream instance
    // */
    // private CompressorOutputStream setCompressor() {
    // CompressorOutputStream compressor = null;
    // try {
    // compressor = new
    // CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP,
    // _byteArrStream);
    // } catch (CompressorException e) {
    // System.exit(1);
    // }
    // return compressor;
    // }
}