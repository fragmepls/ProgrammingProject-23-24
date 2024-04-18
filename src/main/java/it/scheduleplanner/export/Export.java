package it.scheduleplanner.export;

public interface Export {

	/**
	 * Exports the calendar as a CSV file
	 * @param calendar
	 */
	public static Boolean CSVExport(FixedShiftsSchedule schedule, String pathToDirectory){
		if (schedule.getSchedule().keySet().size() <= 0) {
			return false;
		}
		CSVExport.simpleScheduleExport(schedule, pathToDirectory);
		return true;
	}
}
