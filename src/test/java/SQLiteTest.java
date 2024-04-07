import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTest {
    public static void main(String[] args) {
        // SQLite's connection string
        String url = "jdbc:sqlite:test.sqlite";

        try {
            // Establish a connection to the SQLite database
            Connection connection = DriverManager.getConnection(url);

            // Create a new table
            String createTableSQL = "CREATE TABLE IF NOT EXISTS persons ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL,"
                    + "age INTEGER)";

            Statement statement = connection.createStatement();
            statement.execute(createTableSQL);
            statement.close();

            // Insert data into the table
            String insertSQL = "INSERT INTO persons (name, age) VALUES ('John', 30)";
            statement = connection.createStatement();
            statement.executeUpdate(insertSQL);
            statement.close();

            // Query data from the table
            String querySQL = "SELECT * FROM persons";
            statement = connection.createStatement();
            var resultSet = statement.executeQuery(querySQL);

            // Process the result set
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");

                System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age);
            }

            // Close the result set, statement, and connection
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
    }
}