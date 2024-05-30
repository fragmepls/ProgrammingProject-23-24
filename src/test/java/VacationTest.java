import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.utils.Vacation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


public class VacationTest {

    @Test
    void testVacation() {
        Employee employee = new Employee("John", false, "MONDAY", true);

        // Define vacation dates
        LocalDate vacationStart = LocalDate.of(2024, 6, 1);
        LocalDate vacationEnd = LocalDate.of(2024, 6, 7);

        // Create a vacation instance
        Vacation vacation = new Vacation(vacationStart, vacationEnd);

        // Add the vacation to the employee
        employee.addVacation(vacation);

        // Test if the employee is on vacation for a date within the vacation period
        assertTrue(employee.isOnVacation(LocalDate.of(2024, 6, 3)));

        // Test if the employee is not on vacation for a date outside the vacation period
        assertFalse(employee.isOnVacation(LocalDate.of(2024, 6, 10)));
    }
}


