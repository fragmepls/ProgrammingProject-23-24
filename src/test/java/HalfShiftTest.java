import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.planner.EmployeeComparator;
import it.scheduleplanner.export.Shift;
import static it.scheduleplanner.planner.ScheduleCreator.employeeSet;



import static it.scheduleplanner.planner.ScheduleCreator.employeeSet;
import static org.junit.jupiter.api.Assertions.*;

public class HalfShiftTest {


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

    @Test
    public void testAssignShifts() {
        ArrayList<String> employees = new ArrayList<>();
        for (Employee employee : employeeSet) {
            String employeeName = employee.getName();
            employees.add(employeeName);
        }

        System.out.println(employees);
        LocalDate testDate = LocalDate.of(2024, 6, 5); // Wednesday
        int numberOfEmployeesPerDay = 2;

        // Run the method under test
        Map<Employee, Shift> shifts = EmployeeComparator.getNext(employeeSet, testDate, numberOfEmployeesPerDay, DayOfWeek.THURSDAY);

        // Verify the expected shifts
        assertEquals(4, shifts.size(), "Expected 2 employees with shifts (4 shifts total)");

        // Ensure shifts are correctly assigned
        int morningCount = 0;
        int afternoonCount = 0;

        for (Map.Entry<Employee, Shift> entry : shifts.entrySet()) {
            if (entry.getValue() == Shift.MORNING) {
                morningCount++;
            } else if (entry.getValue() == Shift.AFTERNOON) {
                afternoonCount++;
            }
        }

        assertEquals(2, morningCount, "Expected 2 morning shifts");
        assertEquals(2, afternoonCount, "Expected 2 afternoon shifts");
    }
}
