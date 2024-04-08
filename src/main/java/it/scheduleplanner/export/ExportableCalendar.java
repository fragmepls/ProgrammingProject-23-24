package it.scheduleplanner.export;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;


public class ExportableCalendar implements CalendarInt{

	private LocalDate beginOfCalendar;
	Map<LocalDate, Day> calendar = new HashMap<LocalDate, Day>();
	
	/**
	 * Constructs a new exportable calendar with the Days mapped to the Dates
	 * @param beginOfCalendar java Date
	 */
	public ExportableCalendar(LocalDate beginOfCalendar) {
		this.beginOfCalendar = beginOfCalendar;
	}
	
	/**
	 * 
	 * @param date
	 * @param workDay
	 */
	public void addDay(LocalDate date, Day workDay) {
		calendar.put(date, workDay);
	}

	/**
	 * 
	 * @return java Date begin of the Calendar
	 */
	public LocalDate getBeginOfCalendar() {
		return beginOfCalendar;
	}

	/**
	 * 
	 * @return the Calendar as Map<Date, Day> HashMap
	 */
	public Map<LocalDate, Day> getCalendar() {
		return calendar;
	}
	
	
}
