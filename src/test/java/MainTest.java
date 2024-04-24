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
		ShiftDayInterface day = new FixedShiftDay();
		ShiftDayInterface day2 = new FixedShiftDay();
		Employee empl1 = new EmployeeImpl("empl1");
		Employee empl2 = new EmployeeImpl("empl2");
		Employee empl3 = new EmployeeImpl("empl3");
		Employee empl4 = new EmployeeImpl("empl4");
		Employee empl5 = new EmployeeImpl("empl5");
		Employee empl6 = new EmployeeImpl("empl6");
		day.addEmployee(empl1);
		day.addEmployee(empl2, Shift.AFTERNOON);
		day.addEmployee(empl3);
		day2.addEmployee(empl4, Shift.MORNING);
		day2.addEmployee(empl5, Shift.MORNING);
		day.addEmployee(empl6);
		FixedShiftsSchedule calendar = new FixedShiftsSchedule(date.minusDays(10));
		calendar.addDay(date, day);
		calendar.addDay(date.plusDays(2), day2);
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
