package it.scheduleplanner.tests;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.Employee;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.regex.*;
import java.util.stream.Stream;
import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Tests {
	private static Set<Employee> employeeSet = Set.of(
			new Employee("empl1", false, "sunday", false), 
			new Employee("empl2", false, "sunday", false), 
			new Employee("empl3", false, "sunday", false), 
			new Employee("empl4", false, "sunday", false), 
			new Employee("empl5", false, "sunday", false));
	
	private static final File path = new File("src/test/resources/export");
	private static final String PATH_PREFIX = path.getAbsolutePath();
	private static final String CSV_SUFFIX = ".csv";
	
	private static final LocalDate BEGIN = LocalDate.of(2024, 8, 14);
	private static final LocalDate END = LocalDate.of(2024, 8, 22);
	
	private static final DateTimeFormatter FORMATTER_ddMMyyyy = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	
	//Strings comparison files
	private static final String BLANK_SCHEDULE = ";;Monday;;Tuesday;;Wednesday;;Thursday;;Friday;;Saturday;;Sunday;\n"
			+ ";Week Nr. 33;Date:;12.08.2024;Date:;13.08.2024;Date:;14.08.2024;Date:;15.08.2024;Date:;16.08.2024;Date:;17.08.2024;Date:;18.08.2024\n"
			+ "Name;ID;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon\n"
			+ "empl1;1\n"
			+ "empl2;2\n"
			+ "empl3;3\n"
			+ "empl4;4\n"
			+ "empl5;5\n"
			+ ";\n"
			+ ";Week Nr. 34;Date:;19.08.2024;Date:;20.08.2024;Date:;21.08.2024;Date:;22.08.2024;Date:;23.08.2024;Date:;24.08.2024;Date:;25.08.2024\n"
			+ "Name;ID;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon\n"
			+ "empl1;1\n"
			+ "empl2;2\n"
			+ "empl3;3\n"
			+ "empl4;4\n"
			+ "empl5;5\n"
			+ ";";
	
	public static void main(String[] args) {
		Export.CSVExport(createTestSchedule(), employeeSet, PATH_PREFIX);
	}

	private static boolean writeToFile(List<String> fileContentList, String pathToFile) {
		try{
			Files.write(Path.of(pathToFile), fileContentList);
			System.out.println("write to file: " + pathToFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
		return true;
	}
	
	private static ShiftScheduleInterface createTestSchedule() {
		ShiftScheduleInterface schedule = new FixedShiftsSchedule(BEGIN);
		int i = 0;
		for (LocalDate date = BEGIN; !date.isAfter(END); date = date.plusDays(1), i++) {
			if (!isWeekend(date)) {
				ShiftDayInterface day = addEmployees(i);
				if (day != null) {
					schedule.addDay(date, day);
				}
			}
		}
		System.out.println("Problem?" + schedule.getSchedule().get(LocalDate.of(2024, 8, 17)));
		return schedule;
	}
	
	private static boolean isWeekend(LocalDate date) {
		switch (date.getDayOfWeek().toString()) {
			case "SATURDAY":
				return true;
				
			case "SUNDAY":
				return true;
				
			default:
				return false;
		}
	}
	
	private static ShiftDayInterface addEmployees(int i) {
		ShiftDayInterface day = new FixedShiftDay();
		List<Employee> employeeList = new ArrayList<>(employeeSet);
		Collections.sort(employeeList, (e1, e2) -> e1.getEmployeeId() - e2.getEmployeeId());
		
		switch (i) {
		case 0:
			day.addEmployee(employeeList.get(0), Shift.FULL);
			day.addEmployee(employeeList.get(1), Shift.MORNING);
			day.addEmployee(employeeList.get(2), Shift.MORNING);
			day.addEmployee(employeeList.get(3), Shift.AFTERNOON);
			day.addEmployee(employeeList.get(4), Shift.FULL);
			return day;
			
		case 1:
			day.addEmployee(employeeList.get(0), Shift.FULL);
			day.addEmployee(employeeList.get(1), Shift.MORNING);
			day.addEmployee(employeeList.get(2), Shift.MORNING);
			day.addEmployee(employeeList.get(3), Shift.AFTERNOON);
			day.addEmployee(employeeList.get(4), Shift.FULL);
			return day;
			
		case 2:
			day.addEmployee(employeeList.get(0), Shift.FULL);
			day.addEmployee(employeeList.get(1), Shift.MORNING);
			day.addEmployee(employeeList.get(2), Shift.FULL);
			day.addEmployee(employeeList.get(3), Shift.FULL);
			return day;
			
		case 5:
			day.addEmployee(employeeList.get(0), Shift.FULL);
			day.addEmployee(employeeList.get(1), Shift.MORNING);
			day.addEmployee(employeeList.get(2), Shift.MORNING);
			day.addEmployee(employeeList.get(3), Shift.AFTERNOON);
			day.addEmployee(employeeList.get(4), Shift.FULL);
			return day;
			
		case 6:
			day.addEmployee(employeeList.get(0), Shift.FULL);
			day.addEmployee(employeeList.get(1), Shift.MORNING);
			day.addEmployee(employeeList.get(2), Shift.FULL);
			day.addEmployee(employeeList.get(4), Shift.FULL);
			return day;
			
		case 7:
			day.addEmployee(employeeList.get(0), Shift.FULL);
			day.addEmployee(employeeList.get(1), Shift.MORNING);
			day.addEmployee(employeeList.get(2), Shift.FULL);
			day.addEmployee(employeeList.get(3), Shift.FULL);
			return day;
			
		case 8:
			day.addEmployee(employeeList.get(0), Shift.FULL);
			day.addEmployee(employeeList.get(1), Shift.MORNING);
			day.addEmployee(employeeList.get(2), Shift.MORNING);
			day.addEmployee(employeeList.get(3), Shift.AFTERNOON);
			day.addEmployee(employeeList.get(4), Shift.FULL);
			return day;
		}
		return null;
	}
	
}
