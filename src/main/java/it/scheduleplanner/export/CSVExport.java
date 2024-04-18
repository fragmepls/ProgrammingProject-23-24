package it.scheduleplanner.export;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;

import java.nio.file.Path;
import java.nio.file.Files;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class CSVExport implements Export{
	
	private static Map<DefinedLinesTag, String> definedCSVLines = new HashMap<>();
	
	private static Map<LocalDate, FixedShiftDay> schedule = new HashMap<>();
	private static List<LocalDate> scheduleDates = null;
	
	private static LocalDate beginOfSchedule = null;
	private static LocalDate endOfSchedule = null;
	
	public static void simpleScheduleExport(ShiftSchedule scheduleToExport, String pathToDirectory){
		initPredefinedLines();
		initVariables(scheduleToExport);
		String fileName = createFile(beginOfSchedule.toString(), pathToDirectory);
		List<String> content = createFileContent();
		writeToFile(fileName, content);
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
	
	
	private static List<String> createFileContent() {
		List<String> contentList = new ArrayList<String>();
		contentList.add(definedCSVLines.get(DefinedLinesTag.SCHEDULE_HEADER));
		
		return contentList;
	}
	
	
	private static void writeToFile(String file, List<String> content) {
		try{
			Files.write(Path.of(file), content);
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
	
	private static void initVariables(ShiftSchedule scheduleToExport) {
		schedule = scheduleToExport.getFixedShiftSchedule();
		scheduleDates = new ArrayList<>(schedule.keySet());
		Collections.sort(scheduleDates);
		beginOfSchedule = scheduleToExport.getBeginOfSchedule();
		endOfSchedule = scheduleDates.get(scheduleDates.size() - 1);
	}
	
	private static Map<LocalDate, Map<Shift, List<Employee>>> createExportMap() {
		Map<LocalDate, Map<Shift, List<Employee>>> exportMap = new HashMap<LocalDate, Map<Shift,List<Employee>>>();
		
		for (LocalDate date : scheduleDates) {
			exportMap.put(date, schedule.get(date).getEmployees());
		}
		
		return exportMap;
	}
}

















