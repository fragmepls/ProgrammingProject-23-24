package it.scheduleplanner.export;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;

import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.time.LocalDate;

import it.scheduleplanner.utils.Employee;

/**
 * The Export class houses all necessary methods to export:<br>
 * - empty schedules 'Export.exportBlankSchedule'<br>
 * - it.scheduleplanner.export.FixedShiftsSchedule objects 'Export.CSVExport'<br>
 * - a set of it.schedulaplanner.utils.Employee objects 'Export.employeeExport'<br>
 */
public final class Export {
	
	private enum MapKeys {
		BEGIN_OF_SCHEDULE,
		END_OF_SCHEDULE,
		BEGIN_OF_EXPORT,
		END_OF_EXPORT,
	}
	
	protected enum DefinedLinesTag {
		DAYS,
		WEEK,
		DATE,
		HEADER
	}
	
	protected static final Map<DefinedLinesTag, String> DEFINED_CSV_LINES = Map.of(
			DefinedLinesTag.DAYS, ";;Monday;;Tuesday;;Wednesday;;Thursday;;Friday;;Saturday;;Sunday;",
			DefinedLinesTag.WEEK, ";Week Nr. ",
			DefinedLinesTag.DATE, ";Date:;",
			DefinedLinesTag.HEADER, "Name;ID;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon");
	
	protected static final DateTimeFormatter FORMATTER_ddMMyyyy = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	/*
	 * public methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	/**
	 * This method exports a FixedShiftsSchedule to a CSV File located in the indicated directory.<br>
	 * The CSV File will be named 'date of begin of schedule'.csv. In case a file with this name already exists, an increasing number will be added to the beginning of the name.<br>
	 * The name will then look like this: ('Nr.')date.csv
	 * 
	 * @param scheduleToExport Schedule of type ShifScheduleInterface
	 * @param pathToDirectory String describing the path to the desired directory
	 */
	public static boolean CSVExport(ShiftScheduleInterface scheduleToExport, Set<Employee> employeeSet, String pathToDirectory){
		Map<MapKeys, Object> vars = new HashMap<>();
		List<String> fileContentList = new ArrayList<String>();

		List<Employee> employeeList = sortEmployees(employeeSet);
		
		vars.putAll(initVariables(scheduleToExport));
		vars.putAll(calculateExportBeginEnd((LocalDate)vars.get(MapKeys.BEGIN_OF_SCHEDULE), (LocalDate)vars.get(MapKeys.END_OF_SCHEDULE)));
		
		createScheduleFileContent(scheduleToExport.getSchedule(), employeeList, fileContentList, (LocalDate)vars.get(MapKeys.BEGIN_OF_EXPORT), (LocalDate)vars.get(MapKeys.END_OF_EXPORT));

		String path = FileCreator.create(((LocalDate)vars.get(MapKeys.BEGIN_OF_SCHEDULE)).toString(), pathToDirectory, ".csv", false).get(true);
		
		if(path == null) {
			return false;
		}
		
		return writeToFile(fileContentList, path);
	}
	
	/**
	 * 
	 * @param begin
	 * @param end
	 * @param employeeSet
	 * @param pathToDirectory
	 * @return
	 */
	public static Map<Boolean, String> exportBlankSchedule(LocalDate begin, LocalDate end, Set<Employee> employeeSet, String pathToDirectory) {
		Map<MapKeys, Object> vars = new HashMap<>();
		List<String> fileContentList = new ArrayList<String>();
		
		List<Employee> employeeList = sortEmployees(employeeSet);
		
		vars.putAll(calculateExportBeginEnd(begin, end));
		fileContentList.add(DEFINED_CSV_LINES.get(DefinedLinesTag.DAYS));

		LocalDate date = (LocalDate)vars.get(MapKeys.BEGIN_OF_EXPORT);
		do {
			writeDatesAndHeader(fileContentList, date);
			
			//add all the employees
			employeeList.forEach((employee) -> fileContentList.add(employee.getName() + ";" + employee.getEmployeeId()));
			
			//add empty line
			fileContentList.add(";");
			
			date = date.plusDays(7);
		} while(date.isBefore((LocalDate)vars.get(MapKeys.END_OF_EXPORT)));
		
		//create file
		String path = FileCreator.create(begin.toString(), pathToDirectory, ".csv", false).get(true);
		
		if(path == null) {
			return Map.of(false, null);
		}
		
		//write content to file
		return Map.of(writeToFile(fileContentList, path), path);
	}

	/**
	 * This method may be primarily used for debug purposes but can also be used to get an overview over all the employees.<br>
	 * It exports a JSON file containing all the necessary information bound to every Employee.
	 * 
	 * @param employees Set of Employees to be exported
	 * @param pathToDirectory String describing the path to the desired directory
	 * @return false if an error occurred
	 * <li> true if everything functioned
	 */
	public static boolean employeeExport(Set<Employee> employees, String pathToDirectory){
		OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		
		String file = FileCreator.create("employees_" + LocalDate.now().toString(), pathToDirectory, ".json", true).get(true);
		if (file == null) {
			return false;
		}
		
		try {
			OBJECT_MAPPER.writeValue(new File(file), employees);
		  }
		  catch (IOException e) {
			  e.printStackTrace();
			  return false;
		  }
		
		return true;
	}
	
	/*
	 * content creation methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private static void writeDatesAndHeader(List<String> fileContentList, LocalDate date) {
		int weekNr = date.get(WeekFields.of(Locale.ITALY).weekOfYear());
		//create line with dates
		String dates = "";
		for (int i = 0; i < 7; i++, date = date.plusDays(1)) {
			dates += DEFINED_CSV_LINES.get(DefinedLinesTag.DATE) + date.format(FORMATTER_ddMMyyyy);
		}
		
		//create line with week number and dates
		fileContentList.add(DEFINED_CSV_LINES.get(DefinedLinesTag.WEEK) + weekNr + dates);

		//add header
		fileContentList.add(DEFINED_CSV_LINES.get(DefinedLinesTag.HEADER));
	}

	private static void createScheduleFileContent(Map<LocalDate, ShiftDayInterface> schedule, List<Employee> employeeList, List<String> fileContentList, LocalDate beginOfExport, LocalDate endOfExport) {
		fileContentList.add(DEFINED_CSV_LINES.get(DefinedLinesTag.DAYS)); //add Days to content
		
		//iterate trough the dates and add the days to the content
		LocalDate date = beginOfExport;
		do {
			System.out.println(date + " = first day in export");
			
			writeWeek(schedule, fileContentList, date, employeeList);
			
			date = date.plusDays(7);
		}while(date.isBefore(endOfExport));
				
	}
	
	/**
	 * 
	 * @param start (date of Monday)
	 */
	private static void writeWeek(Map<LocalDate, ShiftDayInterface> schedule, List<String> fileContentList, LocalDate start, List<Employee> employeeList) {
		writeDatesAndHeader(fileContentList, start);
		
		Map<Employee, String> weekMap = new HashMap<Employee, String>();

		employeeList.forEach((employee) -> weekMap.put(employee, ";" + employee.getEmployeeId()));
		
		for (LocalDate date = start; date.isBefore(start.plusDays(7)); date = date.plusDays(1)) {
			System.out.println(date.toString());
			writeDay(schedule.get(date), weekMap);
		}
//		
//		/*
//		 * The following code lines do the same as 
//		 * weekMap.keySet().forEach((employee) -> fileContentList.add(employee.getName() + weekMap.get(employee)));
//		 * would do with the difference, that the employees are getting sorted and therefore have the same order all the time
//		 */
//		List<Employee> list = new ArrayList<Employee>(weekMap.keySet());
//		Collections.sort(list, (e1, e2) -> e1.getEmployeeId() - e2.getEmployeeId());
		for (Employee empl : employeeList) {
			fileContentList.add(empl.getName() + weekMap.get(empl));
		}
		
		fileContentList.add(";");
	}
	
	private static void writeDay(ShiftDayInterface day, Map<Employee, String> weekMap) {
		if (day == null) {
			weekMap.keySet().forEach((employee) -> weekMap.replace(employee, weekMap.get(employee) + ";;"));
			System.out.println("Day = null\n");
			return;
		}
		
		Map<Employee, Shift> employees = day.getEmployees();
		
		for (Employee employee : weekMap.keySet()) {
			try {
				switch (employees.get(employee)) {
				case FULL:
					weekMap.replace(employee, weekMap.get(employee) + ";x;x");
					break;
					
				case MORNING:
					weekMap.replace(employee, weekMap.get(employee) + ";x;");
					break;
					
				case AFTERNOON:
					weekMap.replace(employee, weekMap.get(employee) + ";;x");
					break;
					
				default:
					weekMap.replace(employee, weekMap.get(employee) + ";;");
					break;
				}
			} catch (Exception e) { //if the employee is not in the day
				weekMap.replace(employee, weekMap.get(employee) + ";;");
				System.out.println(employee.getName() + " has his off-day");
			}
		 
		}
	}
	
	/*
	 * initialization methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	private static Map<MapKeys, Object> initVariables(ShiftScheduleInterface scheduleToExport) {
		List<LocalDate> scheduleDates = new ArrayList<>(scheduleToExport.getSchedule().keySet()); //list of all dates occupied by a day
		Collections.sort(scheduleDates); //sort dates
		LocalDate beginOfSchedule = scheduleToExport.getBeginOfSchedule(); //first date occupied by a day
		LocalDate endOfSchedule = scheduleDates.get(scheduleDates.size() - 1); //last date occupied by a day
		return Map.of(
				MapKeys.BEGIN_OF_SCHEDULE, beginOfSchedule,
				MapKeys.END_OF_SCHEDULE, endOfSchedule);
	}

	private static Map<MapKeys, Object> calculateExportBeginEnd(LocalDate begin, LocalDate end) {
		LocalDate beginOfExport = null;
		LocalDate endOfExport = null;
		
		//calculate first date to be exported which has to be the first Monday prior to the first occupied date unless itself is a Monday. 
		switch(begin.getDayOfWeek().toString()) {
		case "MONDAY":
			beginOfExport = begin;
			break;
		case "TUESDAY":
			beginOfExport = begin.minusDays(1);
			break;
		case "WEDNESDAY":
			beginOfExport = begin.minusDays(2);
			break;
		case "THURSDAY":
			beginOfExport = begin.minusDays(3);
			break;
		case "FRIDAY":
			beginOfExport = begin.minusDays(4);
			break;
		case "SATURDAY":
			beginOfExport = begin.minusDays(5);
			break;
		case "SUNDAY":
			beginOfExport = begin.minusDays(6);
			break;
		}

		//calculate last date to be exported which has to be the first Friday after to the last occupied date unless itself is a Friday. 
		switch(end.getDayOfWeek().toString()) {
		case "MONDAY":
			endOfExport = end.plusDays(6);
			break;
		case "TUESDAY":
			endOfExport = end.plusDays(5);
			break;
		case "WEDNESDAY":
			endOfExport = end.plusDays(4);
			break;
		case "THURSDAY":
			endOfExport = end.plusDays(3);
			break;
		case "FRIDAY":
			endOfExport = end.plusDays(2);
			break;
		case "SATURDAY":
			endOfExport = end.plusDays(1);
			break;
		case "SUNDAY":
			endOfExport = end;
			break;
		}
		
		return Map.of(
				MapKeys.BEGIN_OF_EXPORT, beginOfExport,
				MapKeys.END_OF_EXPORT, endOfExport);
	}
	
	private static List<Employee> sortEmployees(Set<Employee> employeeSet){
		List<Employee> employeeList = new ArrayList<Employee>(employeeSet);
		Collections.sort(employeeList, (e1, e2) -> e1.getEmployeeId() - e2.getEmployeeId());
		return employeeList;
	}

	/*
	 * export methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private static boolean writeToFile(List<String> fileContentList, String pathToFile) {
		try{
			Files.write(Path.of(pathToFile), fileContentList);
			System.out.println("write to file: " + pathToFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
		return true;
	}

	
	/**
	 * private constructor to suppress instantiation
	 */
	private Export() {
		super();
	}
}

















