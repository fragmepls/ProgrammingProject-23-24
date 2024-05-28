package it.scheduleplanner.planner;

import java.time.DayOfWeek;
import java.util.HashMap;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.utils.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class EmployeeComparator {

    public static Map<Employee, Shift> getNext(ArrayList<Employee> employeeList, LocalDate date) {
        Map<Employee, Shift> export = new HashMap<>();

        for (Employee employee1 : employeeList) {
            if (!isAvailable(employee1, date)) {
                continue;
            }
            if ((employee1.getWorkingHours()*100) - 8 >= 0) {
                export.clear();
                export.put(employee1, Shift.FULL);
                return export;
            }
            else if ((employee1.getWorkingHours()*100) - 4 >= 0) {
                export.clear();
                export.put(employee1, Shift.HALF);
                return export;
            }
            else {
                Employee employeeWithMinOvertime = employeeList.get(0);
                double minOvertimeHours = employeeWithMinOvertime.getOverTimeHours();

                for (Employee employee2 : employeeList) {
                    double currentOvertimeHours = employee2.getOverTimeHours();


                    if (currentOvertimeHours < minOvertimeHours) {
                        minOvertimeHours = currentOvertimeHours;
                        employeeWithMinOvertime = employee2;

                    }
                }
            }
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



