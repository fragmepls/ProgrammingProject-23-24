package it.scheduleplanner.export;

import it.scheduleplanner.utils.Employee;

import java.util.Set;

/**
 * This Interface handles the export of data and provides the necessary methods.<br>
 * All possible actions are indicated by the methods.
 */
public interface Export {

	/**
	 * This method exports a FixedShiftsSchedule to a CSV File located in the indicated directory.<br>
	 * The CSV File will be named 'date of begin of schedule'.csv. In case a file with this name already exists, an increasing number will be added to the beginning of the name.<br>
	 * The name will then look like this: ('Nr.')date.csv
	 * 
	 * @param schedule Schedule of type ShifScheduleInterface
	 * @param pathToDirectory String describing the path to the desired directory
	 * <br><br>
	 * @return false if schedule is not exportable
	 * <li>true if the schedule is exportable
	 */
	boolean CSVExport(ShiftScheduleInterface scheduleToExport, String pathToDirectory);
	
	/**
	 * This method may be primarily used for debug purposes but can also be used to get an overview over all the employees.<br>
	 * It exports a JSON file containing all the necessary information bound to every Employee.
	 * 
	 * @param employees Set of Employees to be exported
	 * @param pathToDirectory String describing the path to the desired directory
	 * @return false if an error occurred
	 * <li> true if everything functioned
	 */
	public static Boolean employeeExport(Set<Employee> employees, String pathToDirectory) {
		return JSONExport.employeeExport(employees, pathToDirectory);
	}
}