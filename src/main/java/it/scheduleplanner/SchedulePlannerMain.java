package it.scheduleplanner;

import it.scheduleplanner.dbutils.DBUtils;
import it.scheduleplanner.dbutils.SQLQueries;
import it.scheduleplanner.export.*;
import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.utils.Vacation;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class SchedulePlannerMain {

    public static void main(String[] args) throws SQLException {
        DBUtils.initializeDatabase();
        Connection connection = DBUtils.getConnection();

        ArrayList<Employee> employeeList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of employees: ");
        int numberOfEmployees = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numberOfEmployees; i++) {
            System.out.println("Enter details for employee " + (i + 1) + ":");
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Is Weekend Worker (true/false): ");
            boolean isWeekendWorker = scanner.nextBoolean();
            scanner.nextLine();
            System.out.print("Free Day: ");
            String freeDay = scanner.nextLine();
            System.out.print("Is Full Time (true/false): ");
            boolean isFullTime = scanner.nextBoolean();
            scanner.nextLine();

            Employee employee = new Employee(name, isWeekendWorker, freeDay, isFullTime);
            SQLQueries.insertEmployee(connection, employee);
            employeeList.add(employee);

            System.out.println("Enter vacation details for " + name + ":");
            System.out.print("Start Date (YYYY-MM-DD): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());
            System.out.print("End Date (YYYY-MM-DD): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            Vacation vacation = new Vacation(startDate, endDate);
            employee.addVacation(vacation);
        }

        for (Employee e : employeeList) {
            ScheduleCreator.addEmployee(e);
        }

        System.out.print("Enter number of employees per day: ");
        int numberOfEmployeesPerDay = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter begin date (YYYY-MM-DD): ");
        LocalDate beginDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter end date (YYYY-MM-DD): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        ShiftScheduleInterface calendar = ScheduleCreator.create(beginDate, endDate, numberOfEmployeesPerDay, false, DayOfWeek.SATURDAY);
        System.out.println(calendar);
        Export.CSVExport(calendar, "C:\\Users\\leoob\\Desktop");
        Export.employeeExport(ScheduleCreator.employeeSet, "C:\\Users\\leoob\\Desktop");

        DBUtils.closeConnection(connection);
    }
}