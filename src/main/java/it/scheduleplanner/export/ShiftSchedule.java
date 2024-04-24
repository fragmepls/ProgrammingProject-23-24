package it.scheduleplanner.export;

import java.time.LocalDate;
import java.util.Map;

public interface ShiftSchedule {

	/**
	 * 
	 * @param date
	 * @param workDay
	 */
	public void addDay(LocalDate date, ShiftDayInterface workDay);

	/**
	 * 
	 * @return java Date begin of the Schedule
	 */
	public LocalDate getBeginOfSchedule();

	/**
	 * 
	 * @return the Schedule as Map<Date, Day> HashMap
	 */
	public Map<LocalDate, ShiftDayInterface> getSchedule();

}
