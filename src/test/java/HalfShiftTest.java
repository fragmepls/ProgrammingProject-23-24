import it.scheduleplanner.planner.InsufficientEmployeesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.planner.EmployeeComparator;
import it.scheduleplanner.export.Shift;

import static it.scheduleplanner.planner.ScheduleCreator.employeeSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for verifying the functionality of assigning half shifts to employees.
 */
public class HalfShiftTest {

    /**
     * Sets up mock employees with specific working hours for testing.
     */
    @BeforeEach
    public void setUp() {
        // Creating mock employees
        Employee emp1 = new Employee("Doe", true, "MONDAY", false);
        emp1.setWorkingHours(6); // Ensure this employee can work a morning shift but not a full shift

        Employee emp2 = new Employee("Smith", true, "TUESDAY", false);
        emp2.setWorkingHours(6); // Ensure this employee can work a morning shift but not a full shift

        Employee emp3 = new Employee("Jane", true, "WEDNESDAY", false);
        emp3.setWorkingHours(6); // Ensure this employee can work a morning shift but not a full shift

        Employee emp4 = new Employee("Doe", true, "MONDAY", false);
        emp4.setWorkingHours(4); // Ensure this employee can work an afternoon shift

        Employee emp5 = new Employee("Smith", true, "TUESDAY", false);
        emp5.setWorkingHours(4); // Ensure this employee can work an afternoon shift
    }

    /**
     * Test case to verify assigning half shifts (morning and afternoon) to employees for a specific date.
     *
     * @throws InsufficientEmployeesException if there are not enough employees available to cover the required shifts
     */
    @Test
    public void testAssignShifts() throws InsufficientEmployeesException {
        // Extract employee names for logging purposes
        ArrayList<String> employees = new ArrayList<>();
        for (Employee employee : employeeSet) {
            String employeeName = employee.getName();
            employees.add(employeeName);
        }

        // Log the extracted employee names
        System.out.println(employees);

        // Define test parameters
        LocalDate testDate = LocalDate.of(2024, 6, 5); // Wednesday
        int numberOfEmployeesPerDay = 2;

        // Run the method under test
        Map<Employee, Shift> shifts = EmployeeComparator.getNext(employeeSet, testDate, numberOfEmployeesPerDay, DayOfWeek.THURSDAY);

        // Verify the expected shifts
        assertEquals(4, shifts.size(), "Expected 2 employees with shifts (4 shifts total)");

        // Count morning and afternoon shifts
        int morningCount = 0;
        int afternoonCount = 0;

        for (Map.Entry<Employee, Shift> entry : shifts.entrySet()) {
            if (entry.getValue() == Shift.MORNING) {
                morningCount++;
            } else if (entry.getValue() == Shift.AFTERNOON) {
                afternoonCount++;
            }
        }

        // Assert the counts of morning and afternoon shifts
        assertEquals(2, morningCount, "Expected 2 morning shifts");
        assertEquals(2, afternoonCount, "Expected 2 afternoon shifts");
    }
}
