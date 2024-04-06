import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class CSVExport implements Export{
	
	public static void simpleExport(CalendarInt calendar){
		createFile(calendar.getBeginOfCalendar());
		
	}
	
	private static void createFile(LocalDate date) {
		String dateS = date.toString();
		String fileName = dateS + ".csv";
		int i = 1;
		try {
		    while (true) {
		    	File newFile = new File(fileName);
		    	if (newFile.createNewFile()) {
		    		// TODO log
		    		break;
			    } 
			    else {
			    	fileName = "(" + i + ")" + dateS + ".csv";
			    	i++;
				}
			}
		} 
		catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
