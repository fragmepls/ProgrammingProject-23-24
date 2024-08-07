package it.scheduleplanner.export;

import java.time.LocalDate;
import java.util.Map;

public final class Import {

	public static ShiftScheduleInterface importSchedule() {
		
		
		return new FixedShiftsSchedule(null);
	}
}
