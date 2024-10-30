package it.scheduleplanner.dbutils;

import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.utils.Vacation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
     * @return the ID of the inserted employee
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

            preparedStatement.executeUpdate();

            try (PreparedStatement lastInsertIdStatement = connection.prepareStatement("SELECT last_insert_rowid()")) {
                ResultSet resultSet = lastInsertIdStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Inserts a vacation into the database.
     *
     * @param connection the database connection
     * @param employeeId the ID of the employee
     * @param startDate  the start date of the vacation
     * @param endDate    the end date of the vacation
     * @return the number of rows affected by the query
     * @throws SQLException if a database access error occurs
     */
    public static int insertVacation(Connection connection, int employeeId, String startDate, String endDate) throws SQLException {
        String sql = "INSERT INTO vacation (employeeId, startDate, endDate) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setString(2, startDate);
            preparedStatement.setString(3, endDate);

            return preparedStatement.executeUpdate();
        }
    }

    public static List<Vacation> selectVacation(Connection connection, int employeeId) throws SQLException {
        String sql = "SELECT * FROM vacation WHERE employeeId = ?";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Vacation> vacations = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                vacations.add(new Vacation(
                        LocalDate.parse(resultSet.getString("startDate"), formatter),
                        LocalDate.parse(resultSet.getString("endDate"), formatter)
                ));
            }
        }
        return vacations;
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
                employee.setEmployeeId(resultSet.getInt("id"));
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
     * Truncates the employee table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void truncateDatabase(Connection connection) throws SQLException {
        String sql = "DELETE FROM employee; UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'employee';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Prevents instantiation.
     */
    private SQLQueries() {
    }
}