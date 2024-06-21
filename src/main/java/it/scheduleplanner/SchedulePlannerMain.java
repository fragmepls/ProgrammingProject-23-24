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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class SchedulePlannerMain {

    public static void main(String[] args) throws SQLException {
        DBUtils.initializeDatabase();
        Connection connection = DBUtils.getConnection();

        ArrayList<Employee> employeeList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        int numberOfEmployees;
        while (true) {
            try {
                System.out.print("Enter the number of employees: ");
                numberOfEmployees = scanner.nextInt();
                scanner.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }

        for (int i = 0; i < numberOfEmployees; i++) {
            String name;
            boolean isWeekendWorker;
            String freeDay;
            boolean isFullTime;

            while (true) {
                try {
                    System.out.println("Enter details for employee " + (i + 1) + ":");
                    System.out.print("Name: ");
                    name = scanner.nextLine();
                    System.out.print("Is Weekend Worker (true/false): ");
                    isWeekendWorker = scanner.nextBoolean();
                    scanner.nextLine();
                    System.out.print("Free Day: ");
                    freeDay = scanner.nextLine();
                    System.out.print("Is Full Time (true/false): ");
                    isFullTime = scanner.nextBoolean();
                    scanner.nextLine();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter the correct values.");
                    scanner.nextLine();
                }
            }

            Employee employee = new Employee(name, isWeekendWorker, freeDay, isFullTime);
            SQLQueries.insertEmployee(connection, employee);
            employeeList.add(employee);

            LocalDate startDate;
            LocalDate endDate;

            while (true) {
                try {
                    System.out.println("Enter vacation details for " + name + ":");
                    System.out.print("Start Date (YYYY-MM-DD): ");
                    startDate = LocalDate.parse(scanner.nextLine());
                    System.out.print("End Date (YYYY-MM-DD): ");
                    endDate = LocalDate.parse(scanner.nextLine());
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please enter the date in the format YYYY-MM-DD.");
                }
            }

            Vacation vacation = new Vacation(startDate, endDate);
            employee.addVacation(vacation);
        }

        for (Employee e : employeeList) {
            ScheduleCreator.addEmployee(e);
        }

        int numberOfEmployeesPerDay;
        while (true) {
            try {
                System.out.print("Enter number of employees per day: ");
                numberOfEmployeesPerDay = scanner.nextInt();
                scanner.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }

        LocalDate beginDate;
        LocalDate endDate;

        while (true) {
            try {
                System.out.print("Enter begin date (YYYY-MM-DD): ");
                beginDate = LocalDate.parse(scanner.nextLine());
                System.out.print("Enter end date (YYYY-MM-DD): ");
                endDate = LocalDate.parse(scanner.nextLine());
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in the format YYYY-MM-DD.");
            }
        }

        ShiftScheduleInterface calendar = ScheduleCreator.create(beginDate, endDate, numberOfEmployeesPerDay, false, DayOfWeek.SATURDAY);
        System.out.println(calendar);
        Export.CSVExport(calendar, "C:\\Users\\leoob\\Desktop");
        Export.employeeExport(ScheduleCreator.employeeSet, "C:\\Users\\leoob\\Desktop");

        DBUtils.closeConnection(connection);
    }
}