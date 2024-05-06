package it.scheduleplanner.export;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import it.scheduleplanner.utils.EmployeeInterface;

/**
 * An object that maps Employees to their respective Shift and may be used as a way to indicate a Shift plan for a day.
 */
public class FixedShiftDay implements ShiftDayInterface {

	private Map<Shift, List<EmployeeInterface>> employees = new HashMap<>();
	
	/**
	 * basic Constructor for FixedShiftDay
	 */
	public FixedShiftDay() {
		employees.put(Shift.MORNING, new ArrayList<>());
		employees.put(Shift.AFTERNOON, new ArrayList<>());
		employees.put(Shift.FULL, new ArrayList<>());
	}
	
	public void addEmployee(EmployeeInterface employee){
		employees.get(Shift.FULL).add(employee);
	}

	public void addEmployee(EmployeeInterface employee, Shift shift){
		if (shift.equals(Shift.HALF)) {
			shift = Shift.MORNING;
		}
		employees.get(shift).add(employee);
	}
	
	public Map<Shift, List<EmployeeInterface>> getEmployees() {
		return employees;
	}
	
}
