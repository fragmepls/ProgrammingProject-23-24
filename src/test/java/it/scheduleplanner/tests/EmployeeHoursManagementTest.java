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
    @DisplayName("Should track missing hours after shift deletion")
    void testTrackMissingHours() {
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
    @DisplayName("Should find suitable replacement for shift")
    void testFindBestReplacementForShift() {
        Employee originalEmployee = employees.get(0);
        Employee potentialReplacement = employees.get(1);

        // Setup conditions
        originalEmployee.setWorkingHours(4); // Too few hours
        potentialReplacement.setWorkingHours(40); // Sufficient hours

        Employee replacement = findBestReplacement(employees, Shift.FULL, testDate);

        assertNotNull(replacement, "Should find a replacement");
        assertNotEquals(originalEmployee, replacement,
                "Replacement should not be the original employee");
        assertTrue(replacement.getWorkingHours() >= 8,
                "Replacement should have sufficient hours");
    }

    @Test
    @DisplayName("Should optimize schedule after deletion")
    void testOptimizeScheduleAfterDeletion() {
        Employee employee = employees.get(0);
        int initialHours = employee.getWorkingHours();

        // Add full shift
        mockCalendar.getDay(testDate).addEmployee(employee, Shift.FULL);
        assertEquals(initialHours - 8, employee.getWorkingHours(),
                "Hours should be deducted after adding shift");

        // Remove shift
        mockCalendar.getDay(testDate).removeEmployee(employee);
        assertEquals(initialHours, employee.getWorkingHours(),
                "Hours should be restored after optimization");
    }

    @Test
    @DisplayName("Should balance hours across employees")
    void testBalanceHoursAcrossEmployees() {
        Employee fullTimeEmp = employees.get(0);
        Employee partTimeEmp = employees.get(2);

        // Set up unbalanced hours
        fullTimeEmp.setWorkingHours(30); // Under hours for full-time
        partTimeEmp.setWorkingHours(25); // Over hours for part-time

        balanceHours(employees);

        int fullTimeHours = fullTimeEmp.getWorkingHours();
        int partTimeHours = partTimeEmp.getWorkingHours();

        assertTrue(fullTimeHours > 30,
                "Full-time employee hours should be increased: " + fullTimeHours);
        assertTrue(partTimeHours <= 20,
                "Part-time employee hours should be decreased: " + partTimeHours);
    }

    // Helper method to find best replacement
    private Employee findBestReplacement(List<Employee> employees, Shift shift, LocalDate date) {
        int requiredHours = (shift == Shift.FULL) ? 8 : 4;

        return employees.stream()
                .filter(e -> e.getWorkingHours() >= requiredHours)
                .filter(e -> !e.isOnVacation(date))
                .min(Comparator.comparingInt(Employee::getOverTimeHours))
                .orElse(null);
    }

    // Helper method to balance hours
    private void balanceHours(List<Employee> employees) {
        Map<Employee, Integer> targetHours = new HashMap<>();
        for (Employee emp : employees) {
            targetHours.put(emp, emp.isFullTimeWorker() ? 40 : 20);
        }

        boolean balanced;
        do {
            balanced = true;
            for (Employee emp : employees) {
                int current = emp.getWorkingHours();
                int target = targetHours.get(emp);

                if (current < target) {
                    // Find donor with excess hours
                    Optional<Employee> donor = employees.stream()
                            .filter(other -> other != emp)
                            .filter(other -> other.getWorkingHours() > targetHours.get(other))
                            .findFirst();

                    if (donor.isPresent()) {
                        Employee d = donor.get();
                        int transfer = Math.min(4, Math.min(target - current,
                                d.getWorkingHours() - targetHours.get(d)));

                        if (transfer > 0) {
                            d.setWorkingHours(d.getWorkingHours() - transfer);
                            emp.setWorkingHours(emp.getWorkingHours() + transfer);
                            balanced = false;
                        }
                    }
                }
            }
        } while (!balanced);
    }
}