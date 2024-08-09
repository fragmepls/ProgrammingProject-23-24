package it.scheduleplanner.tests;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.planner.InsufficientEmployeesException;
import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.utils.Vacation;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static it.scheduleplanner.planner.ScheduleCreator.employeeSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the ScheduleCreator class.
 */
public class ScheduleCreatorTest {

    /**
     * Test case to verify date list generation with weekend open or closed.
     */
    @Test
    public void testGenerateDateListWeekendOpen() {
        // Define date range
        LocalDate begin = LocalDate.of(2024, 6, 1); // June 1, 2024
        LocalDate end = LocalDate.of(2024, 6, 7);   // June 7, 2024

        // Generate date list with weekends open
        List<LocalDate> dateList = ScheduleCreator.generateDateList(begin, end, true, null);
        assertEquals(7, dateList.size(), "Should include all days from June 1 to June 7");

        // Generate date list with weekends open, excluding Wednesday
        dateList = ScheduleCreator.generateDateList(begin, end, true, DayOfWeek.WEDNESDAY);
        assertEquals(6, dateList.size(), "Should exclude Wednesday, June 5, 2024");
    }

    /**
     * Test case to verify date list generation with weekend closed.
     */
    @Test
    public void testGenerateDateListWeekendClosed() {
        // Define date range
        LocalDate begin = LocalDate.of(2024, 6, 1); // June 1, 2024
        LocalDate end = LocalDate.of(2024, 6, 7);   // June 7, 2024

        // Generate date list with weekends closed
        List<LocalDate> dateList = ScheduleCreator.generateDateList(begin, end, false, null);
        assertEquals(5, dateList.size(), "Should exclude weekends (June 1 and June 2)");

        // Generate date list with weekends closed, excluding Wednesday
        dateList = ScheduleCreator.generateDateList(begin, end, false, DayOfWeek.WEDNESDAY);
        assertEquals(4, dateList.size(), "Should exclude Wednesday, Saturday, and Sunday");
    }

    /**
     * Test case to verify schedule creation.
     */
    @Test
    public void testCreateSchedule() throws InsufficientEmployeesException {
        // Add mock employees to the employeeSet
        Employee emp1 = new Employee("Doe", true, "monday", false);
        Employee emp2 = new Employee("Smith", true, "TUESDAY", false);
        Employee employee3 = new Employee("Jack", true, "wednesday", false);
        Employee employee4 = new Employee("Jill", true, "friday", false);
        Employee employee5 = new Employee("Bob", true, "saturday", false);
        Employee employee6 = new Employee("Katherine", true, "sunday", false);
        Employee employee7 = new Employee("Jason", true, "monday", false);
        Employee employee8 = new Employee("Jan", false, "tuesday", true);
        Employee employee9 = new Employee("Lara", true, "saturday", false);
        Employee employee10 = new Employee("Sara", false, "wednesday", false);

        // Create a list of employee names for verification
        ArrayList<String> employees = new ArrayList<>();
        for (Employee employee : employeeSet) {
            employees.add(employee.getName());
        }

        // Verify the list of employees
        System.out.println(employees);

        // Define schedule creation parameters
        LocalDate begin = LocalDate.of(2024, 6, 1); // June 1, 2024
        LocalDate end = LocalDate.of(2024, 6, 20);   // June 20, 2024

        // Create the shift schedule
        ShiftScheduleInterface schedule = ScheduleCreator.create(begin, end, 3, true, null);
        System.out.println(schedule); // Output the created schedule (implementation-dependent)
    }

    @Test
    public void testCreateSchedule2() throws InsufficientEmployeesException {
        // Add mock employees to the employeeSet
        Employee emp1 = new Employee("Doe", true, "monday", false);
        Employee emp2 = new Employee("Smith", true, "TUESDAY", true);
        Employee emp3 = new Employee("Jack", true, "wednesday", true);


        // Create a list of employee names for verification
        ArrayList<String> employees = new ArrayList<>();
        for (Employee employee : employeeSet) {
            employees.add(employee.getName());
        }

        // Verify the list of employees
        System.out.println(employees);

        Vacation vacation = new Vacation(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 7));
        emp1.addVacation(vacation);

        // Define schedule creation parameters
        LocalDate begin = LocalDate.of(2024, 7, 1); // June 1, 2024
        LocalDate end = LocalDate.of(2024, 7, 31);   // June 20, 2024

        // Create the shift schedule
        ShiftScheduleInterface schedule = ScheduleCreator.create(begin, end, 2, true, DayOfWeek.WEDNESDAY);
        System.out.println(schedule); // Output the created schedule (implementation-dependent)
    }
}
