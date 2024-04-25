package it.scheduleplanner.dbutils;

import it.scheduleplanner.utils.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility class for SQL queries.
 */
public class SQLQueries {

    /**
     * Inserts an employee into the database.
     *
     * @param employee the employee to insert
     * @throws SQLException if a database access error occurs
     */
    public static void insertEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employee (name, overTimeHours, weekendWorker, workingHours) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, employee.getName());
            preparedStatement.setDouble(2, employee.getOverTimeHours());
            preparedStatement.setBoolean(3, employee.isWeekendWorker());
            preparedStatement.setInt(4, employee.getWorkingHours());

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Selects all employees.
     *
     * @return the result set of the query
     * @throws SQLException if a database access error occurs
     */
    public static ResultSet selectAllEmployees() throws SQLException {
        String sql = "SELECT * FROM employee";

        try (Connection connection = DBUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            return preparedStatement.executeQuery();
        }
    }

    /**
     * Selects an employee by ID.
     *
     * @param id the ID of the employee to select
     * @return the result set of the query
     * @throws SQLException if a database access error occurs
     */
    public static ResultSet selectEmployeeById(int id) throws SQLException {
        String sql = "SELECT * FROM employee WHERE id = ?";

        try (Connection connection = DBUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            return preparedStatement.executeQuery();
        }
    }

    /**
     * Updates an employee in the database.
     *
     * @param employee the employee to update
     * @throws SQLException if a database access error occurs
     */
    public static void updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE employee SET name = ?, overTimeHours = ?, weekendWorker = ?, workingHours = ? WHERE id = ?";

        try (Connection connection = DBUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, employee.getName());
            preparedStatement.setDouble(2, employee.getOverTimeHours());
            preparedStatement.setBoolean(3, employee.isWeekendWorker());
            preparedStatement.setInt(4, employee.getWorkingHours());
            preparedStatement.setInt(5, 0 /*employee.getId()*/);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Deletes an employee from the database.
     *
     * @param id the ID of the employee to delete
     * @throws SQLException if a database access error occurs
     */
    public static void deleteEmployee(int id) throws SQLException {
        String sql = "DELETE FROM employee WHERE id = ?";

        try (Connection connection = DBUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Prevents instantiation.
     */
    private SQLQueries() {
    }

}