package it.scheduleplanner.export;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import it.scheduleplanner.utils.Employee;

/**
 * An object that maps Employees to their respective Shift and may be used as a way to indicate a Shift plan for a day.
 */
public class FixedShiftDay implements ShiftDayInterface {

    private Map<Shift, List<Employee>> employees = new HashMap<>();

    /**
     * Basic constructor for FixedShiftDay
     */
    public FixedShiftDay() {
        employees.put(Shift.MORNING, new ArrayList<>());
        employees.put(Shift.AFTERNOON, new ArrayList<>());
        employees.put(Shift.FULL, new ArrayList<>());
    }

    
    @Override
    public void addEmployee(Employee employee) {
        employees.get(Shift.FULL).add(employee);
    }

    @Override
    public void addEmployee(Employee employee, Shift shift) {
        if (shift == Shift.HALF) {
            shift = Shift.MORNING;
        }
        employees.get(shift).add(employee);
    }

    @Override
    public Map<Shift, List<Employee>> getEmployees() {
        return employees;
    }

    
    @Override
    public String toString() {
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
    }
}
