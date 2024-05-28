package it.scheduleplanner.planner;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static it.scheduleplanner.planner.EmployeeComparator.getNext;

public class ScheduleCreator {
    //TODO Create calendar
    //TODO Loop to iterate over the days between LocalDate beginn - LocalDate end
    //TODO For every day add date to calendar
    //TODO For every day take the most adeguate employee(with EmployeeComparator)
    //TODO if employee = half we need +1 employee
    //TODO return calendar

    static ArrayList<Employee> employeeList = new ArrayList<>();

    public static ShiftScheduleInterface create(LocalDate begin, LocalDate end, int numberOfEmployeesPerDay) {
        ShiftScheduleInterface calendar = new FixedShiftsSchedule(begin);
        ShiftDayInterface day = new FixedShiftDay();

        List<LocalDate> dateList = new ArrayList<>();
        // Loop from begin to end date (inclusive)
        LocalDate currentDate = begin;
        while (!currentDate.isAfter(end)) {
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1); // Move to the next day
        }
        for (LocalDate date : dateList) {
            Employee employee1ForThisDay = (Employee) getNext(employeeList, date);
            day.addEmployee(employee1ForThisDay);
            calendar.addDay(date, day);
        }
        return calendar;
    }
}
