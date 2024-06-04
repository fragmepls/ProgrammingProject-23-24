package it.scheduleplanner.planner;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.HashMap;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.utils.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class EmployeeComparator {

    public static Map<Employee, Shift> getNext(ArrayList<Employee> employeeList, LocalDate date, int numberOfEmployeesPerDay) {
        Map<Employee, Shift> export = new HashMap<>();

        for (Employee employee : employeeList) {
            if (employee.isFullTimeWorker()) {
                employee.setWorkingHours(40);
            } else {
                employee.setWorkingHours(20);
            }
        }

        if (date.getDayOfWeek() != DayOfWeek.MONDAY) {
            Collections.shuffle(employeeList);
        } else {
            for (Employee employee : employeeList) {
                if (employee.isFullTimeWorker()) {
                    employee.setWorkingHours(40);
                } else {
                    employee.setWorkingHours(20);
                }
            }
            Collections.shuffle(employeeList);
        }

        int employeesAssigned = 0;

        while (employeesAssigned < numberOfEmployeesPerDay) {
            boolean shiftAssigned = false;

            // Assign FULL shifts
            shiftAssigned = assignShifts(employeeList, export, date, Shift.FULL, 8, numberOfEmployeesPerDay - employeesAssigned);
            employeesAssigned += countAssignedShifts(export, Shift.FULL);

            // Assign MORNING and AFTERNOON shifts
            if (employeesAssigned < numberOfEmployeesPerDay) {
                shiftAssigned = assignShifts(employeeList, export, date, Shift.MORNING, 4, numberOfEmployeesPerDay - employeesAssigned) ||
                        assignShifts(employeeList, export, date, Shift.AFTERNOON, 4, numberOfEmployeesPerDay - employeesAssigned);
                employeesAssigned += countAssignedShifts(export, Shift.MORNING) + countAssignedShifts(export, Shift.AFTERNOON);
            }

            // Assign remaining shifts to employees with the least overtime hours
            if (employeesAssigned < numberOfEmployeesPerDay) {
                shiftAssigned = assignRemainingShift(employeeList, export, date, employeesAssigned);
                if (shiftAssigned) {
                    employeesAssigned++;
                }
            }

            // If no employees were assigned during this pass, we cannot fulfill the required number of employees
            if (!shiftAssigned) {
                break;
            }
        }

        return export;
    }

    private static boolean assignShifts(ArrayList<Employee> employeeList, Map<Employee, Shift> export, LocalDate date, Shift shift, int requiredHours, int limit) {
        int assignedCount = 0;
        for (Employee employee : employeeList) {
            if (!isAvailable(employee, date)) {
                continue;
            }

            if (employee.getWorkingHours() >= requiredHours && assignedCount < limit) {
                export.put(employee, shift);
                employee.setWorkingHours(employee.getWorkingHours() - requiredHours);
                assignedCount++;
                if (assignedCount >= limit) {
                    return true;
                }
            }
        }
        return assignedCount > 0;
    }

    private static int countAssignedShifts(Map<Employee, Shift> export, Shift shift) {
        int count = 0;
        for (Shift assignedShift : export.values()) {
            if (assignedShift == shift) {
                count++;
            }
        }
        return count;
    }

    private static boolean assignRemainingShift(ArrayList<Employee> employeeList, Map<Employee, Shift> export, LocalDate date, int employeesAssigned) {
        Employee employeeWithMinOvertime = null;
        int minOvertimeHours = Integer.MAX_VALUE;

        for (Employee employee : employeeList) {
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
