// import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import it.scheduleplanner.export.*;

public class MainTest {

	public static void main(String[] args) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, yyyy-MM-dd");
		LocalDate date = LocalDate.now();
		String s = date.toString();
		FixedShiftDay day = new FixedShiftDay();
		day.addEmployee(new Employee("Peter"));
		day.addEmployee(new Employee("Isaiah"), Shift.AFTERNOON);
		FixedShiftsSchedule calendar = new FixedShiftsSchedule(date);
		calendar.addDay(date, day);
		Export.CSVExport(calendar, "/home/manjaro/Desktop/");
		System.out.println(date.getDayOfWeek());
		switch(date.getDayOfWeek().toString()) {
		case "MONDAY":
			System.out.println(true);
			break;
		default:
			System.out.println(false);
		}
//		LocalDate date = LocalDate.of(2024, 4, 2);
//		date = date.plusDays(1);
//		System.out.println(date.toString());
	}

}
