package it.scheduleplanner.tests;

import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for verifying the functionality of adding employees to the ScheduleCreator's employee set.
 */
public class EmployeeListTest {

    @BeforeEach
    void setUp() {
        // Clear existing employees and IDs
        ScheduleCreator.employeeSet = new HashSet<>();
        ScheduleCreator.employeeSetByID = new HashSet<>();
    }

    @Test
    @DisplayName("Should correctly add employees and assign sequential IDs")
    public void employeeListTest() {
        // Create test employees
        createTestEmployee("John", true, "monday", true);
        createTestEmployee("Jane", false, "tuesday", false);
        createTestEmployee("Jack", true, "wednesday", false);
        createTestEmployee("Jill", false, "friday", true);
        createTestEmployee("Jake", true, "saturday", false);
        createTestEmployee("Jess", true, "monday", true);
        createTestEmployee("Jerry", true, "tuesday", true);
        createTestEmployee("Janet", false, "monday", false);
        createTestEmployee("Jasmine", true, "sunday", true);
        createTestEmployee("James", false, "tuesday", false);

        // Verify correct number of employees
        assertEquals(10, ScheduleCreator.employeeSet.size(),
                "Expected 10 employees in the set");
        assertEquals(10, ScheduleCreator.employeeSetByID.size(),
                "Expected 10 employee IDs in the set");

        // Verify ID assignment
        for (Employee emp : ScheduleCreator.employeeSet) {
            assertTrue(emp.getEmployeeId() >= 0 && emp.getEmployeeId() < 10,
                    "Employee ID should be between 0 and 9, but was: " + emp.getEmployeeId());
            assertTrue(ScheduleCreator.employeeSetByID.contains(emp.getEmployeeId()),
                    "Employee ID set should contain ID: " + emp.getEmployeeId());
        }

        // Verify no duplicate IDs
        assertEquals(ScheduleCreator.employeeSet.size(), ScheduleCreator.employeeSetByID.size(),
                "Number of employees should match number of unique IDs");
    }

    private Employee createTestEmployee(String name, boolean weekendWorker, String freeDay, boolean fullTime) {
        Employee employee = new Employee(name, weekendWorker, freeDay, fullTime);
        ScheduleCreator.addEmployee(employee);
        return employee;
    }
}