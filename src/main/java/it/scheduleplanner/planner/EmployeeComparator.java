package it.scheduleplanner.planner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.utils.Employee;

public class EmployeeComparator {

    /**
     * Generates the schedule for the next day, assigning shifts to employees.
     *
     * @param employeeSet Set of employees available for scheduling.
     * @param date The date for which the schedule is being generated.
     * @param numberOfEmployeesPerDay The number of employees needed per day.
     * @param restDay The designated rest day for an employee has.
     * @return A map of employees to their assigned shifts for the given date.
     */
    public static Map<Employee, Shift> getNext(Set<Employee> employeeSet, LocalDate date, int numberOfEmployeesPerDay, DayOfWeek restDay) {
        // Initialize maps to store shift assignments
        Map<Employee, Shift> fullDayExport = new HashMap<>();
        Map<Employee, Shift> morningExport = new HashMap<>();
        Map<Employee, Shift> afternoonExport = new HashMap<>();
        Map<Employee, Shift> overtimeExport = new HashMap<>();

        // Convert employeeSet to a list for easier manipulation
        List<Employee> employeeList = new ArrayList<>(employeeSet);

        // Assign working hours and shuffle employees if necessary
        if (restDay == null || !restDay.equals(DayOfWeek.MONDAY)) {
            if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                assignWorkingHoursToEmployee(employeeSet);
                Collections.shuffle(employeeList); // Shuffle employees
            }
        } else {
            if (date.getDayOfWeek() == DayOfWeek.TUESDAY) {
                assignWorkingHoursToEmployee(employeeSet);
                Collections.shuffle(employeeList); // Shuffle employees
            }
        }

        // Remove past vacations for all employees
        removePastVacations(employeeSet);

        int coveredShifts = 0;

        // Create lists to keep track of available employees
        List<Employee> availableEmployeesForCurrentDay = new ArrayList<>(employeeList);
        List<Employee> availableEmployeesForAfternoon = new ArrayList<>(employeeList);

        // Assign full day shifts to employees who can work a full day
        for (Employee employee : employeeList) {
            if (coveredShifts == numberOfEmployeesPerDay) break; // Stop if required shifts are covered
            if (!isAvailable(employee, date)) {
                continue; // Skip if employee is not available
            }

            if (employee.getWorkingHours() >= 8) { // Check if the employee has enough hours for a full day shift
                fullDayExport.put(employee, Shift.FULL);
                employee.setWorkingHours(employee.getWorkingHours() - 8);
                availableEmployeesForCurrentDay.remove(employee);
                availableEmployeesForAfternoon.remove(employee);
                coveredShifts++;
            }
        }

        // Assign morning and afternoon shifts if full day shifts are not enough
        if (coveredShifts < numberOfEmployeesPerDay) {
            for (Employee employee : availableEmployeesForCurrentDay) {
                if (coveredShifts == numberOfEmployeesPerDay) {
                    break; // Stop if required shifts are covered
                }
                boolean afternoonShiftAssigned = false;

                if (employee.getWorkingHours() < 8 && employee.getWorkingHours() >= 4) { // Check if the employee can work a half-day shift
                    morningExport.put(employee, Shift.MORNING);
                    employee.setWorkingHours(employee.getWorkingHours() - 4);
                    availableEmployeesForAfternoon.remove(employee);

                    // Search for an employee for the afternoon shift
                    for (Employee afternoonEmployee : availableEmployeesForAfternoon) {
                        if (afternoonEmployee.getWorkingHours() >= 4) { // Check if the afternoon employee can work a half-day shift
                            afternoonExport.put(afternoonEmployee, Shift.AFTERNOON);
                            afternoonShiftAssigned = true;
                            afternoonEmployee.setWorkingHours(afternoonEmployee.getWorkingHours() - 4);
                            break;
                        }
                    }

                    if (afternoonShiftAssigned) {
                        coveredShifts++;
                    } else {
                        // If no afternoon shift is assigned, handle it
                        morningExport.remove(employee);
                        employee.setWorkingHours(employee.getWorkingHours() + 4);
                        System.out.println("Problem to solve the afternoon shift, need to use overtime hours");
                        break;
                    }
                }
            }
        }

        // If still not enough employees, assign overtime shifts
        if (coveredShifts < numberOfEmployeesPerDay) {
            overtimeExport = assignRemainingShift(availableEmployeesForCurrentDay, morningExport, afternoonExport, date, coveredShifts, numberOfEmployeesPerDay);
        }

        // Merge all maps into a single export map
        Map<Employee, Shift> export = new HashMap<>(fullDayExport);
        export.putAll(morningExport);
        export.putAll(afternoonExport);
        export.putAll(overtimeExport);

        return export;
    }

    /**
     * Removes past vacations for all employees based on the current date.
     *
     * @param employeeSet Set of employees to process.
     */
    private static void removePastVacations(Set<Employee> employeeSet) {
        LocalDate currentDate = LocalDate.now();
        for (Employee employee : employeeSet) {
            employee.removeExpiredVacations(currentDate);
        }
    }

    /**
     * Assigns standard working hours to employees based on their employment type.
     *
     * @param employeeSet Set of employees to process.
     */
    public static void assignWorkingHoursToEmployee(Set<Employee> employeeSet) {
        for (Employee employee : employeeSet) {
            if (employee.isFullTimeWorker()) {
                employee.setWorkingHours(40); // Full-time workers get 40 hours
            } else {
                employee.setWorkingHours(20); // Part-time workers get 20 hours
            }
        }
    }

    /**
     * Assigns remaining shifts as overtime for employees with the least overtime hours.
     *
     * @param employeeList List of employees available for overtime.
     * @param morningExport Map of morning shifts already assigned.
     * @param afternoonExport Map of afternoon shifts already assigned.
     * @param date The date for which the schedule is being generated.
     * @param coveredShifts The number of shifts already covered.
     * @param totalShiftsNeeded The total number of shifts needed.
     * @return A map of employees to their assigned overtime shifts.
     */
    private static Map<Employee, Shift> assignRemainingShift(List<Employee> employeeList, Map<Employee, Shift> morningExport, Map<Employee, Shift> afternoonExport, LocalDate date, int coveredShifts, int totalShiftsNeeded) {
        Map<Employee, Shift> overtimeMorningExport = new HashMap<>();
        Map<Employee, Shift> overtimeAfternoonExport = new HashMap<>();

        while (coveredShifts < totalShiftsNeeded) {
            Employee leastOvertimeEmployee1 = employeeWithLeastOvertimeHours(employeeList);
            overtimeMorningExport.put(leastOvertimeEmployee1, Shift.MORNING);
            leastOvertimeEmployee1.addOverTimeHours(4);

            Employee leastOvertimeEmployee2 = employeeWithLeastOvertimeHours(employeeList);
            overtimeAfternoonExport.put(leastOvertimeEmployee2, Shift.AFTERNOON);
            leastOvertimeEmployee2.addOverTimeHours(4);

            coveredShifts++;
        }

        Map<Employee, Shift> export = new HashMap<>(overtimeMorningExport);
        export.putAll(overtimeAfternoonExport);
        return export;
    }

    /**
     * Finds the employee with the least overtime hours.
     *
     * @param employeeList List of employees to check.
     * @return The employee with the least overtime hours.
     */
    private static Employee employeeWithLeastOvertimeHours(List<Employee> employeeList) {
        int currentLeastOvertimeHours = Integer.MAX_VALUE;
        Employee leastOvertimeEmployee = null;
        for (Employee employee : employeeList) {
            if (employee.getOverTimeHours() < currentLeastOvertimeHours) {
                currentLeastOvertimeHours = employee.getOverTimeHours();
                leastOvertimeEmployee = employee;
            }
        }
        return leastOvertimeEmployee;
    }

    /**
     * Checks if an employee is available on a specific date based on various conditions.
     *
     * @param employee The employee to check.
     * @param date The date to check availability for.
     * @return true if the employee is available, false otherwise.
     */
    public static boolean isAvailable(Employee employee, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (employee.isOnVacation(date)) return false;
        if (employee.getFreeDay() == dayOfWeek) return false;
        if ((dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) && !employee.isWeekendWorker())
            return false;
        return true;
    }
}
