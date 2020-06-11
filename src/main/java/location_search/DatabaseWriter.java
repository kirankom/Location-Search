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
    /** The file path to the JSON file. */
    private String _filename;
    /** The first timestamp recorded in the location data. */
    private Long _firstTimestamp;

    public DatabaseWriter(int userID, String filename) {
        _userID = userID;
        _filename = filename;
        _firstTimestamp = 0L;
    }

    /**
     * Compresses and returns given encoded String as a byte array.
     * 
     * @param encodedStr Encoded Latitude/Longitude String
     * @return compressed byte array
     */
    public byte[] convertStr(String encodedStr) {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();

        try {
            CompressorOutputStream compressor = new CompressorStreamFactory()
                    .createCompressorOutputStream(CompressorStreamFactory.GZIP, fos);
            compressor.write(encodedStr.getBytes());
            compressor.flush();
            compressor.close();
        } catch (CompressorException e) {
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        }
        // System.out.println("Compressed byte String:" + fos.size() + ":::" +
        // "Compression Type:::" + t);
        return fos.toByteArray();
    }

    /**
     * Adds the user ID, the first timestamp, the compressed byte array of
     * timestamps, and the encoded latitude/longitude String into a database as one
     * entry.
     */
    public void databaseAdder() {
        String json_file = "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/test.json";
        List<Long> times = new ArrayList<Long>();
        StringWriter writer = new StringWriter();

        try {
            RoaringBitmap bitmap = DataUtils.parser(writer, times, _filename);
            byte[] timeData = DataUtils.serializeBitmap(bitmap);
            byte[] compressedEncoding = convertStr(writer.toString());

        } catch (FileNotFoundException e) {
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        } catch (org.json.simple.parser.ParseException e) {
            System.exit(1);
        }
    }

    /**
     * Creates a data table in the connected database
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void createTable() throws ClassNotFoundException, SQLException {
        String command = "CREATE TABLE IF NOT EXISTS location_data ("
                + "user_ID int NOT null, first_timestamp bigint NOT null, timestamps VARBINARY, locations VARBINARY)";

        Connection conn = establishConnection();
        PreparedStatement create = conn.prepareStatement(command);
        create.executeUpdate();
    }

    /**
     * Establishes a connection with a MySQL database.
     * 
     * @return database connection instance
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection establishConnection() throws ClassNotFoundException, SQLException {

        String database_name = "location_search";
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/" + database_name;
        String username = "root";
        String password = "4quhgfzn";

        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}