
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.*;
import it.scheduleplanner.*;
import it.scheduleplanner.planner.*;

public class MainTest {

	public static void main(String[] args) throws InsufficientEmployeesException {
		/*DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, yyyy-MM-dd");
		LocalDate date = LocalDate.of(2024, 05, 13);
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
		Export.employeeExport(employees, "/home/isaiah/Desktop/");*/
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
		
		
	
		// Initialize employees
        Employee employee1 = new Employee("John", true, "monday", true);
        Employee employee2 = new Employee("Jane", false, "tuesday", false);
        Employee employee3 = new Employee("Jack", true, "wednesday", false);
        Employee employee4 = new Employee("Jill", false, "friday", true);
        Employee employee5 = new Employee("Jake", true, "saturday", false);
        Employee employee6 = new Employee("Jess", true, "monday", true);
        Employee employee7 = new Employee("Jerry", true, "tuesday", true);
        Employee employee8 = new Employee("Janet", false, "monday", false);
        Employee employee9 = new Employee("Jasmine", true, "sunday", true);
        Employee employee10 = new Employee("James", false, "tuesday", false);

        // Set up vacations
        Vacation juneVacation1 = new Vacation(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 10));
        Vacation juneVacation2 = new Vacation(LocalDate.of(2024, 6, 11), LocalDate.of(2024, 6, 20));
        Vacation juneVacation3 = new Vacation(LocalDate.of(2024, 6, 21), LocalDate.of(2024, 6, 30));

        employee1.addVacation(juneVacation1);
        employee2.addVacation(juneVacation2);
        employee3.addVacation(juneVacation1);
        employee4.addVacation(juneVacation3);
        employee5.addVacation(juneVacation1);
        employee6.addVacation(juneVacation2);
        employee7.addVacation(juneVacation2);

        // Add employees to list
        ArrayList<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee1);
        employeeList.add(employee2);
        employeeList.add(employee3);
        employeeList.add(employee4);
        employeeList.add(employee5);
        employeeList.add(employee6);
        employeeList.add(employee7);
        employeeList.add(employee8);
        employeeList.add(employee9);
        employeeList.add(employee10);

        for (Employee e : employeeList) {
        	ScheduleCreator.addEmployee(e);
        }
        // Define the date and the number of employees needed per day
        LocalDate testDate = LocalDate.of(2024, 6, 3); // A Monday
        int numberOfEmployeesPerDay = 1;
        
        LocalDate beginDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 6);
        
        ShiftScheduleInterface calendar = ScheduleCreator.create(beginDate, endDate, numberOfEmployeesPerDay, false, DayOfWeek.SATURDAY);
        System.out.println(calendar.toString());
        Export.CSVExport(calendar, "/home/isaiah/Desktop/");
		Export.employeeExport(ScheduleCreator.employeeSet, "/home/isaiah/Desktop/");
	}
	
	

}
