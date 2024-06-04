package it.scheduleplanner.planner;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.Employee;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.scheduleplanner.planner.EmployeeComparator.getNext;

public class ScheduleCreator {

    public static ArrayList<Employee> employeeList = new ArrayList<>();

    public static void addEmployee(Employee employee) {
        employeeList.add(employee);
    }

    public static ShiftScheduleInterface create(LocalDate begin, LocalDate end, int numberOfEmployeesPerDay, boolean weekendOpen, DayOfWeek restDay) {
        ShiftScheduleInterface calendar = new FixedShiftsSchedule(begin);

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
            while (!currentDate.isAfter(end)) {
                if (restDay == null || !currentDate.getDayOfWeek().equals(restDay) || !currentDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || !currentDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    dateList.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }
        }


        for (LocalDate date : dateList) {
            ShiftDayInterface day = new FixedShiftDay();
            
            Map<Employee, Shift> currentDayCoveredShift = getNext(employeeList, date, numberOfEmployeesPerDay); //TODO error trying to cast map to employee
            for (Employee employee : currentDayCoveredShift.keySet()) {
                day.addEmployee(employee, currentDayCoveredShift.get(employee));
            }
        	
            calendar.addDay(date, day);
        }
        return calendar; //ShiftScheduleInterface calendar.getSchedule --> returns map: dates - day
    }
}
