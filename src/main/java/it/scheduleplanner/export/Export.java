package it.scheduleplanner.export;

/**
 * This Interface handles the export of data and provides the necessary methods.<br>
 * All possible actions are indicated by the methods.
 */
public interface Export {

	/**
	 * Exports the schedule to a CSV file stored in the directory indicated by the path.
	 * 
	 * @param ShiftScheduleInterface schedule
	 * @param String pathToDirectory
	 * <br><br>
	 * @return false if schedule is not exportable
	 * <li>true if the schedule is exportable
	 */
	public static Boolean CSVExport(ShiftScheduleInterface schedule, String pathToDirectory){
		if (schedule.getSchedule().keySet().size() <= 0) {
			return false;
		}
		CSVExport.simpleScheduleExport(schedule, pathToDirectory);
		return true;
	}
}
