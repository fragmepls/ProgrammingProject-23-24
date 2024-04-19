package it.scheduleplanner.export;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class FloatingShiftDay{

	private List<Object[]> employees = new ArrayList<Object[]>();
	
	/**
	 * Adds employee to the Work Day with 'shift' as 'full'.
	 * @param employee Employee to add to the Work Day
	 */
	public void addEmployee(Employee employee){
		Object[] temp = {employee, Shift.FULL};
		employees.add(temp);
	}

	/**
	 * Adds employee to the Work Day with associated shift begin and end.
	 * @param employee Employee to add to the Work Day
	 * @param begin Determines the begin of the shift
	 * @param end Determines the end of the shift
	 */
	public void addEmployee(Employee employee, int begin, int end){
		Object[] temp = {employee, begin, end};
		employees.add(temp);
	}
	
	/**
	 * 
	 * @return Employees mapped to Shift
	 */
	public List<Object[]> getEmployees() {
		return employees;
	}
}
