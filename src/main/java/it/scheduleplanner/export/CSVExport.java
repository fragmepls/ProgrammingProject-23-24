package it.scheduleplanner.export;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class CSVExport implements Export{
	
	public static void simpleExport(CalendarInt calendar){
		String fileName = createFile(calendar.getBeginOfCalendar());
		String content = createFileContent(calendar);
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
	
	
	private static String createFileContent(CalendarInt calendar) {
		return "complete export coming soon";
	}
	
	
	private static void writeToFile(String fileName, String content) {
		try {
			FileWriter writer = new FileWriter(fileName);
			writer.write(content);
			writer.close();
			//TODO log
		}
		catch (IOException e) {
			//TODO log
			e.printStackTrace();
		}
	}
}


















