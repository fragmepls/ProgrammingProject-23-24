import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.planner.ScheduleCreator;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class ScheduleCreatorTest2 {

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testCreateSchedule() {

        ScheduleCreator.employeeSet.clear();
        ScheduleCreator.addEmployee(new Employee("Doe", true, "MONDAY", false));
        ScheduleCreator.addEmployee(new Employee("Smith", false, "TUESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jack", true, "WEDNESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jill", false, "FRIDAY", false));
        ScheduleCreator.addEmployee(new Employee("Bob", true, "SATURDAY", false));
        ScheduleCreator.addEmployee(new Employee("Katherine", false, "SUNDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jason", true, "MONDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jan", false, "TUESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Lara", true, "SATURDAY", false));
        ScheduleCreator.addEmployee(new Employee("Sara", false, "WEDNESDAY", false));

        LocalDate begin = LocalDate.of(2024, 6, 2);
        LocalDate end = LocalDate.of(2024, 6, 30);

        ShiftScheduleInterface schedule = ScheduleCreator.create(begin, end, 3, true, null);
        printSchedule(schedule);
    }

    @Test
    public void testCreateScheduleWithRestDay() {

        ScheduleCreator.employeeSet.clear();
        ScheduleCreator.addEmployee(new Employee("Doe", true, "MONDAY", false));
        ScheduleCreator.addEmployee(new Employee("Smith", false, "TUESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jack", true, "WEDNESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jill", false, "FRIDAY", false));
        ScheduleCreator.addEmployee(new Employee("Bob", true, "SATURDAY", false));
        ScheduleCreator.addEmployee(new Employee("Katherine", false, "SUNDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jason", true, "MONDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jan", false, "TUESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Lara", true, "SATURDAY", false));
        ScheduleCreator.addEmployee(new Employee("Sara", false, "WEDNESDAY", false));

        LocalDate begin = LocalDate.of(2024, 6, 2);
        LocalDate end = LocalDate.of(2024, 6, 30);

        ShiftScheduleInterface schedule = ScheduleCreator.create(begin, end, 3, true, DayOfWeek.WEDNESDAY);
        printSchedule(schedule);
    }

    @Test
    public void testCreateScheduleWithWeekendClosed() {
        LocalDate begin = LocalDate.of(2024, 6, 2);
        LocalDate end = LocalDate.of(2024, 6, 30);

        ScheduleCreator.employeeSet.clear();
        ScheduleCreator.addEmployee(new Employee("Doe", true, "MONDAY", false));
        ScheduleCreator.addEmployee(new Employee("Smith", false, "TUESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jack", true, "WEDNESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jill", false, "FRIDAY", false));
        ScheduleCreator.addEmployee(new Employee("Bob", true, "SATURDAY", false));
        ScheduleCreator.addEmployee(new Employee("Katherine", false, "SUNDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jason", true, "MONDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jan", false, "TUESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Lara", true, "SATURDAY", false));
        ScheduleCreator.addEmployee(new Employee("Sara", false, "WEDNESDAY", false));

        ShiftScheduleInterface schedule = ScheduleCreator.create(begin, end, 6, false, null);
        printSchedule(schedule);
    }

    @Test
    public void testCreateScheduleWithOvertime() {

        ScheduleCreator.employeeSet.clear();
        ScheduleCreator.addEmployee(new Employee("Doe", true, "MONDAY", false));
        ScheduleCreator.addEmployee(new Employee("Smith", false, "TUESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jack", true, "WEDNESDAY", false));
        ScheduleCreator.addEmployee(new Employee("Jill", false, "FRIDAY", false));
        ScheduleCreator.addEmployee(new Employee("Bob", true, "SATURDAY", false));
        ScheduleCreator.addEmployee(new Employee("Katherine", false, "SUNDAY", false));

        LocalDate begin = LocalDate.of(2024, 6, 4);
        LocalDate end = LocalDate.of(2024, 6, 9);


       for (Employee employee : ScheduleCreator.employeeSet) {
           System.out.println(employee.getName() + " has initial working hours:" + employee.getWorkingHours());
           System.out.println(employee.getName() + " has initial overtime hours:" + employee.getOverTimeHours());

       }
        ShiftScheduleInterface schedule = ScheduleCreator.create(begin, end, 3, true, null);
        printSchedule(schedule);

        //print overtimehours after
        for (Employee employee : ScheduleCreator.employeeSet) {
            employee.setWorkingHours(2);
            System.out.println(employee.getName() + " has now " + employee.getOverTimeHours() + " overtime hours");

        }
    }



    private void printSchedule(ShiftScheduleInterface schedule) {
        System.out.println(schedule);
        for (Employee employee : ScheduleCreator.employeeSet) {
            if (employee.getOverTimeHours() > 0) {
                System.out.println(employee.getOverTimeHours());
            }
        }

    }

}
