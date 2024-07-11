package it.scheduleplanner.planner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.utils.Employee;

/**
 * Utility class for comparing employees and generating work schedules.
 */
public class EmployeeComparator {

    /**
     * Generates the schedule for the next day, assigning shifts to employees.
     *
     * @param employeeSet             Set of employees available for scheduling.
     * @param date                    The date for which the schedule is being generated.
     * @param numberOfEmployeesPerDay The number of employees needed per day.
     * @param restDay                 The designated rest day for an employee has.
     * @return A map of employees to their assigned shifts for the given date.
     * @throws InsufficientEmployeesException if there are not enough employees to cover the required shifts.
     */
    public static Map<Employee, Shift> getNext(Set<Employee> employeeSet, LocalDate date, int numberOfEmployeesPerDay, DayOfWeek restDay) throws InsufficientEmployeesException {
        Map<Employee, Shift> export = new HashMap<>();
        Map<Employee, Shift> finalExport = new HashMap<>();

        List<Employee> employeeList = new ArrayList<>(employeeSet);

        if (restDay == null || !restDay.equals(DayOfWeek.MONDAY)) {
            if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                assignWorkingHoursToEmployee(employeeSet);
                Collections.shuffle(employeeList);
            }
        } else {
            if (date.getDayOfWeek() == DayOfWeek.TUESDAY) {
                assignWorkingHoursToEmployee(employeeSet);
                Collections.shuffle(employeeList);
            }
        }

        removePastVacations(employeeSet);

        int coveredShifts = 0;

        List<Employee> availableEmployeesForCurrentDay = new ArrayList<>(employeeList);
        List<Employee> availableEmployeesForAfternoon = new ArrayList<>(employeeList);
        List<Employee> availableEmployeesForOvertimeMorning = new ArrayList<>(employeeList);
        List<Employee> availableEmployeesForOvertimeAfternoon = new ArrayList<>(employeeList);

        for (Employee employee : employeeList) {
            if (coveredShifts == numberOfEmployeesPerDay) break;
            if (!isAvailable(employee, date)) {
                continue;
            }

            if (employee.getWorkingHours() >= 8) {
                export.put(employee, Shift.FULL);
                employee.setWorkingHours(employee.getWorkingHours() - 8);
                availableEmployeesForCurrentDay.remove(employee);
                availableEmployeesForAfternoon.remove(employee);
                availableEmployeesForOvertimeMorning.remove(employee);
                availableEmployeesForOvertimeAfternoon.remove(employee);
                coveredShifts++;
            }
        }

        if (coveredShifts < numberOfEmployeesPerDay) {
            for (Employee employee : availableEmployeesForCurrentDay) {
                if (coveredShifts == numberOfEmployeesPerDay) {
                    break;
                }
                boolean afternoonShiftAssigned = false;

                if (employee.getWorkingHours() < 8 && employee.getWorkingHours() >= 4) {
                    export.put(employee, Shift.MORNING);
                    employee.setWorkingHours(employee.getWorkingHours() - 4);
                    availableEmployeesForAfternoon.remove(employee);
                    availableEmployeesForOvertimeMorning.remove(employee);

                    for (Employee afternoonEmployee : availableEmployeesForAfternoon) {
                        if (afternoonEmployee.getWorkingHours() >= 4) {
                            export.put(afternoonEmployee, Shift.AFTERNOON);
                            afternoonShiftAssigned = true;
                           //TODO here added covered shifts++ --> missed for some reason
                            coveredShifts++;
                            afternoonEmployee.setWorkingHours(afternoonEmployee.getWorkingHours() - 4);
                            availableEmployeesForOvertimeAfternoon.remove(afternoonEmployee);
                            break;
                        }
                    }

                    if (!afternoonShiftAssigned) {
                        export.remove(employee);
                        employee.setWorkingHours(employee.getWorkingHours() + 4);
                        //TODO added here afterwards -- missed before
                        availableEmployeesForOvertimeMorning.add(employee);
                        System.out.println("Problem to solve the afternoon shift, need to use overtime hours");
                        break;
                    }
                }
            }
        }

        if (coveredShifts < numberOfEmployeesPerDay) {
            finalExport = assignRemainingShift(availableEmployeesForOvertimeMorning, availableEmployeesForOvertimeAfternoon, export, date, coveredShifts, numberOfEmployeesPerDay);
            return finalExport;
        }

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
                employee.setWorkingHours(40);
            } else {
                employee.setWorkingHours(20);
            }
        }
    }

    /**
     * Assigns remaining shifts as overtime for employees with the least overtime hours.
     *
     * @param morningAvailableEmployees   List of employees available for morning shifts.
     * @param afternoonAvailableEmployees List of employees available for afternoon shifts.
     * @param currentMappedShifts         Current mapping of employees to shifts.
     * @param date                        The date for which the schedule is being generated.
     * @param coveredShifts               The number of shifts already covered.
     * @param totalShiftsNeeded           The total number of shifts needed.
     * @return A map of employees to their assigned overtime shifts.
     * @throws InsufficientEmployeesException if there are not enough employees to cover the required shifts.
     */
    private static Map<Employee, Shift> assignRemainingShift(
            List<Employee> morningAvailableEmployees,
            List<Employee> afternoonAvailableEmployees,
            Map<Employee, Shift> currentMappedShifts,
            LocalDate date, int coveredShifts, int totalShiftsNeeded) throws InsufficientEmployeesException {

        while (coveredShifts < totalShiftsNeeded) {
            boolean afternoonShiftAssigned = false;

            Employee leastOvertimeEmployee1 = employeeWithLeastOvertimeHours(morningAvailableEmployees, date);
            if (leastOvertimeEmployee1 != null) {
                if (currentMappedShifts.containsKey(leastOvertimeEmployee1)) {
                    currentMappedShifts.remove(leastOvertimeEmployee1);
                    currentMappedShifts.put(leastOvertimeEmployee1, Shift.FULL);
                } else {
                    currentMappedShifts.put(leastOvertimeEmployee1, Shift.MORNING);
                }
                leastOvertimeEmployee1.addOverTimeHours(4);
                System.out.println(leastOvertimeEmployee1.getName() + " assigned 4 overtime hours for morning shift");
            } else {
                throw new InsufficientEmployeesException("No available employees for morning overtime shifts.");
            }

            Employee leastOvertimeEmployee2 = employeeWithLeastOvertimeHours(afternoonAvailableEmployees, date);
            if (leastOvertimeEmployee2 != null) {
                if (currentMappedShifts.containsKey(leastOvertimeEmployee2)) {
                    currentMappedShifts.remove(leastOvertimeEmployee2);
                    currentMappedShifts.put(leastOvertimeEmployee2, Shift.FULL);
                } else {
                    currentMappedShifts.put(leastOvertimeEmployee2, Shift.AFTERNOON);
                }
                afternoonShiftAssigned = true;
                leastOvertimeEmployee2.addOverTimeHours(4);
                System.out.println(leastOvertimeEmployee2.getName() + " assigned 4 overtime hours for afternoon shift");
            } else {
                throw new InsufficientEmployeesException("No available employees for afternoon overtime shifts.");
            }

            if (afternoonShiftAssigned) {
                coveredShifts++;
            } else {
                System.out.println("Something went wrong assigning afternoon shift.");
            }
        }

        return currentMappedShifts;
    }

    /**
     * Finds the employee with the least overtime hours.
     *
     * @param employeeList List of employees to check.
     * @param date         The date to check availability for.
     * @return The employee with the least overtime hours.
     */
    private static Employee employeeWithLeastOvertimeHours(List<Employee> employeeList, LocalDate date) {
        int currentLeastOvertimeHours = Integer.MAX_VALUE;
        Employee leastOvertimeEmployee = null;
        for (Employee employee : employeeList) {
            if (employee.getOverTimeHours() < currentLeastOvertimeHours) {
                if (isAvailable(employee, date)) {
                    currentLeastOvertimeHours = employee.getOverTimeHours();
                    leastOvertimeEmployee = employee;
                }
            }
        }

        return leastOvertimeEmployee;
    }

    /**
     * Checks if an employee is available on a specific date based on various conditions.
     *
     * @param employee The employee to check.
     * @param date     The date to check availability for.
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
