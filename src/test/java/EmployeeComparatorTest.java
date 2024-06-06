import it.scheduleplanner.planner.EmployeeComparator;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.export.Shift;
import it.scheduleplanner.utils.Vacation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import static it.scheduleplanner.planner.ScheduleCreator.employeeSet;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeComparatorTest {

    @Test
    void testGetNext() {
        // Initialize employees
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

        // Set up vacations
        Vacation juneVacation1 = new Vacation(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 10));
        Vacation juneVacation2 = new Vacation(LocalDate.of(2024, 6, 11), LocalDate.of(2024, 6, 20));
        Vacation juneVacation3 = new Vacation(LocalDate.of(2024, 6, 21), LocalDate.of(2024, 6, 30));

        employee1.addVacation(juneVacation1);
        employee2.addVacation(juneVacation2);
        employee3.addVacation(juneVacation1);
        employee4.addVacation(juneVacation3);
        employee5.addVacation(juneVacation1);
        employee6.addVacation(juneVacation2);
        employee7.addVacation(juneVacation2);

        // Add employees to list
        ArrayList<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee1);
        employeeList.add(employee2);
        employeeList.add(employee3);
        employeeList.add(employee4);
        employeeList.add(employee5);
        employeeList.add(employee6);
        employeeList.add(employee7);
        employeeList.add(employee8);
        employeeList.add(employee9);
        employeeList.add(employee10);

        // Define the date and the number of employees needed per day
        LocalDate testDate = LocalDate.of(2024, 6, 3); // A Monday
        int numberOfEmployeesPerDay = 1;

        // Call the getNext method
        Map<Employee, Shift> result = EmployeeComparator.getNext(employeeSet, testDate, numberOfEmployeesPerDay);

        // Assert the results
        assertNotNull(result);
        assertEquals(1, result.size());
        Map.Entry<Employee, Shift> entry = result.entrySet().iterator().next();
        Employee assignedEmployee = entry.getKey();
        Shift assignedShift = entry.getValue();

        // Ensure the assigned employee is not on vacation and is available on the given date
        assertFalse(assignedEmployee.isOnVacation(testDate));
        assertTrue(EmployeeComparator.isAvailable(assignedEmployee, testDate));

        // Print the assigned employee and their shift for visual verification
        System.out.println("Assigned Employee: " + assignedEmployee.getName() + ", Shift: " + assignedShift);

        // Additional assertions can be added as needed
    }
    @Test
    void testMorningShiftAssignment() {
        LocalDate date = LocalDate.of(2023, 6, 5); // Set a specific date

        // Assume we need 2 employees per day
        int numberOfEmployeesPerDay = 2;

        // Call the method
        Map<Employee, Shift> result = EmployeeComparator.getNext(employeeSet, date, numberOfEmployeesPerDay);

        // Assert the results
        assertNotNull(result, "The result should not be null");
        assertEquals(numberOfEmployeesPerDay, result.size(), "The number of assigned employees should be correct");

        // Check if one of the employees is assigned to the MORNING shift
        boolean morningShiftAssigned = result.values().contains(Shift.MORNING);
        assertTrue(morningShiftAssigned, "At least one employee should be assigned to the MORNING shift");

        // Optionally print the result to manually verify the output
        result.forEach((employee, shift) -> {
            System.out.println(employee.getName() + " is assigned to " + shift);
        });
    }
}
