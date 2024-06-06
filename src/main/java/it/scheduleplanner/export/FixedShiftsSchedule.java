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
	private Map<LocalDate, ShiftDayInterface> schedule = new HashMap<>();

	/**
	 * Constructs a new exportable schedule with the Days mapped to the Dates
	 * @param beginOfSchedule beginOfSchedule = first day of the schedule
	 */
	public FixedShiftsSchedule(LocalDate beginOfSchedule) {
		this.beginOfSchedule = beginOfSchedule;
	}

	@Override
	public void addDay(LocalDate date, ShiftDayInterface day) {
		schedule.put(date, day);
	}

	@Override
	public LocalDate getBeginOfSchedule() {
		return beginOfSchedule;
	}

	@Override
	public Map<LocalDate, ShiftDayInterface> getSchedule() {
		return schedule;
	}

	@Override
	public ShiftDayInterface getDay(LocalDate date) {
		return schedule.get(date);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Schedule starting from: ").append(beginOfSchedule.toString()).append("\n");
		for (Map.Entry<LocalDate, ShiftDayInterface> entry : schedule.entrySet()) {
			sb.append(entry.getKey().toString()).append(": ").append(entry.getValue().toString()).append("\n");
		}
		return sb.toString();
	}
}
