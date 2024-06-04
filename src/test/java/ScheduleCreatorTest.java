import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
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
        Employee emp1 = new Employee("John Doe", true, "monday", false);
        Employee emp2 = new Employee("Jane Smith", false, "TUESDAY", false);
        Employee employee3 = new Employee("Jack", true, "wednesday", false);
        Employee employee4 = new Employee("Jill", false, "friday", false);
        Employee employee5 = new Employee("Jake", true, "saturday", false);
        Employee employee6 = new Employee("Jess", true, "monday", false);
        Employee employee7 = new Employee("Jerry", true, "tuesday", false);
        Employee employee8 = new Employee("Janet", false, "monday", false);
        Employee employee9 = new Employee("Jasmine", true, "sunday", false);
        Employee employee10 = new Employee("James", false, "tuesday", false);



        LocalDate begin = LocalDate.of(2024, 6, 1); // June 1, 2024
        LocalDate end = LocalDate.of(2024, 6, 7);   // June 7, 2024

        ShiftScheduleInterface schedule = ScheduleCreator.create(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 7), 5, true, null);
        System.out.println(schedule.toString());

        schedule = ScheduleCreator.create(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 7), 5, false, DayOfWeek.WEDNESDAY);
        System.out.println(schedule.toString());
    }
}
