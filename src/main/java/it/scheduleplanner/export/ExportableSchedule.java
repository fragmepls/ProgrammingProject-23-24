package it.scheduleplanner.export;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;


public class ExportableSchedule implements ShiftSchedule{

	private LocalDate beginOfSchedule;
	private Map<LocalDate, FixedShiftDay> scheduleFixedShifts = new HashMap<LocalDate, FixedShiftDay>();
	
	/**
	 * Constructs a new exportable schedule with the Days mapped to the Dates
	 * @param beginOfSchedule java Date
	 */
	public ExportableSchedule(LocalDate beginOfSchedule) {
		this.beginOfSchedule = beginOfSchedule;
	}
	
	/**
	 * 
	 * @param date
	 * @param workDay
	 */
	public void addFixedShiftDay(LocalDate date, FixedShiftDay workDay) {
		scheduleFixedShifts.put(date, workDay);
	}

	/**
	 * 
	 * @return java Date begin of the Schedule
	 */
	public LocalDate getBeginOfSchedule() {
		return beginOfSchedule;
	}

	/**
	 * 
	 * @return the Schedule as Map<Date, Day> HashMap
	 */
	public Map<LocalDate, FixedShiftDay> getFixedShiftSchedule() {
		return scheduleFixedShifts;
	}
	
	
}
