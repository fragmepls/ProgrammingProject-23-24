
public interface Export {

	/**
	 * Exports the calendar as a CSV file
	 * @param calendar
	 */
	public static void CSVExport(CalendarInt calendar){
		CSVExport.simpleExport(calendar);
	}
}
