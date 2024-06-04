package it.scheduleplanner;

import it.scheduleplanner.dbutils.DBUtils;
import it.scheduleplanner.dbutils.SQLQueries;
import it.scheduleplanner.export.*;
import it.scheduleplanner.planner.EmployeeComparator;
import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.utils.EmployeeInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class SchedulePlannerMain {

    public static void main(String[] args) throws SQLException {
        DBUtils.initializeDatabase();
        Connection connection = DBUtils.getConnection();

        ArrayList<Employee> employees = new ArrayList<>();
        LocalDate date = LocalDate.now();

        Employee employee1 = SQLQueries.selectEmployeeById(connection, 1);
        employees.add(employee1);
        Employee employee2 = SQLQueries.selectEmployeeById(connection, 2);
        employees.add(employee2);
        Employee employee3 = SQLQueries.selectEmployeeById(connection, 3);
        employees.add(employee3);
        Employee employee4 = SQLQueries.selectEmployeeById(connection, 4);
        employees.add(employee4);
        Employee employee5 = SQLQueries.selectEmployeeById(connection, 5);
        employees.add(employee5);
        Employee employee6 = SQLQueries.selectEmployeeById(connection, 6);
        employees.add(employee6);

        for (Employee employee : employees) {
            ScheduleCreator.addEmployee(employee);
        }

        ShiftScheduleInterface shift = ScheduleCreator.create(LocalDate.now(), LocalDate.of(2024, 7, 30), 2, false, DayOfWeek.SUNDAY);

        System.out.println(shift.getSchedule().size());

        for (Map.Entry<LocalDate, ShiftDayInterface> entry : shift.getSchedule().entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        //ShiftDayInterface day1 = new FixedShiftDay();
        //ShiftDayInterface day2 = new FixedShiftDay();

        //day1.addEmployee(employee1);
        //day1.addEmployee(employee2);
        //day1.addEmployee(employee3);
        //day1.addEmployee(employee4, Shift.AFTERNOON);
        //day2.addEmployee(employee5, Shift.MORNING);
        //day2.addEmployee(employee6, Shift.MORNING);

        //FixedShiftsSchedule calendar = new FixedShiftsSchedule(date.minusDays(10));

        //calendar.addDay(date, day1);
        //calendar.addDay(date.plusDays(2), day2);

        //Export.CSVExport(calendar, "/Users/noahthanei/Desktop");
        //Export.employeeExport(employees, "/Users/noahthanei/Desktop");

        connection.close();

    }

}
