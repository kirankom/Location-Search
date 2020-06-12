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

    public DatabaseWriter(int userID, String filename) {
        _userID = userID;
        _filename = filename;
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
    public void databaseAdder(String databaseName) {
        // can change varbinary to (med/long)blob to increase size
        String table_command = "CREATE TABLE IF NOT EXISTS testing123 (user_ID INTEGER PRIMARY KEY NOT NULL, first_timestamp BIGINT NOT NULL, timestamps VARBINARY(30000) NOT NULL, locations VARBINARY(30000) NOT NULL)";

        // String json_file =
        // "C:/Users/meetr/Documents/personal_projects/Location-Search/src/test/test.json";
        List<Long> times = new ArrayList<Long>();
        StringWriter writer = new StringWriter();

        try {

            RoaringBitmap bitmap = DataUtils.parser(writer, times, _filename);
            Long firstTimestamp = times.get(0);
            byte[] timeData = DataUtils.serializeBitmap(bitmap);
            byte[] compressedEncoding = convertStr(writer.toString());

            Connection conn = establishConnection(databaseName);
            Statement tableStmt = conn.createStatement();
            // create the data table
            tableStmt.executeUpdate(table_command);

            PreparedStatement dataEntry = conn.prepareStatement(
                    "INSERT INTO testing123(user_ID, first_timestamp, timestamps, locations) VALUES (?, ?, ?, ?)");
            dataEntry.setInt(1, _userID);
            dataEntry.setLong(2, firstTimestamp);
            dataEntry.setBytes(3, timeData);
            dataEntry.setBytes(4, compressedEncoding);
            dataEntry.executeUpdate();

            conn.close();
            System.out.println("DONEEE!!!!!");

        } catch (FileNotFoundException e) {
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
        } catch (org.json.simple.parser.ParseException e) {
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.exit(1);
        } catch (SQLException e) {
            System.exit(1);
        }
    }

    /**
     * Creates a data table in the connected database.
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void createTable(String database_name) throws ClassNotFoundException, SQLException {

        // conn.close();
    }

    /**
     * Establishes a connection with the specified MySQL database.
     * 
     * @return database connection instance
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection establishConnection(String database_name) throws ClassNotFoundException, SQLException {

        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/" + database_name;
        String username = "ls";
        String password = "locationSearch";

        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}