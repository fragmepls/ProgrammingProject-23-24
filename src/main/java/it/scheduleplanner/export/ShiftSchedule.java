package it.scheduleplanner.export;
import java.time.LocalDate;
import java.util.Map;

public interface ShiftSchedule {

	/**
	 * 
	 * @param date
	 * @param workDay
	 */
	public void addFixedShiftDay(LocalDate date, FixedShiftDay workDay);
	
	/**
	 * 
	 * @return java LocalDate begin of the Schedule
	 */
	public LocalDate getBeginOfSchedule();
	
	/**
	 * 
	 * @return the Schedule as Map<LocalDate, Day> HashMap
	 */
	public Map<LocalDate, FixedShiftDay> getFixedShiftSchedule();
}
