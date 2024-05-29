package it.scheduleplanner.planner;

import java.time.DayOfWeek;
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
            if (employee.isFulltimeWorker()) {
                employee.setWorkingHours(40);
            }
            else employee.setWorkingHours(20);
        }

        if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            for (Employee employee : employeeList) {
                if (employee.isFulltimeWorker()) {
                    employee.setWorkingHours(40);
                }
                else employee.setWorkingHours(20);
            }
        }

        for (int i = 1; i == numberOfEmployeesPerDay; i++) {
            boolean fullShiftAssigned = false;
            boolean morningShiftAssigned = false;
            boolean afternoonShiftAssigned = false;

            // First pass: Try to assign a FULL shift or both MORNING and AFTERNOON shifts
            for (Employee employee1 : employeeList) {
                if (!isAvailable(employee1, date)) {
                    continue;
                }

                if ((employee1.getWorkingHours() * 100) - 8 >= 0) {
                    export.clear();
                    export.put(employee1, Shift.FULL);
                    fullShiftAssigned = true;
                    return export;
                }
            }

            // Second pass: Try to assign MORNING and AFTERNOON shifts if FULL shift is not assigned
            if (!fullShiftAssigned) {
                for (Employee employee : employeeList) {
                    if (!isAvailable(employee, date)) {
                        continue;
                    }

                    if ((employee.getWorkingHours() * 100) - 4 >= 0) {
                        if (!morningShiftAssigned) {
                            export.put(employee, Shift.MORNING);
                            morningShiftAssigned = true;
                        }
                        else if (!afternoonShiftAssigned) {
                            export.put(employee, Shift.AFTERNOON);
                            afternoonShiftAssigned = true;
                            return export;
                        }
                    }
                }
            }

            // Third pass: Assign missing shift to the employee with the least overtime hours
            if (!fullShiftAssigned && (!morningShiftAssigned || !afternoonShiftAssigned)) {
                Employee employeeWithMinOvertime = null;
                double minOvertimeHours = Double.MAX_VALUE;

                for (Employee employee : employeeList) {
                    if (!isAvailable(employee, date)) {
                        continue;
                    }

                    double currentOvertimeHours = employee.getOverTimeHours();
                    if (currentOvertimeHours < minOvertimeHours) {
                        minOvertimeHours = currentOvertimeHours;
                        employeeWithMinOvertime = employee;
                    }
                }

                if (employeeWithMinOvertime != null) {
                    if (!morningShiftAssigned) {
                        export.put(employeeWithMinOvertime, Shift.MORNING);
                    }
                    else {
                        export.put(employeeWithMinOvertime, Shift.AFTERNOON);
                    }
                    return export;
                }
            }

            return export.isEmpty() ? null : export;
        }
        return null;
    }

    private static boolean isAvailable(Employee employee, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
//        if (!employee.isNotOnVaccation) {
//            return false;
//        }
//        else {
        if (employee.getFreeDay() == dayOfWeek) {
            return false;
        }
        else {
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                if (!employee.isWeekendWorker()) {
                    return false;
                }
            }
            return true;
        }
    }

}



