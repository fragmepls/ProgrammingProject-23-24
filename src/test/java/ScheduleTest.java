import it.scheduleplanner.export.ShiftDayInterface;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.utils.Vacation;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleTest {

    @Test
    void ScheduleTest() {
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
        // Add employees to ScheduleCreator
        ScheduleCreator.addEmployee(employee1);
        ScheduleCreator.addEmployee(employee2);
        ScheduleCreator.addEmployee(employee3);
        ScheduleCreator.addEmployee(employee4);
        ScheduleCreator.addEmployee(employee5);
        ScheduleCreator.addEmployee(employee6);
        ScheduleCreator.addEmployee(employee7);
        ScheduleCreator.addEmployee(employee8);
        ScheduleCreator.addEmployee(employee9);
        ScheduleCreator.addEmployee(employee10);

        // Create the schedule
        ShiftScheduleInterface schedule = ScheduleCreator.create(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30), 2, true, DayOfWeek.TUESDAY);

        // Assertions to verify the schedule
        assertNotNull(schedule, "The schedule should not be null");

        // Access the full schedule map
        Map<LocalDate, ShiftDayInterface> scheduleMap = schedule.getSchedule();

        // Check that the schedule contains the expected dates and employees
        for (LocalDate date = LocalDate.of(2024, 6, 1); !date.isAfter(LocalDate.of(2024, 6, 30)); date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.TUESDAY) {
                ShiftDayInterface day = schedule.getDay(date);
                assertNotNull(day, "Day should not be null for date: " + date);
                assertEquals(2, day.getEmployeeCount(), "There should be 2 employees scheduled for date: " + date);
            } else {
                assertFalse(scheduleMap.containsKey(date), "Day should be null for rest day: " + date);
            }
        }
    }
}