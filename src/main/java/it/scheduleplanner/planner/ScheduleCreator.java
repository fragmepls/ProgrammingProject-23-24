package it.scheduleplanner.planner;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.Employee;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static it.scheduleplanner.planner.EmployeeComparator.getNext;
import static it.scheduleplanner.planner.EmployeeComparator.assignWorkingHoursToEmployee;


public class ScheduleCreator {

    public static Set<Employee> employeeSet = new HashSet<>();

    public static void addEmployee(Employee employee) {
        if (!employeeSet.contains(employee)) {
            employeeSet.add(employee);
        }
    }

    public static void removeEmployee(Employee employee) {
        employeeSet.remove(employee);
    }

    public static void addEmployeeList(ArrayList<Employee> employees) {
        for (Employee employee : employees) {
            addEmployee(employee);
        }
    }
    //returns a calendar - consits out of
    public static ShiftScheduleInterface create(LocalDate begin, LocalDate end, int numberOfEmployeesPerDay, boolean weekendOpen, DayOfWeek restDay) {
        //initialize the working hours for the case the schedule begins not with a monday
        assignWorkingHoursToEmployee(employeeSet);
        ShiftScheduleInterface calendar = new FixedShiftsSchedule(begin);
        List<LocalDate> dateList = generateDateList(begin, end, weekendOpen, restDay);

        for (LocalDate date : dateList) {
            ShiftDayInterface day = new FixedShiftDay();
            Map<Employee, Shift> currentDayCoveredShift = getNext(employeeSet, date, numberOfEmployeesPerDay, restDay);
            for (Employee employee : currentDayCoveredShift.keySet()) {
                day.addEmployee(employee, currentDayCoveredShift.get(employee));
            }
            calendar.addDay(date, day);
        }
        printOvertimeHours(employeeSet);
        return calendar;
    }


    private static void printOvertimeHours(Set<Employee> employeeSet) {
        for (Employee employee : employeeSet) {
            if (employee.getOverTimeHours() > 0) {
                System.out.println(employee.getOverTimeHours());
            }
        }
    }

    public static List<LocalDate> generateDateList(LocalDate begin, LocalDate end, boolean weekendOpen, DayOfWeek restDay) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = begin;

        if (weekendOpen) {
            while (!currentDate.isAfter(end)) {
                if (restDay == null || !currentDate.getDayOfWeek().equals(restDay)) {
                    dateList.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }
        } else {
            while (!currentDate.isAfter(end)) {
                DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                if ((restDay == null || !dayOfWeek.equals(restDay)) && dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                    dateList.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }
        }
        return dateList;
    }

}
