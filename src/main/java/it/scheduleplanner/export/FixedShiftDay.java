package it.scheduleplanner.export;

import java.util.Map;
import java.util.HashMap;

import it.scheduleplanner.utils.Employee;

/**
 * An object that maps Employees to their respective Shift and may be used as a way to indicate a Shift plan for a day.
 */
public class FixedShiftDay implements ShiftDayInterface {

    private Map<Employee, Shift> employees = new HashMap<>();

    /**
     * Basic constructor for FixedShiftDay
     */
    public FixedShiftDay() {
        
    }

    @Override
    public void addEmployee(Employee employee, Shift shift) {
    	
    	if (shift == Shift.HALF) {
            shift = Shift.MORNING;
        }
        employees.put(employee, shift);
    }

    @Override
    public Map<Employee, Shift> getEmployees() {
        return employees;
    }

    //TODO repair toString
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Employee empl : employees.keySet()) {
            sb.append(empl.getName() + ", ID = " + empl.getEmployeeId() + " : " + employees.get(empl).toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
