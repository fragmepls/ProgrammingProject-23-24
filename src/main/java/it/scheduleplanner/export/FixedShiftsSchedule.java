package it.scheduleplanner.export;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;


public class FixedShiftsSchedule{

	private LocalDate beginOfSchedule;
	private Map<LocalDate, FixedShiftDay> schedule = new HashMap<LocalDate, FixedShiftDay>();
	
	/**
	 * Constructs a new exportable schedule with the Days mapped to the Dates
	 * @param beginOfSchedule java Date
	 */
	public FixedShiftsSchedule(LocalDate beginOfSchedule) {
		this.beginOfSchedule = beginOfSchedule;
	}
	
	/**
	 * 
	 * @param date
	 * @param workDay
	 */
	public void addDay(LocalDate date, FixedShiftDay workDay) {
		schedule.put(date, workDay);
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
	public Map<LocalDate, FixedShiftDay> getSchedule() {
		return schedule;
	}
	
	
}
