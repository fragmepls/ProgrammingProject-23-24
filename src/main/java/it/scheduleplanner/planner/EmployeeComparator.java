package it.scheduleplanner.planner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.utils.Employee;

public class EmployeeComparator {

    public static Map<Employee, Shift> getNext(Set<Employee> employeeSet, LocalDate date, int numberOfEmployeesPerDay, DayOfWeek restDay) {
        Map<Employee, Shift> fullDayExport = new HashMap<>();
        Map<Employee, Shift> morningExport = new HashMap<>();
        Map<Employee, Shift> afternoonExport = new HashMap<>();
        Map<Employee, Shift> overtimeExport = new HashMap<>();
        List<Employee> employeeList = new ArrayList<>(employeeSet);

        // Add working hours and shuffle all employees on Monday, if Monday is restDay then on Tuesday
        if (restDay == null || !restDay.equals(DayOfWeek.MONDAY)) {
            if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                assignWorkingHoursToEmployee(employeeSet);
                Collections.shuffle(employeeList); // Shuffle employees for randomness
            }
        } else {
            if (date.getDayOfWeek() == DayOfWeek.TUESDAY) {
                assignWorkingHoursToEmployee(employeeSet);
                Collections.shuffle(employeeList); // Shuffle employees for randomness
            }
        }


        removePastVacations(employeeSet);

        int coveredShifts = 0;

        List<Employee> availableEmployeesForCurrentDay = new ArrayList<>(employeeList);
        List<Employee> availableEmployeesForAfternoon = new ArrayList<>(employeeList);
        List<Employee> availableEmployeesForOvertime = new ArrayList<>(employeeList);

        for (Employee employee : employeeList) {
            if (coveredShifts == numberOfEmployeesPerDay) break;
            if (!isAvailable(employee, date)) {
                continue;
            }

            if (employee.getWorkingHours() >= 8) {
                fullDayExport.put(employee, Shift.FULL);
                employee.setWorkingHours(employee.getWorkingHours() - 8);
                availableEmployeesForCurrentDay.remove(employee);
                availableEmployeesForAfternoon.remove(employee);
                availableEmployeesForOvertime.remove(employee);
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
                    morningExport.put(employee, Shift.MORNING);
                    employee.setWorkingHours(employee.getWorkingHours() - 4);
                    availableEmployeesForAfternoon.remove(employee);
                    availableEmployeesForOvertime.remove(employee);
                    //search an employee for afternoon shift
                    for (Employee afternoonEmployee : availableEmployeesForAfternoon) {

                        if (afternoonEmployee.getWorkingHours() >= 4) {
                            afternoonExport.put(afternoonEmployee, Shift.AFTERNOON);
                            afternoonShiftAssigned = true;
                            afternoonEmployee.setWorkingHours(employee.getWorkingHours() - 4);
                            availableEmployeesForOvertime.remove(afternoonEmployee);
                        }
                        if (afternoonShiftAssigned) {

                            coveredShifts++;
                            break;
                        }

                    }
                    if (!afternoonShiftAssigned) {
                        morningExport.remove(employee);
                        employee.setWorkingHours(employee.getWorkingHours() + 4);
                        System.out.println("Problem to solve the afternoon shift, need to use overtime hours");
                        break;
                    }
                }
            }
        }

        if (coveredShifts < numberOfEmployeesPerDay) {
            overtimeExport = assignRemainingShift(availableEmployeesForCurrentDay, morningExport, afternoonExport, date, coveredShifts, numberOfEmployeesPerDay);
        }

        // Merge fullDayExport, morningExport, and afternoonExport into a single export map
        Map<Employee, Shift> export = new HashMap<>(fullDayExport);
        export.putAll(morningExport);
        export.putAll(afternoonExport);
        export.putAll(overtimeExport);

        return export;
    }

    private static void removePastVacations(Set<Employee> employeeSet) {
        LocalDate currentDate = LocalDate.now();
        for (Employee employee : employeeSet) {
            employee.removeExpiredVacations(currentDate);
        }
    }

    public static void assignWorkingHoursToEmployee(Set<Employee> employeeSet) {
        for (Employee employee : employeeSet) {
            if (employee.isFullTimeWorker()) {
                employee.setWorkingHours(40);
            } else {
                employee.setWorkingHours(20);
            }
        }
    }

    private static Map<Employee, Shift> assignRemainingShift(List<Employee> employeeList, Map<Employee, Shift> morningExport, Map<Employee, Shift> afternoonExport, LocalDate date, int coveredShifts, int totalShiftsNeeded) {
        Map<Employee, Shift> overtimeMorningExport = new HashMap<>();
        Map<Employee, Shift> overtimeAfternoonExport = new HashMap<>();

        while (coveredShifts < totalShiftsNeeded) {

            boolean afternoonShiftAssigned = false;

            Employee leastOvertimeEmployee1 = employeeWithLeastOvertimeHours(employeeList);

            overtimeMorningExport.put(leastOvertimeEmployee1, Shift.MORNING);
            leastOvertimeEmployee1.addOverTimeHours(4);
            System.out.println(leastOvertimeEmployee1.getName() + " got added 4 overtime hours");

            //search an employee for afternoon shift

            Employee leastOvertimeEmployee2 = employeeWithLeastOvertimeHours(employeeList);

            overtimeAfternoonExport.put(leastOvertimeEmployee2, Shift.AFTERNOON);
            afternoonShiftAssigned = true;
            leastOvertimeEmployee2.addOverTimeHours(4);
            System.out.println(leastOvertimeEmployee2.getName() + " got added 4 overtime hours");

            if (afternoonShiftAssigned) {
                coveredShifts++;
            }

            if (!afternoonShiftAssigned) {
                System.out.println("Something went wrong.");
            }
        }


        Map<Employee, Shift> export = new HashMap<>(overtimeMorningExport);
        export.putAll(overtimeAfternoonExport);
        return export;
    }


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

    public static boolean isAvailable(Employee employee, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (employee.isOnVacation(date)) return false;
        if (employee.getFreeDay() == dayOfWeek) return false;
        if ((dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) && !employee.isWeekendWorker())
            return false;
        return true;
    }
}
