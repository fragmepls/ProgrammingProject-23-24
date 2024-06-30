import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for verifying the functionality of adding employees to the ScheduleCreator's employee set.
 */
public class EmployeeListTest {

    /**
     * Test case to verify adding employees to the ScheduleCreator's employee set.
     */
    @Test
    public void employeeListTest(){

        // Create employee objects
        Employee employee1 = new Employee("John", true, "monday", true);
        Employee employee2 = new Employee("Jane", false, "tuesday", false);
        Employee employee3 = new Employee("Jack", true, "wednesday", false);
        Employee employee4 = new Employee("Jill", false, "friday", true);
        Employee employee5 = new Employee("Jake", true, "saturday", false);
        Employee employee6 = new Employee("Jess", true, "monday", true);
        Employee employee7 = new Employee("Jerry", true, "tuesday", true);
        Employee employee8 = new Employee("Janet", false, "monday", false);
        Employee employee9 = new Employee("Jasmine", true, "sunday", true);
        Employee employee10 = new Employee("James", false, "tuesday", false);

        // Define the expected set of employees
        Set<Employee> expectedEmployees = new HashSet<>();
        expectedEmployees.add(employee1);
        expectedEmployees.add(employee2);
        expectedEmployees.add(employee3);
        expectedEmployees.add(employee4);
        expectedEmployees.add(employee5);
        expectedEmployees.add(employee6);
        expectedEmployees.add(employee7);
        expectedEmployees.add(employee8);
        expectedEmployees.add(employee9);
        expectedEmployees.add(employee10);

        // Verify that all employees are added to the ScheduleCreator's employee set
        assertEquals(expectedEmployees.size(), ScheduleCreator.employeeSet.size());
        assertEquals(expectedEmployees, ScheduleCreator.employeeSet);
    }

}
