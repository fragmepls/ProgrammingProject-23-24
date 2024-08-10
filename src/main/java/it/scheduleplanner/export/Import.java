package it.scheduleplanner.export;

import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Import {

	
	/*
	 * public methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public static ShiftScheduleInterface importSchedule(String pathToFile) {
		LocalDate scheduleStart = findStartDateInFileName(pathToFile);
		
		return new FixedShiftsSchedule(null);
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
		
		String[] dateNumbers = matcher.group().split("\\.")[0].split("-");
		
		return LocalDate.of(Integer.parseInt(dateNumbers[0]), Integer.parseInt(dateNumbers[1]), Integer.parseInt(dateNumbers[2]));
	}
	
	
	/**
	 * private constructor to suppress instantiation
	 */
	private Import() {
		super();
	}
}
















