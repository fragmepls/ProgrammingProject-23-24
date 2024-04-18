package it.scheduleplanner.export;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class FixedShiftDay extends Day{

	private Map<Shift, List<Employee>> employees = new HashMap<>();
	
	/**
	 * basic Constructor for FixedShiftDay
	 */
	public FixedShiftDay() {
		employees.put(Shift.MORNING, new ArrayList<>());
		employees.put(Shift.AFTERNOON, new ArrayList<>());
		employees.put(Shift.FULL, new ArrayList<>());
	}

	/**
	 * Adds employee to the Work Day with 'shift' as 'full'.
	 * @param employee Employee to add to the Work Day
	 */
	public void addEmployee(Employee employee){
		employees.get(Shift.FULL).add(employee);
	}

	/**
	 * Adds employee to the Work Day with associated shift.
	 * @param employee Employee to add to the Work Day
	 * @param shift To select from enum Shift, determines the shift
	 */
	public void addEmployee(Employee employee, Shift shift){
		employees.get(shift).add(employee);
	}
	
	/**
	 * 
	 * @return Employees mapped to Shift
	 */
	public Map<Shift, List<Employee>> getEmployees() {
		return null;
	}
	
}
