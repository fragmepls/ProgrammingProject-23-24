package it.scheduleplanner.export;

public interface Export {

	/**
	 * Exports the calendar as a CSV file
	 * @param calendar
	 */
	public static void CSVExport(ShiftSchedule calendar, String pathToDirectory){
		CSVExport.simpleScheduleExport(calendar, pathToDirectory);
	}
}
