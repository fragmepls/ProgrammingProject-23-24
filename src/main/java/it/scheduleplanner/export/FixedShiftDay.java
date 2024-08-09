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
    //@Override
    /*public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Shift, List<Employee>> entry : employees.entrySet()) {
            sb.append(entry.getKey().toString()).append(": ");
            for (Employee employee : entry.getValue()) {
                sb.append(employee.getName()).append(", ");
            }
            if (!entry.getValue().isEmpty()) {
                sb.setLength(sb.length() - 2); // Remove the last comma and space
            }
            sb.append("\n");
        }
        return sb.toString();
    }*/
}
