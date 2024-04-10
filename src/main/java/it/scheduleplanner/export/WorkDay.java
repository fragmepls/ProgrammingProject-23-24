package it.scheduleplanner.export;

import it.scheduleplanner.utils.Employee;
import java.util.ArrayList;

public class WorkDay implements Day{

	private ArrayList<Object[]> employees = new ArrayList<Object[]>();
	
	/**
	 * basic Constructor for WorkDay
	 */
	public WorkDay() {
	}
	

	/**
	 * Adds employee to the Work Day with 'shift' as 'full'.
	 * @param employee Employee to add to the Work Day
	 */
	public void addEmployee(Employee employee){
		Object[] temp = {employee, Shift.FULL};
		employees.add(temp);
	}

	/**
	 * Adds employee to the Work Day with associated shift.
	 * @param employee Employee to add to the Work Day
	 * @param shift To select from enum Shift, determines the shift
	 */
	public void addEmployee(Employee employee, Shift shift){
		Object[] temp = {employee, shift};
		employees.add(temp);
	}

	/**
	 * Adds employee to the Work Day with associated shift begin and end.
	 * @param employee Employee to add to the Work Day
	 * @param begin Determines the begin of the shift
	 * @param end Determines the end of the shift
	 */
//	public void addEmployee(Employee employee, int begin, int end){
//		Object[] temp = {employee, begin, end};
//		employees.add(temp);
//	}

	/**
	 * 
	 * @return ArrayList of the assigned employees with parameters: [Employee employee, Shift shift / shift begin*, shift end**]<br/>
	 * 
	 * * either enum Shift constant or shift begin
	 * ** only if a shift begin was specified
	 */
	public ArrayList<Object[]> getEmployeesWithShifts() {
		return employees;
	}
	
	
}
