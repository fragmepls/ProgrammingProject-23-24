package it.scheduleplanner.export;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Set;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.ClassDefinition;
import java.io.BufferedWriter;

import java.nio.file.Path;
import java.nio.file.Files;

import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.time.LocalDate;

public class CSVExport implements Export{
	
	private static Map<DefinedLinesTag, String> definedCSVLines = new HashMap<>();
	
	private static Map<LocalDate, FixedShiftDay> schedule = new HashMap<>();
	private static List<LocalDate> scheduleDates = null;
	
	private static Map<LocalDate, Map<Shift, List<Employee>>> exportMap = new HashMap<LocalDate, Map<Shift,List<Employee>>>();

	private static List<String> fileContentList = new ArrayList<String>();
	
	private static LocalDate beginOfSchedule = null;
	private static LocalDate endOfSchedule = null;
	private static LocalDate beginOfExport = null;
	private static LocalDate endOfExport = null;
	
	private static int exportLength = 0;
	
	public static void simpleScheduleExport(FixedShiftsSchedule scheduleToExport, String pathToDirectory){
		initPredefinedLines();
		initVariables(scheduleToExport);
		
		createExportMap();
		String fileName = createFile(beginOfSchedule.toString(), pathToDirectory);
		
		createFileContent();
		writeToFile(fileName);
	}
	
	private static String createFile(String title, String pathToDirectory) {
		String file = title + ".csv";
		String fileNew = "";
		if (!pathToDirectory.endsWith("/")) {
			pathToDirectory += "/";
		}
		
		fileNew = pathToDirectory + file;
		int i = 1;
		try {
		    while (true) {
		    	if (!Files.exists(Path.of(fileNew))) {
		    		Files.createFile(Path.of(fileNew));
		    		// TODO log
		    		return fileNew;
			    } 
			    else {
			    	fileNew = pathToDirectory + "(" + i + ")" + file;
			    	i++;
				}
			}
		} 
		catch (IOException e) {
			//TODO log
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static void createFileContent() {
		fileContentList.add(definedCSVLines.get(DefinedLinesTag.SCHEDULE_HEADER));
		
		for (LocalDate date = beginOfExport; date.getDayOfYear() <= endOfExport.getDayOfYear(); date = date.plusDays(7)) {
			writeWeek(date);
		}
		
	}
	
	
	private static void writeToFile(String file) {
		try{
			Files.write(Path.of(file), fileContentList);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private static void initPredefinedLines() {
		definedCSVLines.put(DefinedLinesTag.SCHEDULE_HEADER, ";Monday;;Tuesday;;Wednesday;;Thursday;;Friday;;Saturday;;Sunday;");
		definedCSVLines.put(DefinedLinesTag.WEEK, "\nWeek Nr. ");
		definedCSVLines.put(DefinedLinesTag.DATE, ";Date:;");
		definedCSVLines.put(DefinedLinesTag.MORNING_FULL, ";morning;full day;morning;full day;morning;full day;morning;full day;morning;full day;morning;full day;morning;full day");
		definedCSVLines.put(DefinedLinesTag.AFTERNOON, ";afternoon;");
	}
	
	private static void initVariables(FixedShiftsSchedule scheduleToExport) {
		schedule = scheduleToExport.getSchedule();
		scheduleDates = new ArrayList<>(schedule.keySet());
		Collections.sort(scheduleDates);
		beginOfSchedule = scheduleToExport.getBeginOfSchedule();
		endOfSchedule = scheduleDates.get(scheduleDates.size() - 1);
	}
	
	private static void createExportMap() {
		
		for (LocalDate date : scheduleDates) {
			exportMap.put(date, schedule.get(date).getEmployees());
		}
		
		switch(beginOfSchedule.getDayOfWeek().toString()) {
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
		
		switch(endOfSchedule.getDayOfWeek().toString()) {
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
		
		exportLength = endOfExport.getDayOfYear() - beginOfExport.getDayOfYear();
	}
	
	private static void writeWeek(LocalDate start) {
		int maxMorning = 0;
		int maxAfternoon = 0;
		int maxFull = 0;
		int lines = 0;
		for (LocalDate date = start; date.getDayOfYear() <= start.getDayOfYear() + 6; date.plusDays(1)) {
			if (scheduleDates.contains(date)) {
				int temp = 0;
				temp = exportMap.get(date).get(Shift.MORNING).size();
				if (temp > maxMorning) {
					maxMorning = temp;
				}
				temp = exportMap.get(date).get(Shift.AFTERNOON).size();
				if (temp > maxAfternoon) {
					maxAfternoon = temp;
				}
				temp = exportMap.get(date).get(Shift.FULL).size();
				if (temp > maxFull) {
					maxFull = temp;
				}
			}
		}
		lines = Integer.max(maxMorning + maxAfternoon + 1, maxFull);
		
		fileContentList.add(definedCSVLines.get(DefinedLinesTag.WEEK) + start.get(WeekFields.of(Locale.ITALY).weekOfYear()));
	}
}

















