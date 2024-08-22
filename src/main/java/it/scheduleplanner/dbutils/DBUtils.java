package it.scheduleplanner.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for database operations.
 */
public class DBUtils {

    private static final String URL = "jdbc:sqlite:scheduleplanner.sqlite";

    /**
     * Initializes the database by creating the employee table if it does not exist.
     *
     * @throws SQLException if a database access error occurs
     */
    public static void initializeDatabase() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS employee ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "overTimeHours INTEGER,"
                + "weekendWorker BOOLEAN NOT NULL ,"
                + "workingHours INTEGER,"
                + "fulltimeWorker BOOLEAN NOT NULL ,"
                + "freeDay TEXT CHECK( freeDay IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') ) NOT NULL);"
                + "CREATE TABLE IF NOT EXISTS vacation ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "employeeId INTEGER NOT NULL,"
                + "startDate TEXT NOT NULL,"
                + "endDate TEXT NOT NULL,"
                + "FOREIGN KEY(employeeId) REFERENCES employee(id));";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        }
    }

    /**
     * Establishes a connection to the database.
     *
     * @return the connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Closes the connection to the database.
     *
     * @param connection the connection to close
     * @throws SQLException if a database access error occurs
     */
    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Prevents instantiation.
     */
    private DBUtils() {
    }
}