package location_search;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores and retrieves Records of user data from a database.
 * 
 * @author Meet Vora
 * @since June 29th, 2020
 */
public class StorageWriter implements IStoreWriter {

    /** Name of the database to store data in. */
    private String _databaseName;

    /** Username of database. */
    private String _username;

    /** Password of database. */
    private String _password;

    /** Name of table. */
    private String _tableName;

    /** Connection instance to database. */
    private Connection _conn;

    /** PreparedStatement instance that inserts data into table. */
    private PreparedStatement _ps;

    /** Statement instance that gets records from database. */
    private Statement _stmt;

    /** ResultSet instance that gets records from database. */
    private ResultSet _rs;

    public StorageWriter(String databaseName, String username, String password) {
        this._databaseName = databaseName;
        this._username = username;
        this._password = password;
        this._tableName = "writertest";
        this._conn = establishConnection();
        this._ps = null;
        this._stmt = null;
        this._rs = null;
        setStmts();
        createTable();
    }

    public StorageWriter() {
        this("location_search", "ls", "locationSearch");
    }

    /**
     * Inserts or updates the given record in the database.
     * 
     * @param record Record instance that contains user data
     */
    public void upsertRecord(Record record) {
        long userID = record.getUserID();
        long firstTimestamp = record.getFirstTimestamp();
        byte[] times = record.getTimes();
        byte[] coordinates = record.getCoordinates();

        try {
            _ps.setLong(1, userID);
            _ps.setLong(2, firstTimestamp);
            _ps.setBytes(3, times);
            _ps.setBytes(4, coordinates);
            _ps.setBytes(5, times);
            _ps.setBytes(6, coordinates);
            _ps.addBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Obtains record of the user with given user ID from the database.
     * 
     * @param userID user ID of user
     * @return Record instance that contains the specific user's data
     */
    public Record getRecord(long userID) {
        String query = "SELECT * FROM " + _tableName + " WHERE user_ID = " + userID;
        Long firstTimestamp = 0L;
        byte[] times = null;
        byte[] coordinates = null;
        boolean recordExists = false;

        try {
            _rs = _stmt.executeQuery(query);
            while (_rs.next()) {
                firstTimestamp = _rs.getLong("first_timestamp");
                times = _rs.getBytes("timestamps");
                coordinates = _rs.getBytes("coordinates");
                recordExists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (!recordExists) {
            throw new IllegalArgumentException("Record does not exist.");
        }

        return new Record(userID, firstTimestamp, times, coordinates);
    }

    /**
     * Returns an iterable of Coordinates of lat/longs that describes the user
     * location within the given time range.
     * 
     * @param userID    user ID of user
     * @param startTime start time of search interval
     * @param endTime   end time of search interval
     * @return Iterable of Coordinates that fall within time interval
     */
    public Iterable<Coordinate> search(long userID, long startTime, long endTime) {

        if (startTime < 0 || startTime >= endTime) {
            throw new IllegalArgumentException("Time interval is invalid");
        }

        List<Coordinate> intervalCoordinates = new ArrayList<Coordinate>();
        Compressor compressor = new Compressor();
        Record record = getRecord(userID);
        boolean end = false;

        Iterator<Long> timestampsIter = compressor.decompressTimestamps(record.getTimes(), record.getFirstTimestamp())
                .iterator();
        Iterator<Coordinate> coordinateIter = compressor.decompressCoordinates(record.getCoordinates()).iterator();

        while (timestampsIter.hasNext() && coordinateIter.hasNext() && !end) {
            long time = timestampsIter.next();
            Coordinate co = coordinateIter.next();

            if (time <= endTime && time >= startTime) {
                intervalCoordinates.add(co);
            }
            if (time >= endTime) {
                end = true;
            }
        }
        return intervalCoordinates;
    }

    /**
     * Commits all the records added, to the database.
     */
    public void commit() {
        try {
            _ps.executeBatch();
            _conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the connection to the database and all statements.
     */
    public void close() {
        try {
            _conn.close();
            _ps.close();
            _stmt.close();
            _rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ==================================================== */
    /* ================== Helper Methods ================== */
    /* ==================================================== */

    /**
     * Creates the data table in the database if it doesn't already exist.
     */
    private void createTable() {
        String tableCmd = "CREATE TABLE IF NOT EXISTS " + _tableName
                + " (user_ID BIGINT PRIMARY KEY NOT NULL, first_timestamp BIGINT NOT NULL, timestamps LONGBLOB NOT NULL, coordinates LONGBLOB NOT NULL)";

        try {
            Statement tableStmt = _conn.createStatement();
            tableStmt.executeUpdate(tableCmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

            // SETTING AUTOCOMMIT TO FALSE!
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Database name or login credentials are incorrect.", e);
        }
        return conn;
    }

    /**
     * Instantiates the Statement and PreparedStatement instances.
     */
    private void setStmts() {
        String insertCmd = "INSERT INTO " + _tableName
                + " (user_ID, first_timestamp, timestamps, coordinates) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE timestamps = ?, coordinates = ?";

        try {
            _stmt = _conn.createStatement();
            _ps = _conn.prepareStatement(insertCmd);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}