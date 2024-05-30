package it.scheduleplanner.planner;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.Employee;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static it.scheduleplanner.planner.EmployeeComparator.getNext;

public class ScheduleCreator {

    static ArrayList<Employee> employeeList = new ArrayList<>();

    public static void addEmployee(Employee employee) {
        employeeList.add(employee);
    }

    public static ShiftScheduleInterface create(LocalDate begin, LocalDate end, int numberOfEmployeesPerDay, boolean weekendOpen, DayOfWeek restDay) {
        ShiftScheduleInterface calendar = new FixedShiftsSchedule(begin);
        ShiftDayInterface day = new FixedShiftDay();

        List<LocalDate> dateList = new ArrayList<>();
        // Loop from begin to end date (inclusive) and add all days, where shifts are needed.
        LocalDate currentDate = begin;

        if (weekendOpen) {
            while (!currentDate.isAfter(end)) {
                if (restDay == null || !currentDate.getDayOfWeek().equals(restDay)) {
                    dateList.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }
        } else if (!weekendOpen) {
            while (!currentDate.isBefore(end)) {
                if (restDay == null || !currentDate.getDayOfWeek().equals(restDay) || !currentDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || !currentDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    dateList.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }
        }


        for (LocalDate date : dateList) {
            Employee employee1ForThisDay = (Employee) getNext(employeeList, date, numberOfEmployeesPerDay);
            day.addEmployee(employee1ForThisDay);
            calendar.addDay(date, day);
        }
        return calendar;
    }
}
