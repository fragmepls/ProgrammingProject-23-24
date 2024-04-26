package it.scheduleplanner.export;

import java.util.List;
import java.util.Map;

import it.scheduleplanner.utils.EmployeeInterface;

public interface ShiftDayInterface {
	
	/**
	 * Adds employee to the Work Day with 'shift' as 'full'.
	 * @param employee Employee to add to the Work Day
	 */
	public void addEmployee(EmployeeInterface employee);

	/**
	 * Adds employee to the Work Day with associated shift.
	 * @param employee Employee to add to the Work Day
	 * @param shift To select from enum Shift, determines the shift
	 */
	public void addEmployee(EmployeeInterface employee, Shift shift);

	/**
	 * 
	 * @return Employees mapped to Shift
	 */
	public Map<Shift, List<EmployeeInterface>> getEmployees();
}
