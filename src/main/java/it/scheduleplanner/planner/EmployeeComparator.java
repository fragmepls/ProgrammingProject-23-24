package it.scheduleplanner.planner;

import java.time.DayOfWeek;
import java.util.*;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.utils.Employee;

import java.time.LocalDate;

public class EmployeeComparator {

    public static Map<Employee, Shift> getNext(Set<Employee> employeeSet, LocalDate date, int numberOfEmployeesPerDay) {
        Map<Employee, Shift> export = new HashMap<>();
        Set<Employee> employeeSetShuffeld = employeeSet;

        /* every monday all employees that are full time workers get 40 working hours that they should work during this week and
        * every halftime workers get 20 working hours that they should work during this week.
        * In addition, every Monday the Set of employees gets sorted randomly so that not always the same workers get the same shifts
         */
        if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            assignWorkingHoursToEmployee(employeeSet);
            employeeSetShuffeld = sortRandomlyEmployees(employeeSet);
        }
        /* if the calendar start not with a monday we have to ensure that there are available employees until monday:
        *
        */

        //Remove all past vacations that are over
        LocalDate currentDate = LocalDate.now();

        // Iterate through all employees and remove expired vacations
        for (Employee employee : employeeSet) {
            employee.removeExpiredVacations(currentDate);
        }

        //ensures that always exactly the needed employees per day get added
        int coveredShifts = 0;

        while (coveredShifts < numberOfEmployeesPerDay) {
            boolean shiftAssigned = false;

            // Assign FULL shifts
            shiftAssigned = assignShifts(employeeSetShuffeld, export, date,numberOfEmployeesPerDay - coveredShifts);
            coveredShifts = export.size();


            // Assign remaining shifts to employees with the least overtime hours
            if (coveredShifts < numberOfEmployeesPerDay) {
                shiftAssigned = assignRemainingShift(employeeSetShuffeld, export, date, coveredShifts);
                if (shiftAssigned) {
                    coveredShifts++;
                }
            }

            // If no employees were assigned during this pass, we cannot fulfill the required number of employees
            if (!shiftAssigned) {
                break;
            }
        }

        return export;
    }


    private static void assignWorkingHoursToEmployee(Set<Employee> employeeSet) {
        for (Employee employee : employeeSet) {
            if (employee.isFullTimeWorker()) {
                employee.setWorkingHours(40);
            } else {
                employee.setWorkingHours(20);
            }
        }
    }

    private static Set<Employee> sortRandomlyEmployees(Set<Employee> employeeSet) {
        //make out of the set a list i order to shuffle the employees
        ArrayList <Employee> employeeList = new ArrayList<>(employeeSet);
        Collections.shuffle(employeeList);
        return new HashSet<>(employeeList);
    }


    private static boolean assignShifts(Set<Employee> employeeSet, Map<Employee, Shift> export, LocalDate date, int limit) {
        int assignedCount = 0;
        boolean morningShiftAssigned = false;
        for (Employee employee : employeeSet) {
            if (!isAvailable(employee, date)) {
                continue;
            }

            if (employee.getWorkingHours() >= 8 && assignedCount < limit) {
                export.put(employee, Shift.FULL);
                employee.setWorkingHours(employee.getWorkingHours() - 8);
                assignedCount++;

            } else if (employee.getWorkingHours() < 8 && employee.getWorkingHours() >= 4 && assignedCount < limit) {
                if (!morningShiftAssigned) {
                    export.put(employee, Shift.MORNING);
                    employee.setWorkingHours(employee.getWorkingHours() - 4);
                    morningShiftAssigned = true;
                }
            }
        }

        // Assign afternoon shifts for employees with remaining hours after morning shift
        if (morningShiftAssigned) {
            for (Employee otherEmployee : employeeSet) {
                if (!isAvailable(otherEmployee, date)) {
                    continue;
                }
                if (otherEmployee.getWorkingHours() >= 4) {
                    export.put(otherEmployee, Shift.AFTERNOON);
                    otherEmployee.setWorkingHours(otherEmployee.getWorkingHours() - 4);
                    assignedCount++;
                }
                if (assignedCount >= limit) {
                    return true;
                }
            }
        }
        return assignedCount > 0;
    }



    private static int countAssignedShifts(Map<Employee, Shift> export) {
        return export.size(); // Simply count the total number of assigned shifts
    }
    private static boolean assignRemainingShift(Set<Employee> employeeSet, Map<Employee, Shift> export, LocalDate date, int employeesAssigned) {
        Employee employeeWithMinOvertime = null;
        int minOvertimeHours = Integer.MAX_VALUE;

        for (Employee employee : employeeSet) {
            if (!isAvailable(employee, date)) {
                continue;
            }

            int currentOvertimeHours = employee.getOverTimeHours();
            if (currentOvertimeHours < minOvertimeHours) {
                minOvertimeHours = currentOvertimeHours;
                employeeWithMinOvertime = employee;
            }
        }

        if (employeeWithMinOvertime != null) {
            export.put(employeeWithMinOvertime, employeesAssigned % 2 == 0 ? Shift.MORNING : Shift.AFTERNOON);
            employeeWithMinOvertime.addOverTimeHours(4);
            return true;
        }

        return false;
    }

    public static boolean isAvailable(Employee employee, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        if (employee.isOnVacation(date)) {
            return false;
        }
        if (employee.getFreeDay() == dayOfWeek) {
            return false;
        } else {
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                if (!employee.isWeekendWorker()) {
                    return false;
                }
            }
            return true;
        }
    }
}
