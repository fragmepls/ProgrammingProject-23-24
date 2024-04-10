package it.scheduleplanner.export;

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
	
	public static void simpleExport(ShiftSchedule schedule){
		String fileName = createFile(schedule.getBeginOfCalendar());
		String content = createFileContent(schedule);
		writeToFile(fileName, content);
	}
	
	private static String createFile(LocalDate date) {
		String dateS = date.toString();
		String fileName = dateS + ".csv";
		int i = 1;
		try {
		    while (true) {
		    	File newFile = new File(fileName);
		    	if (newFile.createNewFile()) {
		    		// TODO log
		    		return fileName;
			    } 
			    else {
			    	fileName = "(" + i + ")" + dateS + ".csv";
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
	
	
	private static String createFileContent(ShiftSchedule schedule) {
		return "complete export coming soon";
	}
	
	
	private static void writeToFile(String file, String content) {
		try(PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))){
	        writer.println(content);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}


















