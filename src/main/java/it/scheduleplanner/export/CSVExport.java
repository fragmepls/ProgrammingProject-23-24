package it.scheduleplanner.export;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

import it.scheduleplanner.utils.EmployeeInterface;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;

import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.time.LocalDate;

public class CSVExport implements Export {
	
	private static Map<DefinedLinesTag, String> definedCSVLines = new HashMap<>();
	
	private static Map<LocalDate, ShiftDayInterface> schedule = new HashMap<>();
	private static List<LocalDate> scheduleDates = null;
	
	private static Map<LocalDate, Map<Shift, List<EmployeeInterface>>> exportMap = new HashMap<LocalDate, Map<Shift,List<EmployeeInterface>>>();

	private static List<String> fileContentList = new ArrayList<String>();
	
	private static LocalDate beginOfSchedule = null;
	private static LocalDate endOfSchedule = null;
	private static LocalDate beginOfExport = null;
	private static LocalDate endOfExport = null;
	
	public static void simpleScheduleExport(ShiftSchedule scheduleToExport, String pathToDirectory){
		initPredefinedLines();
		initVariables(scheduleToExport);
		
		createExportMap();
		String fileName = createFile(beginOfSchedule.toString(), pathToDirectory);
		
		createScheduleFileContent();
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
		    		System.out.println("create file: " + fileNew);
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
		return fileNew;
	}
	
	
	private static void createScheduleFileContent() {
		fileContentList.add(definedCSVLines.get(DefinedLinesTag.SCHEDULE_HEADER));
		
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
		definedCSVLines.put(DefinedLinesTag.SCHEDULE_HEADER, ";Monday;;Tuesday;;Wednesday;;Thursday;;Friday;;Saturday;;Sunday;");
		definedCSVLines.put(DefinedLinesTag.WEEK, "\nWeek Nr. ");
		definedCSVLines.put(DefinedLinesTag.DATE, ";Date:;");
		definedCSVLines.put(DefinedLinesTag.MORNING_FULL, ";morning;full day;morning;full day;morning;full day;morning;full day;morning;full day;morning;full day;morning;full day");
		definedCSVLines.put(DefinedLinesTag.AFTERNOON, ";afternoon;");
	}
	
	private static void initVariables(ShiftSchedule scheduleToExport) {
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
		
	}
	
	private static void writeWeek(LocalDate start) {
		DateTimeFormatter formatter_ddMMyyyy = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		
		List<EmployeeInterface> morningList = new ArrayList<EmployeeInterface>();
		List<EmployeeInterface> afternoonList = new ArrayList<EmployeeInterface>();
		List<EmployeeInterface> fullList = new ArrayList<EmployeeInterface>();
		
		int maxMorning = 0;
		int morningLines = 1;
		int maxAfternoon = 0;
		int afternoonLines = 1;
		int maxFull = 0;
		int fullLines = 1;
		int lines = 0;
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
		lines = Integer.max(morningLines + afternoonLines + 1, fullLines) + 2;
		
//		TODO ausfuehrlich testen und log
		
		String[] content = new String[lines];
		for (int i = 0; i < content.length; i++) {
			content[i] = "";
		}
		
		content[0] = definedCSVLines.get(DefinedLinesTag.WEEK) + start.get(WeekFields.of(Locale.ITALY).weekOfYear());
		for (LocalDate date = start; date.getDayOfYear() <= start.getDayOfYear() + 6; date = date.plusDays(1)) {
			content[0] += definedCSVLines.get(DefinedLinesTag.DATE) + date.format(formatter_ddMMyyyy);
		}
		content[1] = definedCSVLines.get(DefinedLinesTag.MORNING_FULL);
		
		
		for (LocalDate date = start; date.getDayOfYear() <= start.getDayOfYear() + 6; date = date.plusDays(1)) {
			
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
			else {
				//TODO
				morningList = exportMap.get(date).get(Shift.MORNING);
				afternoonList = exportMap.get(date).get(Shift.AFTERNOON);
				fullList = exportMap.get(date).get(Shift.FULL);
				int morning = morningList.size();
				int afternoon = afternoonList.size();
				int full = fullList.size();
				
				int i = 2;
			
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

















