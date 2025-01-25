package it.scheduleplanner.tests;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.planner.ScheduleCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying employee hours management functionality.
 * Tests how working hours are tracked, modified, and validated during shift assignments.
 */
@DisplayName("Employee Hours Management Tests")
class EmployeeHoursManagementTest {
    private ShiftScheduleInterface mockCalendar;  // Mock calendar for testing
    private List<Employee> employees;             // List of test employees
    private LocalDate testDate;                   // Test date for shift assignments

    /**
     * Sets up test environment before each test.
     * Creates mock calendar and test employees with different contracts.
     */
    @BeforeEach
    void setUp() {
        // Clear any existing employees to start fresh
        ScheduleCreator.employeeSet.clear();

        // Initialize test components
        mockCalendar = new ScheduleMocks.MockShiftSchedule();
        employees = new ArrayList<>();
        testDate = LocalDate.of(2024, 1, 15);

        // Create test employees with different contract types
        Employee emp1 = new Employee("John", true, "MONDAY", true);
        Employee emp2 = new Employee("Jane", false, "TUESDAY", true);
        Employee emp3 = new Employee("Bob", true, "WEDNESDAY", false);

        // Set initial working hours based on contract type
        emp1.setWorkingHours(40);  // Full time = 40 hours
        emp2.setWorkingHours(40);
        emp3.setWorkingHours(20);  // Part time = 20 hours

        employees = Arrays.asList(emp1, emp2, emp3);
    }

    /**
     * Tests if hours are correctly tracked when adding and removing shifts.
     * Verifies that:
     * - Hours are deducted when assigning a shift
     * - Hours are restored when removing a shift
     */
    @Test
    @DisplayName("Should track hours after shift assignment and deletion")
    void testTrackHours() {
        Employee employee = employees.get(0);
        int initialHours = employee.getWorkingHours();

        // Test hour deduction for full shift
        mockCalendar.getDay(testDate).addEmployee(employee, Shift.FULL);
        assertEquals(initialHours - 8, employee.getWorkingHours(),
                "Hours should be deducted after adding full shift");

        // Test hour restoration after shift removal
        mockCalendar.getDay(testDate).removeEmployee(employee);
        assertEquals(initialHours, employee.getWorkingHours(),
                "Hours should be restored after shift removal");
    }

    /**
     * Tests if hours are correctly deducted for different shift types.
     * Verifies that:
     * - Full shift deducts 8 hours
     * - Morning/Afternoon shift deducts 4 hours
     */
    @Test
    @DisplayName("Should verify hours deduction for different shift types")
    void testHoursDeductionByShiftType() {
        Employee employee = employees.get(0);
        int initialHours = employee.getWorkingHours();

        // Test full shift (8 hours)
        mockCalendar.getDay(testDate).addEmployee(employee, Shift.FULL);
        assertEquals(initialHours - 8, employee.getWorkingHours(),
                "Full shift should deduct 8 hours");

        // Reset hours for next test
        mockCalendar.getDay(testDate).removeEmployee(employee);
        employee.setWorkingHours(initialHours);

        // Test morning shift (4 hours)
        mockCalendar.getDay(testDate).addEmployee(employee, Shift.MORNING);
        assertEquals(initialHours - 4, employee.getWorkingHours(),
                "Morning shift should deduct 4 hours");
    }

    /**
     * Tests if shift assignment is prevented when employee has insufficient hours.
     * Verifies that employees cannot be assigned shifts they don't have hours for.
     */
    @Test
    @DisplayName("Should handle insufficient hours for shift assignment")
    void testInsufficientHours() {
        Employee employee = employees.get(0);

        // Set hours too low for full shift
        employee.setWorkingHours(7);  // Not enough for full shift (8 hours)

        // Attempt to assign full shift
        mockCalendar.getDay(testDate).addEmployee(employee, Shift.FULL);

        // Verify assignment was prevented
        assertFalse(mockCalendar.getDay(testDate).hasEmployee(employee),
                "Employee should not be assigned with insufficient hours");
    }
}