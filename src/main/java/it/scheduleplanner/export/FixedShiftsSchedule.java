package it.scheduleplanner.export;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

public class FixedShiftsSchedule implements ShiftScheduleInterface {

	private LocalDate beginOfSchedule;
	private Map<LocalDate, ShiftDayInterface> schedule = new HashMap<LocalDate, ShiftDayInterface>();
	
	/**
	 * Constructs a new exportable schedule with the Days mapped to the Dates
	 * @param beginOfSchedule java Date
	 */
	public FixedShiftsSchedule(LocalDate beginOfSchedule) {
		this.beginOfSchedule = beginOfSchedule;
	}
	
	public void addDay(LocalDate date, ShiftDayInterface workDay) {
		schedule.put(date, workDay);
	}

	public LocalDate getBeginOfSchedule() {
		return beginOfSchedule;
	}

	public Map<LocalDate, ShiftDayInterface> getSchedule() {
		return schedule;
	}
	
	
}
