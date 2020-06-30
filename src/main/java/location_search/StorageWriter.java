package location_search;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class StorageWriter implements IStoreWriter {

    /** Name of the database to store data in. */
    private String _databaseName;

    /** Username of database. */
    private String _username;

    /** Password of database. */
    private String _password;

    private Connection _conn;

    // private Statement _tableStmt;

    public StorageWriter(String databaseName, String username, String password) {
        this._databaseName = databaseName;
        this._username = username;
        this._password = password;
        this._conn = establishConnection();
        createTable();
    }

    public StorageWriter() {
        this("location_search", "ls", "locationSearch");
    }

    public void upsertRecord(Record record) {
    }

    public Record getRecord(long userID) {

    }

    public void commit() {
    }

    private void createTable() {
        String tableCmd = "CREATE TABLE IF NOT EXISTS testing123 (user_ID INTEGER PRIMARY KEY NOT NULL, first_timestamp BIGINT NOT NULL, timestamps VARBINARY(30000) NOT NULL, locations VARBINARY(30000) NOT NULL)";

        try {
            Statement tableStmt = _conn.createStatement();
            tableStmt.executeUpdate(tableCmd);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
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
        } catch (ClassNotFoundException e) {
            System.exit(1);
        } catch (SQLException e) {
            System.exit(1);
        }
        return conn;
    }
}