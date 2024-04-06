import java.time.LocalDate;
import java.util.Map;

public interface CalendarInt {

	/**
	 * 
	 * @param date
	 * @param workDay
	 */
	public void addDay(LocalDate date, Day workDay);
	
	/**
	 * 
	 * @return java Date begin of the Calendar
	 */
	public LocalDate getBeginOfCalendar();
	
	/**
	 * 
	 * @return the Calendar as Map<Date, Day> HashMap
	 */
	public Map<LocalDate, Day> getCalendar();
}
