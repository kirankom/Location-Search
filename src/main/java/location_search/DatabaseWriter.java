package location_search;

import org.locationtech.spatial4j.exception.InvalidShapeException;
import org.locationtech.spatial4j.io.PolyshapeWriter.Encoder;
import org.locationtech.spatial4j.shape.Point;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Grabs and saves the user location data -- timestamps, latitude, and longitude
 * -- into a database
 * 
 * @author Meet Vora
 * @since June 4th, 2020
 */
public class DatabaseWriter {

    /** The userID of this user. */
    private int _userID;

    /** The file path to the data file. */
    private String _filename;

    /** Name of the database to store data in. */
    private String _databaseName;

    /** Username of database. */
    private String _username;

    /** Password of database. */
    private String _password;

    /** Compressor instance that compresses byte[] arrays. */
    private CompressorOutputStream _compressor;

    /** Connection instance used to connect to database. */
    private Connection _conn;

    DatabaseWriter(int userID, String filename, String databaseName, String username, String password) {
        _userID = userID;
        _filename = filename;
        _databaseName = databaseName;
        _username = username;
        _password = password;
        _compressor = null;
        _conn = null;
    }

    DatabaseWriter(int userID, String filename, String databaseName) {
        this(userID, filename, databaseName, "ls", "location");
    }

    /**
     * Creates a table in the given database. Adds the user ID, the first timestamp,
     * the compressed byte array of timestamps, and the encoded latitude/longitude
     * String into a database as one entry.
     * 
     * @param databaseName name of database
     */
    void databaseAdder() {
        // can change varbinary to (med/LONG)blob to increase size
        String tableCommand = "CREATE TABLE IF NOT EXISTS testing123 (user_ID INTEGER PRIMARY KEY NOT NULL, first_timestamp BIGINT NOT NULL, timestamps VARBINARY(30000) NOT NULL, locations VARBINARY(30000) NOT NULL)";

        String insertStmt = "INSERT INTO testing123(user_ID, first_timestamp, timestamps, locations) VALUES (?, ?, ?, ?)";

        // String json_file =
        // "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/test.json";
        List<Long> times = new ArrayList<Long>();
        StringWriter writer = new StringWriter();

        try {

            RoaringBitmap bitmap = DataUtils.parser(writer, times, _filename);
            Long firstTimestamp = times.get(0);
            byte[] timeData = compress(DataUtils.serializeBitmap(bitmap));
            byte[] compressedEncoding = compress(writer.toString().getBytes());

            // create the data table
            Statement tableStmt = _conn.createStatement();
            tableStmt.executeUpdate(tableCommand);

            PreparedStatement dataEntry = _conn.prepareStatement(insertStmt);
            dataEntry.setInt(1, _userID);
            dataEntry.setLong(2, firstTimestamp);
            dataEntry.setBytes(3, timeData);
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
    }

    /**
     * Establishes a connection with the specified MySQL database.
     * 
     * @return database connection instance
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    void establishConnection() {

        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/" + _databaseName;
        // String username = "ls";
        // String password = "locationSearch";

        try {
            Class.forName(driver);
            _conn = DriverManager.getConnection(url, _username, _password);
        } catch (ClassNotFoundException e) {
            System.exit(1);
        } catch (SQLException e) {
            System.exit(1);
        }
    }

    /**
     * Compresses and returns given encoded String as a byte array.
     * 
     * @param arr byte array to be compressed
     * @return compressed byte array
     */
    byte[] compress(byte[] arr) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            _compressor = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP,
                    baos);
            _compressor.write(arr);
            _compressor.flush();
            _compressor.close();
        } catch (CompressorException e) {
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        }

        return baos.toByteArray();
    }
}