package it.scheduleplanner.export;

import java.time.LocalDate;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.util.stream.Stream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;

import it.scheduleplanner.utils.Employee;


public final class Import {

	
	/*
	 * public methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public static ShiftScheduleInterface importSchedule(String pathToFile, Set<Employee> employeeSet) {
		LocalDate scheduleStart = findStartDateInFileName(pathToFile);
		
		String[] fileLines = readFile(pathToFile);
		
		Map<LocalDate, Map<Employee, Shift>> shiftMap = createShiftMap(fileLines, employeeSet);
		
		return createSchedule(shiftMap);
	}
	
	
	/*
	 * support methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private static LocalDate findStartDateInFileName(String pathToFile) {
		Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}.csv");
		Matcher matcher = pattern.matcher(pathToFile);
		
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

			if (line.equals(Export.DEFINED_CSV_LINES.get(Export.DefinedLinesTag.HEADER))) {
				dates = getDates(fileLines[i-1]);
				linesWithEmployees.clear();
				employeeListFlag = true;
				continue;
			}
			
			if (employeeListFlag && line.startsWith(";")) {
				shiftMap.putAll(parseWeek(dates, linesWithEmployees, employeeSet));
				employeeListFlag = false;
				continue;
			}
			
			if (employeeListFlag) {
				linesWithEmployees.add(line);
			}
		}
		
		if (employeeListFlag) {
			shiftMap.putAll(parseWeek(dates, linesWithEmployees, employeeSet));
		}
		
		return null;
	}
	
	
	private static Map<LocalDate, Map<Employee, Shift>> parseWeek(List<LocalDate> dates, List<String> lines, Set<Employee> employeeSet) {
		List<String[]> splitLines = new ArrayList<String[]>();
		
		lines.forEach((line) -> splitLines.add(line.split(";")));
		
		for (int i = 0; i < dates.size(); i++) {
			/*
			 * pro date
			 * splitLines forEach
			 * get Shift (own method)?
			 * put shift with corresponding employee (getEmployeeWithID(int id)) into map
			 */
		}
		
		return null;
	}
	
	private static Employee getEmployeeWithID(int id, Set<Employee> employeeSet) {
		for (Employee employee : employeeSet) {
			if (employee.getEmployeeId() == id) {
				return employee;
			}
		}
		return null;
	}
	
	private static List<LocalDate> getDates(String line) {
		List<LocalDate> datesList= new ArrayList<LocalDate>();
		
		String[] dates = line.split(";");
		
		for (int i = 3; i < dates.length; i += 2) {
			datesList.add(LocalDate.parse(dates[i], Export.FORMATTER_ddMMyyyy));
		}
		
		return datesList;
	}
	
	private static ShiftScheduleInterface createSchedule(Map<LocalDate, Map<Employee, Shift>> shiftMap) {
		return null;
	}
	
	/**
	 * private constructor to suppress instantiation
	 */
	private Import() {
		super();
	}
}
















