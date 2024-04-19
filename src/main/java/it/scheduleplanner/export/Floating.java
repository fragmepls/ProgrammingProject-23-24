package it.scheduleplanner.export;

import java.util.List;

public interface Floating {
	
	/**
	 * Adds employee to the Work Day with 'shift' as 'full'.
	 * @param employee Employee to add to the Work Day
	 */
	public void addEmployee(Employee employee);

	
	/**
	 * Adds employee to the Work Day with associated shift begin and end.
	 * @param employee Employee to add to the Work Day
	 * @param begin Determines the begin of the shift
	 * @param end Determines the end of the shift
	 */
	public void addEmployee(Employee employee, int begin, int end);
	
	/**
	 * Returns ArrayList of the assigned employees with parameters: [Employee employee, Shift shift / shift begin*, shift end**]<br/>
	 * 
	 * * either enum Shift constant or shift begin
	 * ** only if a shift begin was specified
	 * 
	 * @return 
	 */
	public List<Object[]> getEmployees();
}