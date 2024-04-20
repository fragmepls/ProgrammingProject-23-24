// import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.*;

public class MainTest {

	public static void main(String[] args) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, yyyy-MM-dd");
		LocalDate date = LocalDate.now();
		String s = date.toString();
		FixedShiftDay day = new FixedShiftDay();
		Employee empl1 = new EmployeeImpl("Peter");
		Employee empl2 = new EmployeeImpl("Isaiah");
		day.addEmployee(empl1);
		day.addEmployee(empl2, Shift.AFTERNOON);
		FixedShiftsSchedule calendar = new FixedShiftsSchedule(date);
		calendar.addDay(date, day);
		Export.CSVExport(calendar, "/home/isaiah/Desktop/");
//		System.out.println(date.getDayOfWeek());
//		switch(date.getDayOfWeek().toString()) {
//		case "MONDAY":
//			System.out.println(true);
//			break;
//		default:
//			System.out.println(false);
//		}
//		LocalDate date = LocalDate.of(2024, 4, 2);
//		date = date.plusDays(1);
//		System.out.println(date.toString());
	}

}
