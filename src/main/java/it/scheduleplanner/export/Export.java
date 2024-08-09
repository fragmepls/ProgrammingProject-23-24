package it.scheduleplanner.export;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

import it.scheduleplanner.utils.Employee;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;

import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.time.LocalDate;

/**
 * The CSVExport class houses all necessary methods to export:<br>
 * - it.scheduleplanner.FixedShiftsSchedule objects<br>
 * 
 */
public final class Export {
	
	private static Map<DefinedLinesTag, String> definedCSVLines = new HashMap<>();
	
	private static Map<LocalDate, ShiftDayInterface> schedule = new HashMap<>();
	private static List<LocalDate> scheduleDates = null;
	
	private static List<String> fileContentList = new ArrayList<String>();
	
	private static LocalDate beginOfSchedule = null;
	private static LocalDate endOfSchedule = null;
	private static LocalDate beginOfExport = null;
	private static LocalDate endOfExport = null;
	
	private static final DateTimeFormatter formatter_ddMMyyyy = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final ObjectMapper mapper = new ObjectMapper();

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
		initPredefinedLines();
		initVariables(scheduleToExport);
		calculateBeginEnd(beginOfSchedule, endOfSchedule);
		
		createScheduleFileContent(employeeSet);

		String path = FileCreator.create(beginOfSchedule.toString(), pathToDirectory, ".csv", false).get(true);
		
		if(path == null) {
			return false;
		}
		
		writeToFile(path);
		
		return true; //TODO change to meaningful implementation
	}
	
	/**
	 * 
	 * @param begin
	 * @param end
	 * @param employeeSet
	 * @param pathToDirectory
	 * @return
	 */
	public static boolean exportBlankSchedule(LocalDate begin, LocalDate end, Set<Employee> employeeSet, String pathToDirectory) {
		initPredefinedLines();
		calculateBeginEnd(begin, end);
		fileContentList.add(definedCSVLines.get(DefinedLinesTag.DAYS));
		
		LocalDate date = beginOfExport;
		do {
			writeDatesAndHeader(date);
			
			//add all the employees
			employeeSet.forEach((employee) -> fileContentList.add(employee.getName() + ";" + employee.getEmployeeId()));
			
			//add empty line
			fileContentList.add(";");
			
			date = date.plusDays(7);
		} while(date.isBefore(endOfExport));
		
		//create file
		String path = FileCreator.create(begin.toString(), pathToDirectory, ".csv", false).get(true);
		
		if(path == null) {
			return false;
		}
		
		//write content to file
		writeToFile(path);
		
		return true; //TODO change to meaningful implementation
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
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		String file = FileCreator.create("employees_" + LocalDate.now().toString(), pathToDirectory, ".json", true).get(true);
		if (file == null) {
			return false;
		}
		
		try {
			  mapper.writeValue(new File(file), employees);
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
	
	private static void writeDatesAndHeader(LocalDate date) {
		int weekNr = date.get(WeekFields.of(Locale.ITALY).weekOfYear());
		//create line with dates
		String dates = "";
		for (int i = 0; i < 7; i++, date = date.plusDays(1)) {
			dates += definedCSVLines.get(DefinedLinesTag.DATE) + beginOfExport.format(formatter_ddMMyyyy);
		}
		
		//create line with week number and dates
		fileContentList.add(definedCSVLines.get(DefinedLinesTag.WEEK) + weekNr + dates);

		//add header
		fileContentList.add(definedCSVLines.get(DefinedLinesTag.HEADER));
	}

	private static void createScheduleFileContent(Set<Employee> employeeSet) {
		fileContentList.add(definedCSVLines.get(DefinedLinesTag.DAYS)); //add Days to content
		
		//iterate trough the dates and add the days to the content
		LocalDate date = beginOfExport;
		do {
			System.out.println(date + " = first day in export");
			
			writeWeek(date, employeeSet);
			
			date = date.plusDays(7);
		}while(date.isBefore(endOfExport));
				
	}
	
	/**
	 * 
	 * @param start (date of Monday)
	 */
	private static void writeWeek(LocalDate start, Set<Employee> employeeSet) {
		writeDatesAndHeader(start);
		
		Map<Employee, String> weekMap = new HashMap<Employee, String>();

		employeeSet.forEach((employee) -> weekMap.put(employee, ";" + employee.getEmployeeId()));
		
		for (LocalDate date = start; date.isBefore(start.plusDays(7)); date = date.plusDays(1)) {
			writeDay(schedule.get(date), weekMap);
		}
		
		weekMap.keySet().forEach((employee) -> fileContentList.add(employee.getName() + weekMap.get(employee)));
		
	}
	
	private static void writeDay(ShiftDayInterface day, Map<Employee, String> weekMap) {
		if (day == null) {
			weekMap.keySet().forEach((employee) -> weekMap.replace(employee, weekMap.get(employee) + ";;"));
			return;
		}
		
		Map<Employee, Shift> employees = day.getEmployees();
		
		for (Employee employee : weekMap.keySet()) {
		
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
		 
		}
	}
	
	/*
	 * initialization methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private static void initPredefinedLines() {
		//definedCSVLines.put(DefinedLinesTag.EMPTY_LINE, ";"); TODO
		definedCSVLines.put(DefinedLinesTag.DAYS, ";;Monday;;Tuesday;;Wednesday;;Thursday;;Friday;;Saturday;;Sunday;");
		definedCSVLines.put(DefinedLinesTag.WEEK, ";Week Nr. ");
		definedCSVLines.put(DefinedLinesTag.DATE, ";Date:;");
		definedCSVLines.put(DefinedLinesTag.HEADER, "Name;ID;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon");
	}

	private static void initVariables(ShiftScheduleInterface scheduleToExport) {
		schedule = scheduleToExport.getSchedule(); //schedule
		scheduleDates = new ArrayList<>(schedule.keySet()); //list of all dates occupied by a day
		Collections.sort(scheduleDates); //sort dates
		beginOfSchedule = scheduleToExport.getBeginOfSchedule(); //first date occupied by a day
		endOfSchedule = scheduleDates.get(scheduleDates.size() - 1); //last date occupied by a day
	}

	private static void calculateBeginEnd(LocalDate begin, LocalDate end) {

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
		
	}

	/*
	 * export methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private static void writeToFile(String file) {
		try{
			Files.write(Path.of(file), fileContentList);
			System.out.println("write to file: " + file);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	
}

















