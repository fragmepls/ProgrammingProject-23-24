package it.scheduleplanner.tests;
import it.scheduleplanner.planner.EmployeeComparator;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.utils.Vacation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the EmployeeComparator's isAvailable method.
 */
public class IsAvailableTest {

    /**
     * Test case to verify availability based on vacation periods.
     */
    @Test
    void testIsAvailableByVacation() {
        // Create an employee with a defined work schedule
        Employee employee = new Employee("John", false, "MONDAY", true);

        // Define vacation dates
        LocalDate vacationStart = LocalDate.of(2024, 6, 1);
        LocalDate vacationEnd = LocalDate.of(2024, 6, 7);

        // Create a vacation instance
        Vacation vacation = new Vacation(vacationStart, vacationEnd);

        // Add the vacation to the employee
        employee.addVacation(vacation);

        // Test if employee is available for a date outside the vacation period
        assertTrue(EmployeeComparator.isAvailable(employee, LocalDate.of(2024, 8, 2)));

        // Test if employee is not available for a date inside the vacation period
        assertFalse(EmployeeComparator.isAvailable(employee, LocalDate.of(2024, 6, 4)));
    }

    /**
     * Test case to verify availability based on the employee's free day.
     */
    @Test
    void testIsAvailableByFreeDay() {
        // Create an employee with a defined work schedule
        Employee employee = new Employee("John", false, "MONDAY", true);

        // Test if employee is available on another day than Monday
        assertTrue(EmployeeComparator.isAvailable(employee, LocalDate.of(2024, 6, 5)));

        // Test if employee is not available on a Monday
        assertFalse(EmployeeComparator.isAvailable(employee, LocalDate.of(2024, 6, 3)));
    }

    /**
     * Test case to verify availability based on the employee's weekend work schedule.
     */
    @Test
    void testIsAvailableByWeekend() {
        // Create an employee with a defined work schedule
        Employee employee = new Employee("John", false, "MONDAY", true);

        // Test if employee is available during the week
        assertTrue(EmployeeComparator.isAvailable(employee, LocalDate.of(2024, 6, 5)));

        // Test if employee is not available during the weekend
        assertFalse(EmployeeComparator.isAvailable(employee, LocalDate.of(2024, 6, 2)));
    }
}
