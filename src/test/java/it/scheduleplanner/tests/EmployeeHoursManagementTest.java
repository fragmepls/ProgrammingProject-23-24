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

@DisplayName("Employee Hours Management Tests")
class EmployeeHoursManagementTest {
    private ShiftScheduleInterface mockCalendar;
    private List<Employee> employees;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        // Clear any existing employees from previous tests
        ScheduleCreator.employeeSet.clear();

        mockCalendar = new ScheduleMocks.MockShiftSchedule();
        employees = new ArrayList<>();
        testDate = LocalDate.of(2024, 1, 15);

        // Create test employees with different contract hours
        Employee emp1 = new Employee("John", true, "MONDAY", true);  // Full time
        Employee emp2 = new Employee("Jane", false, "TUESDAY", true); // Full time
        Employee emp3 = new Employee("Bob", true, "WEDNESDAY", false); // Part time

        // Set initial working hours
        emp1.setWorkingHours(40);
        emp2.setWorkingHours(40);
        emp3.setWorkingHours(20);

        employees = Arrays.asList(emp1, emp2, emp3);
    }

    @Test
    @DisplayName("Should track hours after shift assignment and deletion")
    void testTrackHours() {
        Employee employee = employees.get(0);
        int initialHours = employee.getWorkingHours();

        // Add a full shift and verify hours deduction
        mockCalendar.getDay(testDate).addEmployee(employee, Shift.FULL);
        assertEquals(initialHours - 8, employee.getWorkingHours(),
                "Hours should be deducted after adding full shift");

        // Remove the shift and verify hours restoration
        mockCalendar.getDay(testDate).removeEmployee(employee);
        assertEquals(initialHours, employee.getWorkingHours(),
                "Hours should be restored after shift removal");
    }

    @Test
    @DisplayName("Should verify hours deduction for different shift types")
    void testHoursDeductionByShiftType() {
        Employee employee = employees.get(0);
        int initialHours = employee.getWorkingHours();

        // Test full shift
        mockCalendar.getDay(testDate).addEmployee(employee, Shift.FULL);
        assertEquals(initialHours - 8, employee.getWorkingHours(),
                "Full shift should deduct 8 hours");

        // Remove previous shift and reset hours
        mockCalendar.getDay(testDate).removeEmployee(employee);
        employee.setWorkingHours(initialHours);

        // Test morning shift
        mockCalendar.getDay(testDate).addEmployee(employee, Shift.MORNING);
        assertEquals(initialHours - 4, employee.getWorkingHours(),
                "Morning shift should deduct 4 hours");
    }

    @Test
    @DisplayName("Should handle insufficient hours for shift assignment")
    void testInsufficientHours() {
        Employee employee = employees.get(0);

        // Set hours too low for full shift
        employee.setWorkingHours(7);

        // Attempt to assign full shift
        mockCalendar.getDay(testDate).addEmployee(employee, Shift.FULL);

        // Verify employee wasn't assigned
        assertFalse(mockCalendar.getDay(testDate).hasEmployee(employee),
                "Employee should not be assigned with insufficient hours");
    }
}