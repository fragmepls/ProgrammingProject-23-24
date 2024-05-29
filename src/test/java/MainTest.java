
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.*;

public class MainTest {

	public static void main(String[] args) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, yyyy-MM-dd");
		LocalDate date = LocalDate.now();
		String s = date.toString();
		List<EmployeeInterface> employees= new ArrayList<>();
		
		ShiftDayInterface day = new FixedShiftDay();
		ShiftDayInterface day2 = new FixedShiftDay();
		EmployeeInterface empl1 = new Employee("empl1", false, "MONDAY", true);
		employees.add(empl1);
		EmployeeInterface empl2 = new Employee("empl2", false, "MONDAY", true);
		employees.add(empl2);
		EmployeeInterface empl3 = new Employee("empl3", false, "MONDAY", true);
		employees.add(empl3);
		EmployeeInterface empl4 = new Employee("empl4", false, "MONDAY", true);
		employees.add(empl4);
		EmployeeInterface empl5 = new Employee("empl5", false, "MONDAY", true);
		employees.add(empl5);
		EmployeeInterface empl6 = new Employee("empl6", false, "MONDAY", true);
		//employees.add(empl6);
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
		Export.employeeExport(employees, "/home/isaiah/Desktop/");
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
		
//		for (int i = 0; i <10 ;i++){
//			if (i%2==0) {
//				continue;
//			}
//			System.out.println(i);
//		}
//		Map<Integer, Boolean> export = new HashMap<>();
		
		
	
	}
	
	

}
