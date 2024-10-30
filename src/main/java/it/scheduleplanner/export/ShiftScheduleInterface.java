package it.scheduleplanner.export;

import java.time.LocalDate;
import java.util.Map;

/**
 * Object that represents a shift-schedule and stores FixedShiftDays mapped to their dates.<br>
 * It may be used to create a schedule exportable through some method specified in it.scheduleplanner.export.Export
 */
public interface ShiftScheduleInterface {

	/**
	 * This method may be used to add a Day to the schedule mapped to its Date.
	 * @param date Date of the day
	 * @param day Day to be added
	 */
	public void addDay(LocalDate date, ShiftDayInterface day);

	/**
	 * 
	 * @return LocalDate beginOfSchedule = first day of the schedule
	 */
	public LocalDate getBeginOfSchedule();

	/**
	 * Returns the schedula as a HashMap.
	 * @return the Schedule as Map<LocalDate, Day> HashMap
	 */
	public Map<LocalDate, ShiftDayInterface> getSchedule();

	/**
	 * this method returns the day mapped to the date in the input.
	 * @param date Date of desired day
	 * @return day mapped to the date
	 */
	ShiftDayInterface getDay(LocalDate date);

	String toString();
}