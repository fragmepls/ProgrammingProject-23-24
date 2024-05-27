package it.scheduleplanner.planner;

import java.util.HashMap;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.utils.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class EmployeeComparator {

//TODO get employee with least working hours if no employee has enough hours add overtime hours and start again
    public static Map<Employee, Shift> getNext(ArrayList<Employee> employeeList, LocalDate date) {
        Map<Employee, Shift> export = new HashMap<>();
        for (Employee employee : employeeList) {
            if (!isAvailable(employee, date)) {
                continue;
            }
            if (employee.getWorkingHours() - 8 >= 0) {
                export.clear();
                export.put(employee, Shift.FULL);
                return export;
            }
            else if (employee.getWorkingHours() - 4 >= 0) {
                export.clear();
                export.put(employee, Shift.HALF);
                return export;
            }
        }


        return null;
    }

    private static boolean isAvailable(Employee employee, LocalDate date) {
//* TODO if is weekendWorker and date is weekend return true else false
        //  if employee is on holyday return false
        if (employee.isWeekendWorker()) {


        }
        else {
            //skip the weekend
        }
        return false; //TODO to be changed
    }

}


