package it.scheduleplanner.export;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

import it.scheduleplanner.utils.Employee;

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
public class Export {
	
	private static Map<DefinedLinesTag, String> definedCSVLines = new HashMap<>();
	
	private static Map<LocalDate, ShiftDayInterface> schedule = new HashMap<>();
	private static List<LocalDate> scheduleDates = null;
	
	private static Map<LocalDate, Map<Shift, List<Employee>>> exportMap = new HashMap<LocalDate, Map<Shift,List<Employee>>>();

	private static List<String> fileContentList = new ArrayList<String>();
	
	private static LocalDate beginOfSchedule = null;
	private static LocalDate endOfSchedule = null;
	private static LocalDate beginOfExport = null;
	private static LocalDate endOfExport = null;
	
	private static DateTimeFormatter formatter_ddMMyyyy = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	
	/**
	 * This method exports a FixedShiftsSchedule to a CSV File located in the indicated directory.<br>
	 * The CSV File will be named 'date of begin of schedule'.csv. In case a file with this name already exists, an increasing number will be added to the beginning of the name.<br>
	 * The name will then look like this: ('Nr.')date.csv
	 * 
	 * @param scheduleToExport Schedule of type ShifScheduleInterface
	 * @param pathToDirectory String describing the path to the desired directory
	 */
	public static boolean CSVExport(ShiftScheduleInterface scheduleToExport, String pathToDirectory){
		initPredefinedLines();
		initVariables(scheduleToExport);
		
		//createExportMap();
		//String fileName = createFile(beginOfSchedule.toString(), pathToDirectory);
		
		createScheduleFileContent();
		//writeToFile(fileName);
		
		return true; //TODO change to meaningful implementation
	}
	
	public static boolean exportBlankSchedule(LocalDate begin, LocalDate end, List<Employee> employeeList) {
		initPredefinedLines();
		calculateBeginEnd(begin, end);
		fileContentList.add(definedCSVLines.get(DefinedLinesTag.DAYS));
		do {
			String dates = "";
			for (int i = 1; i < 7; i++, beginOfExport = beginOfExport.plusDays(1)) {
				dates += definedCSVLines.get(DefinedLinesTag.DATE) + beginOfExport.format(formatter_ddMMyyyy);
			}
			
			fileContentList.add(definedCSVLines.get(DefinedLinesTag.WEEK) + beginOfExport.get(WeekFields.of(Locale.ITALY).weekOfYear()) + dates);

			fileContentList.add(definedCSVLines.get(DefinedLinesTag.HEADER));
			
			for (Employee empl : employeeList) {
				fileContentList.add(empl.getName() + ';' + empl.getEmployeeId());
			}
			
			fileContentList.add("\n");
			
		} while(beginOfExport.isBefore(endOfExport));
		
		/*
		 * create file
		 * write to file
		 */
		return true; //TODO change to meaningful implementation
	}

	
	
	private static void createScheduleFileContent() {
		fileContentList.add(definedCSVLines.get(DefinedLinesTag.SCHEDULE_HEADER)); //add Header to content
		
		//iterate trough the dates and add the days to the content
		for (LocalDate date = beginOfExport; date.getDayOfYear() <= endOfExport.getDayOfYear(); date = date.plusDays(7)) {
			System.out.println(date + " = first day in export");
			writeWeek(date);
		}
		
	}
	
	
	private static void writeToFile(String file) {
		try{
			System.out.println("write to file: " + file);
			Files.write(Path.of(file), fileContentList);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
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
		
//		for (LocalDate date : scheduleDates) {
//			exportMap.put(date, schedule.get(date).getEmployees());
//		}
//		
		//calculate first date to be exported which has to be the first Monday prior to the first occupied date unless itself is a Monday. 
		switch(begin.getDayOfWeek().toString()) {
		case "MONDAY":
			beginOfExport = beginOfSchedule;
			break;
		case "TUESDAY":
			beginOfExport = beginOfSchedule.minusDays(1);
			break;
		case "WEDNESDAY":
			beginOfExport = beginOfSchedule.minusDays(2);
			break;
		case "THURSDAY":
			beginOfExport = beginOfSchedule.minusDays(3);
			break;
		case "FRIDAY":
			beginOfExport = beginOfSchedule.minusDays(4);
			break;
		case "SATURDAY":
			beginOfExport = beginOfSchedule.minusDays(5);
			break;
		case "SUNDAY":
			beginOfExport = beginOfSchedule.minusDays(6);
			break;
		}

		//calculate last date to be exported which has to be the first Friday after to the last occupied date unless itself is a Friday. 
		switch(end.getDayOfWeek().toString()) {
		case "MONDAY":
			endOfExport = endOfSchedule.plusDays(6);
			break;
		case "TUESDAY":
			endOfExport = endOfSchedule.plusDays(5);
			break;
		case "WEDNESDAY":
			endOfExport = endOfSchedule.plusDays(4);
			break;
		case "THURSDAY":
			endOfExport = endOfSchedule.plusDays(3);
			break;
		case "FRIDAY":
			endOfExport = endOfSchedule.plusDays(2);
			break;
		case "SATURDAY":
			endOfExport = endOfSchedule.plusDays(1);
			break;
		case "SUNDAY":
			endOfExport = endOfSchedule;
			break;
		}
		
	}
	
	/**
	 * 
	 * @param start (date of Monday)
	 */
	private static void writeWeek(LocalDate start) {
		
		List<Employee> morningList = new ArrayList<Employee>();
		List<Employee> afternoonList = new ArrayList<Employee>();
		List<Employee> fullList = new ArrayList<Employee>();
		
		int maxMorning = 0;
		int morningLines = 1;
		int maxAfternoon = 0;
		int afternoonLines = 1;
		int maxFull = 0;
		int fullLines = 1;
		int lines = 0;
		/*
		 * calculate the needed lines for the week
		 * lines per shift = number of employees working per shift.
		 */
		for (LocalDate date = start; date.getDayOfYear() <= start.getDayOfYear() + 6; date = date.plusDays(1)) {
			
			if (scheduleDates.contains(date)) {
				int temp = 0;
				
				temp = exportMap.get(date).get(Shift.MORNING).size();
				if (temp > maxMorning) {
					maxMorning = temp;
					if (maxMorning > 0) {
						morningLines = temp;
					}
				}
				temp = exportMap.get(date).get(Shift.AFTERNOON).size();
				if (temp > maxAfternoon) {
					maxAfternoon = temp;
					if (maxAfternoon > 0) {
						afternoonLines = temp;
					}
				}
				temp = exportMap.get(date).get(Shift.FULL).size();
				if (temp > maxFull) {
					maxFull = temp;
					if (maxFull > 0) {
						fullLines = temp;
					}
				}
			}
		}
		//calculating the needed amount of lines for the whole week to have a symmetric layout
		lines = Integer.max(morningLines + afternoonLines + 1, fullLines) + 2;
		

		
		//Array of length lines needed for the week
		String[] content = new String[lines];
		for (int i = 0; i < content.length; i++) {
			content[i] = "";
		}
		
		//add line indicating the weekdays
		content[0] = definedCSVLines.get(DefinedLinesTag.WEEK) + start.get(WeekFields.of(Locale.ITALY).weekOfYear());
		//add the line indicating the dates
		for (LocalDate date = start; date.getDayOfYear() <= start.getDayOfYear() + 6; date = date.plusDays(1)) {
			content[0] += definedCSVLines.get(DefinedLinesTag.DATE) + date.format(formatter_ddMMyyyy);
		}
		//add the line indicating the morning and full shift
		content[1] = definedCSVLines.get(DefinedLinesTag.MORNING_FULL);
		
		//iterate over all days of the week
		for (LocalDate date = start; date.getDayOfYear() <= start.getDayOfYear() + 6; date = date.plusDays(1)) {
			
			//day is not contained in the schedule
			if (!scheduleDates.contains(date)) {
				for (int i = 2; i < lines; i++) {
					if (i == morningLines + 2) {
						content[i] += definedCSVLines.get(DefinedLinesTag.AFTERNOON);
					}
					else {
						content[i] += ";;";
					}
				}
			}
			//day is contained in the schedule
			else {
				morningList = exportMap.get(date).get(Shift.MORNING);
				afternoonList = exportMap.get(date).get(Shift.AFTERNOON);
				fullList = exportMap.get(date).get(Shift.FULL);
				int morning = morningList.size();
				int afternoon = afternoonList.size();
				int full = fullList.size();
				
				int i = 2;
				
				//add all the employees to their shifts while maintaining symmetry.
				for (int j = 0; j < morningLines; j++, i++) {
					if (j < morning && j < full) {
						content[i] += ";" + morningList.get(j).getName() + ";" + fullList.get(j).getName();
					}
					else if (j < morning && j >= full){
						content[i] += ";" + morningList.get(j).getName() + ";";
					}
					else if (j >= morning && j < full){
						content[i] += ";;" + fullList.get(j).getName();
					}
					else {
						content[i] += ";;";
					}
				}
	
				if ((i - 2) < full) {
					content[i] += definedCSVLines.get(DefinedLinesTag.AFTERNOON) + fullList.get(i - 2).getName();
				}
				else {
					content[i] += definedCSVLines.get(DefinedLinesTag.AFTERNOON);
				}
				i++;
		
				for (int j = 0; j < afternoonLines; j++, i++) {
					if (j < afternoon && (i - 2) < full) {
						content[i] += ";" + afternoonList.get(j).getName() + ";" + fullList.get(i - 2).getName();
					}
					else if (j < afternoon && i >= full + 2) {
						content[i] += ";" + afternoonList.get(j).getName() + ";";
					}
					else if (j >= afternoon && i < full + 2) {
						content[i] += ";;" + fullList.get(i - 2).getName();
					}
					else {
						content[i] += ";;";
					}
				}
				while (i < lines) {
					content[i] += ";;" + fullList.get(i - 2).getName();
					i++;
				}
				
			}
			
			
		}
		
//		Adding generated Lines to List
		for (String string : content) {
			if (string != null) {
				fileContentList.add(string);
			}
		}
		
	}
	
	
}

















