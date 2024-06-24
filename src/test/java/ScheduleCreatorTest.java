import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static it.scheduleplanner.planner.ScheduleCreator.employeeSet;
import static org.junit.jupiter.api.Assertions.*;

public class ScheduleCreatorTest {

    @Test
    public void testGenerateDateListWeekendOpen() {
        LocalDate begin = LocalDate.of(2024, 6, 1); // June 1, 2024
        LocalDate end = LocalDate.of(2024, 6, 7);   // June 7, 2024

        List<LocalDate> dateList = ScheduleCreator.generateDateList(begin, end, true, null);
        for (LocalDate date : dateList) {
            System.out.println(date);
        }
        assertEquals(7, dateList.size()); // Should include all days from 1st to 7th June

        dateList = ScheduleCreator.generateDateList(begin, end, true, DayOfWeek.WEDNESDAY);
        assertEquals(6, dateList.size()); // Should exclude Wednesday, June 5, 2024
    }

    @Test
    public void testGenerateDateListWeekendClosed() {
        LocalDate begin = LocalDate.of(2024, 6, 1); // June 1, 2024
        LocalDate end = LocalDate.of(2024, 6, 7);   // June 7, 2024

        List<LocalDate> dateList = ScheduleCreator.generateDateList(begin, end, false, null);
        for (LocalDate date : dateList) {
            System.out.println(date);
        }
        assertEquals(5, dateList.size()); // Should exclude weekends (1st and 2nd June)

        dateList = ScheduleCreator.generateDateList(begin, end, false, DayOfWeek.WEDNESDAY);
        assertEquals(4, dateList.size()); // Should exclude Wednesday, Saturday, and Sunday
    }

        @Test
        public void testCreateSchedule() {

            // Add some mock employees
            Employee emp1 = new Employee("Doe", true, "monday", false);
            Employee emp2 = new Employee("Smith", false, "TUESDAY", false);
            Employee employee3 = new Employee("Jack", true, "wednesday", false);
            Employee employee4 = new Employee("Jill", false, "friday", false);
            Employee employee5 = new Employee("Bob", true, "saturday", false);
            Employee employee6 = new Employee("Katherine", false, "sunday", false);
            Employee employee7 = new Employee("Jason", true, "monday", false);
            Employee employee8 = new Employee("Jan", false, "tuesday", false);
            Employee employee9 = new Employee("Lara", true, "saturday", false);
            Employee employee10 = new Employee("Sara", false, "wednesday", false);

            ArrayList<String> employees = new ArrayList<>();
            for (Employee employee : employeeSet) {
                String employeeName = employee.getName();
                employees.add(employeeName);
            }

            System.out.println(employees);

            LocalDate begin = LocalDate.of(2024, 6, 2); // June 1, 2024
            LocalDate end = LocalDate.of(2024, 6, 30);   // June 7, 2024

            ShiftScheduleInterface schedule = ScheduleCreator.create(begin, end,6, false, null);
            System.out.println(schedule);
        }
    }
