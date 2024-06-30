import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.utils.Vacation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Vacation class.
 */
public class VacationTest {

    /**
     * Test case to verify vacation functionality.
     */
    @Test
    void testVacation() {
        // Create a new employee
        Employee employee = new Employee("John", false, "MONDAY", true);

        // Define vacation dates
        LocalDate vacationStart = LocalDate.of(2024, 6, 1);
        LocalDate vacationEnd = LocalDate.of(2024, 6, 7);

        // Create a vacation instance
        Vacation vacation = new Vacation(vacationStart, vacationEnd);

        // Add the vacation to the employee
        employee.addVacation(vacation);

        // Test if the employee is on vacation for a date within the vacation period
        assertTrue(employee.isOnVacation(LocalDate.of(2024, 6, 3)), "Employee should be on vacation on June 3, 2024");

        // Test if the employee is not on vacation for a date outside the vacation period
        assertFalse(employee.isOnVacation(LocalDate.of(2024, 6, 10)), "Employee should not be on vacation on June 10, 2024");
    }
}
