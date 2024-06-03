import it.scheduleplanner.export.FixedShiftDay;
import it.scheduleplanner.export.FixedShiftsSchedule;
import it.scheduleplanner.export.ShiftDayInterface;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduleCreatorTest {
/*
    @Test
    void testCreate() {
        // Define test parameters
        LocalDate beginDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 10);
        int numberOfEmployeesPerDay = 1;
        boolean weekendOpen = true;
        DayOfWeek restDay = null; // No rest day

        // Call the create method
        ShiftScheduleInterface schedule = ScheduleCreator.create(beginDate, endDate, numberOfEmployeesPerDay, weekendOpen, restDay);

        // Verify that the schedule is created correctly
        assertEquals((endDate.getDayOfYear() - beginDate.getDayOfYear() + 1), schedule.getNumberOfDays());

        // Verify that each day has the correct number of shifts
        for (LocalDate date = beginDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            ShiftDayInterface shiftDay = schedule.getDay(date);
            assertEquals(numberOfEmployeesPerDay, shiftDay.getShifts().size());
        }
    }
    */

}
