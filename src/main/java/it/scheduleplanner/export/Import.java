package it.scheduleplanner.export;

import java.time.LocalDate;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.util.stream.Stream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;

import it.scheduleplanner.utils.Employee;

/**
 * The Import class houses all necessary methods to import a schedule according to the format defined in<br>
 * - Export.CSVExport or<br>
 * - Export.exportBlankSchedule
 */
public final class Import {

	/*
	 * public methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	/**
	 * This method imports a schedule stored in a CSV file which has to have the same format<br>
	 * as the exported schedules by Export.CSVExport and Export.exportBlankSchedule.
	 * 
	 * @param pathToFile String: the path to the file to import <br>
	 * The file has to be named after the following format: dd.MM.yyyy.csv for example "somePath01.01.2024.csv"
	 * @param employeeSet Set of all employees
	 * @return schedule as instance of ShiftSchedulenterface
	 */
	public static ShiftScheduleInterface importSchedule(String pathToFile, Set<Employee> employeeSet) {
		LocalDate scheduleStart = findStartDateInFileName(pathToFile);
		String[] fileLines = readFile(pathToFile);
		
		if (scheduleStart == null || fileLines == null) {
			return null;
		}
		
		Map<LocalDate, Map<Employee, Shift>> shiftMap = createShiftMap(fileLines, employeeSet);
		
		return createSchedule(shiftMap, scheduleStart);
	}
	
	/*
	 * first level support methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private static LocalDate findStartDateInFileName(String pathToFile) {
		Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}.csv");
		Matcher matcher = pattern.matcher(pathToFile);
		
		//if no match was found, the file either is no schedule or was named wrong
		if (!matcher.find()) {
			return null;
		}
		
		return LocalDate.parse(matcher.group().split("\\.")[0]);
	}
	
	private static String[] readFile(String pathToFile) {
		String[] lines = null;
		
		try (Stream<String> stream = Files.lines(Path.of(pathToFile))){
		    lines = (String[])stream.toArray();
		} catch (IOException e) {
		    e.printStackTrace();
		    return null;
		}
		
		return lines;
	}
	
	private static Map<LocalDate, Map<Employee, Shift>> createShiftMap(String[] fileLines, Set<Employee> employeeSet) {
		boolean employeeListFlag = false;
		
		List<LocalDate> dates = null;
		List<String> linesWithEmployees = new ArrayList<>();
		
		Map<LocalDate, Map<Employee, Shift>> shiftMap = new HashMap<>();
		
		for (int i = 0; i < fileLines.length; i++) {
			String line = fileLines[i];

			//when the header is the present line, the line before has all the dates
			if (line.equals(Export.DEFINED_CSV_LINES.get(Export.DefinedLinesTag.HEADER))) {
				dates = getDates(fileLines[i-1]);
				linesWithEmployees.clear();
				employeeListFlag = true;
				continue;
			}
			
			//if a line begins with ";" it cannot be a schedule line with an employee
			if (employeeListFlag && line.startsWith(";")) {
				shiftMap.putAll(parseWeek(dates, linesWithEmployees, employeeSet));
				employeeListFlag = false;
				continue;
			}
			
			//collect all the lines which have employees in them
			if (employeeListFlag) {
				linesWithEmployees.add(line);
			}
		}
		
		//in case the last line has an employee and therefore parseWeek isn't called during the for loop for the last batch of employees
		if (employeeListFlag) {
			shiftMap.putAll(parseWeek(dates, linesWithEmployees, employeeSet));
		}
		
		return shiftMap;
	}
	
	private static ShiftScheduleInterface createSchedule(Map<LocalDate, Map<Employee, Shift>> shiftMap, LocalDate scheduleStart) {
		ShiftScheduleInterface schedule = new FixedShiftsSchedule(scheduleStart);
		
		//go through all the imported days
		for (LocalDate date : shiftMap.keySet()) {
			if (shiftMap.get(date).isEmpty()) {
				continue;
			}
			
			ShiftDayInterface day = new FixedShiftDay();
			
			for (Employee empl : shiftMap.get(date).keySet()) {
				//add employee with corresponding shift to day
				day.addEmployee(empl, shiftMap.get(date).get(empl));
			}
			
			schedule.addDay(date, day);
		}
		
		return schedule;
	}
	
	/*
	 * createShiftMap support methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private static List<LocalDate> getDates(String line) {
		List<LocalDate> datesList= new ArrayList<LocalDate>();
		
		String[] dates = line.split(";");
		
		/*
		 * first date is in the 4th cell of the line ad than every second one comes the next date. 
		 * 16 cells is the maximum needed even if the file has stored more cells
		 */
		for (int i = 3; i < 16; i += 2) {
			datesList.add(LocalDate.parse(dates[i], Export.FORMATTER_ddMMyyyy));
		}
		
		return datesList;
	}
	
	private static Map<LocalDate, Map<Employee, Shift>> parseWeek(List<LocalDate> dates, List<String> lines, Set<Employee> employeeSet) {
		List<String[]> splitLines = new ArrayList<String[]>();
		Map<LocalDate, Map<Employee, Shift>> weekMap = new HashMap<LocalDate, Map<Employee,Shift>>();
		
		lines.forEach((line) -> splitLines.add(line.split(";")));
		
		for (int i = 0; i < dates.size(); i++) {
			weekMap.put(dates.get(i), new HashMap<Employee, Shift>());
			
			for (String[] line : splitLines) {
				//check if employee of current time works this day
				if (line[(2 * i) + 2].isBlank() && line[(2 * i) + 3].isBlank()) {
					continue;
				}
				
				int id = -1;
				
				//try to get id from cell, if not parsable to int it is not an employee id
				try {
					id = Integer.parseInt(line[1]);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				
				Shift shift = getShift(line[(2 * i) + 2], line[(2 * i) + 3]);
				Employee empl = getEmployeeWithID(id, employeeSet);
				
				//if asked employee exists, put employee with corresponding shift into weekMap, no need to check shift as it can't be null
				if (empl != null) {
					weekMap.get(dates.get(i)).put(empl, shift);
				}
				
			}
		}
		
		return null;
	}
	
	/*
	 * parseWeek support methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private static Shift getShift(String morning, String afternoon) {
		if (morning.isBlank()) {
			return Shift.AFTERNOON;
		}
		
		else if (afternoon.isBlank()) {
			return Shift.MORNING;
		}
		
		else {
			return Shift.FULL;
		}
	}
	
	private static Employee getEmployeeWithID(int id, Set<Employee> employeeSet) {
		//search for employee in employeeSet with the asked id
		for (Employee employee : employeeSet) {
			if (employee.getEmployeeId() == id) {
				return employee;
			}
		}
		return null;
	}
	
	/**
	 * private constructor to suppress instantiation
	 */
	private Import() {
		super();
	}
}
















