package it.scheduleplanner.export;

public interface Export {

	/**
	 * Exports the calendar as a CSV file
	 * @param calendar
	 */
	public static void CSVExport(ShiftSchedule schedule){
		CSVExport.simpleScheduleExport(schedule);
	}
}
