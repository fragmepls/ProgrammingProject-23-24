package it.scheduleplanner.dbutils;

import it.scheduleplanner.utils.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for SQL queries.
 */
public class SQLQueries {

    /**
     * Inserts an employee into the database.
     *
     * @param connection the database connection
     * @param employee   the employee to insert
     * @throws SQLException if a database access error occurs
     */
    public static int insertEmployee(Connection connection, Employee employee) throws SQLException {
        String sql = "INSERT INTO employee (name, overTimeHours, weekendWorker, workingHours, freeDay, fulltimeWorker) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setDouble(2, employee.getOverTimeHours());
            preparedStatement.setBoolean(3, employee.isWeekendWorker());
            preparedStatement.setInt(4, employee.getWorkingHours());
            preparedStatement.setString(5, employee.getFreeDay().toString());
            preparedStatement.setBoolean(6, employee.isFullTimeWorker());

            return preparedStatement.executeUpdate();
        }
    }

    /**
     * Selects all employees.
     *
     * @param connection the database connection
     * @return the result set of the query
     * @throws SQLException if a database access error occurs
     */
    public static List<Employee> selectAllEmployees(Connection connection) throws SQLException {
        String sql = "SELECT * FROM employee";
        List<Employee> employees = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getString("name"),
                        resultSet.getBoolean("weekendWorker"),
                        resultSet.getString("freeDay"),
                        resultSet.getBoolean("fulltimeWorker")
                );
                employees.add(employee);
            }

            return employees;
        }
    }

    /**
     * Selects an employee by ID.
     *
     * @param connection the database connection
     * @param id         the ID of the employee to select
     * @return the result set of the query
     * @throws SQLException if a database access error occurs
     */
    public static Employee selectEmployeeById(Connection connection, int id) throws SQLException {
        String sql = "SELECT * FROM employee WHERE id = ?";
        Employee employee = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employee = new Employee(
                        resultSet.getString("name"),
                        resultSet.getBoolean("weekendWorker"),
                        resultSet.getString("freeDay"),
                        resultSet.getBoolean("fulltimeWorker")
                );
            }

            return employee;
        }
    }

    /**
     * Updates an employee in the database.
     *
     * @param connection the database connection
     * @param employee   the employee to update
     * @throws SQLException if a database access error occurs
     */
    public static void updateEmployee(Connection connection, Employee employee, int id) throws SQLException {
        String sql = "UPDATE employee SET name = ?, overTimeHours = ?, weekendWorker = ?, workingHours = ?, freeDay = ?, fulltimeWorker = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setDouble(2, employee.getOverTimeHours());
            preparedStatement.setBoolean(3, employee.isWeekendWorker());
            preparedStatement.setInt(4, employee.getWorkingHours());
            preparedStatement.setString(5, employee.getFreeDay().toString());
            preparedStatement.setBoolean(6, employee.isFullTimeWorker());
            preparedStatement.setInt(7, id);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Deletes an employee from the database.
     *
     * @param connection the database connection
     * @param id         the ID of the employee to delete
     * @throws SQLException if a database access error occurs
     */
    public static void deleteEmployee(Connection connection, int id) throws SQLException {
        String sql = "DELETE FROM employee WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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