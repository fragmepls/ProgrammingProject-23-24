package it.scheduleplanner.planner;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.Employee;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static it.scheduleplanner.planner.EmployeeComparator.getNext;
import static it.scheduleplanner.planner.EmployeeComparator.assignWorkingHoursToEmployee;

/**
 * Class responsible for creating and managing employee shift schedules.
 */
public class ScheduleCreator {

    // Set to hold all employees
    public static Set<Employee> employeeSet = new HashSet<>();
    public static Set<Integer> employeeSetByID = new HashSet<>();

    /**
     * Adds an employee to the employeeSet.
     *
     * @param employee The employee to be added.
     */
    public static void addEmployee(Employee employee) {
        employeeSet.add(employee);
    }

    public static int idCreator() {
        int employeeID = employeeSet.size();
        employeeSetByID.add(employeeID);
        return employeeID;
    }


    /**
     * Adds a list of employees to the employeeSet.
     *
     * @param employees The list of employees to be added.
     */
    public static void addEmployeeList(ArrayList<Employee> employees) {
        for (Employee employee : employees) {
            addEmployee(employee);
        }
    }

    /**
     * Creates a shift schedule for a specified period.
     *
     * @param begin                   The start date of the schedule.
     * @param end                     The end date of the schedule.
     * @param numberOfEmployeesPerDay The number of employees required per day.
     * @param weekendOpen             A flag indicating if weekends are considered working days.
     * @param restDay                 The designated rest day for employees.
     * @return The generated shift schedule.
     */
    public static ShiftScheduleInterface create(LocalDate begin, LocalDate end, int numberOfEmployeesPerDay, boolean weekendOpen, DayOfWeek restDay) throws InsufficientEmployeesException {
        assignWorkingHoursToEmployee(employeeSet);
        ShiftScheduleInterface calendar = new FixedShiftsSchedule(begin);
        List<LocalDate> dateList = generateDateList(begin, end, weekendOpen, restDay);
        StringBuilder errorMessages = new StringBuilder();

        for (LocalDate date : dateList) {
            ShiftDayInterface day = new FixedShiftDay();
            try {
                Map<Employee, Shift> currentDayCoveredShift = getNext(employeeSet, date, numberOfEmployeesPerDay, restDay);
                for (Employee employee : currentDayCoveredShift.keySet()) {
                    day.addEmployee(employee, currentDayCoveredShift.get(employee));
                }
            } catch (InsufficientEmployeesException e) {
                errorMessages.append("Error generating schedule for ").append(date).append(": ").append(e.getMessage()).append("\n");
            }
            calendar.addDay(date, day);
        }

        printOvertimeHours(employeeSet);

        if (errorMessages.length() > 0) {
            throw new InsufficientEmployeesException(errorMessages.toString());
        }

        return calendar;
    }


    /**
     * Prints the overtime hours for each employee.
     *
     * @param employeeSet The set of employees to process.
     */
    private static void printOvertimeHours(Set<Employee> employeeSet) {
        for (Employee employee : employeeSet) {
            if (employee.getOverTimeHours() > 0) {
                System.out.println(employee.getOverTimeHours());
            }
        }
    }

    /**
     * Generates a list of dates for the scheduling period, excluding rest days and weekends if applicable.
     *
     * @param begin       The start date.
     * @param end         The end date.
     * @param weekendOpen A flag indicating if weekends are considered working days.
     * @param restDay     The designated rest day.
     * @return A list of dates to be included in the schedule.
     */
    public static List<LocalDate> generateDateList(LocalDate begin, LocalDate end, boolean weekendOpen, DayOfWeek restDay) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = begin;

        // Generate date list including weekends if applicable
        if (weekendOpen) {
            while (!currentDate.isAfter(end)) {
                if (restDay == null || !currentDate.getDayOfWeek().equals(restDay)) {
                    dateList.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }
        } else {
            // Generate date list excluding weekends
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
