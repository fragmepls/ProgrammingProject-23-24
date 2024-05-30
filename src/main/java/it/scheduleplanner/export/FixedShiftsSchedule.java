package it.scheduleplanner.export;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

/**
 * Object that represents a shift-schedule and stores FixedShiftDays mapped to their dates.<br>
 * It may be used to create a schedule exportable through some method specified in it.scheduleplanner.export.Export
 */
public class FixedShiftsSchedule implements ShiftScheduleInterface {

	private LocalDate beginOfSchedule;
	private Map<LocalDate, ShiftDayInterface> schedule = new HashMap<LocalDate, ShiftDayInterface>();
	
	/**
	 * Constructs a new exportable schedule with the Days mapped to the Dates
	 * @param LocalDate beginOfSchedule = first day of the schedule
	 */
	public FixedShiftsSchedule(LocalDate beginOfSchedule) {
		this.beginOfSchedule = beginOfSchedule;
	}
	
	public void addDay(LocalDate date, ShiftDayInterface day) {
		schedule.put(date, day);
	}

	public LocalDate getBeginOfSchedule() {
		return beginOfSchedule;
	}

	public Map<LocalDate, ShiftDayInterface> getSchedule() {
		return schedule;
	}

	public ShiftDayInterface getDay(LocalDate date) {
		return schedule.get(date);
	}
}
