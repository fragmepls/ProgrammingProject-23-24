package it.scheduleplanner.tests;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;
import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExportImportTests {
	
	private static final File path = new File("src/test/resources/export");
	private static final String PATH_PREFIX = path.getAbsolutePath();
	private static final String CSV_SUFFIX = ".csv";
	
	private static final LocalDate BEGIN = LocalDate.of(2024, 8, 14);
	private static final LocalDate END = LocalDate.of(2024, 8, 22);
	
	private static Set<Employee> employeeSet = Set.of(
			new Employee("empl1", false, "sunday", false), 
			new Employee("empl2", false, "sunday", false), 
			new Employee("empl3", false, "sunday", false), 
			new Employee("empl4", false, "sunday", false), 
			new Employee("empl5", false, "sunday", false));
	
	private static ShiftScheduleInterface schedule = new FixedShiftsSchedule(BEGIN);
	
	
//	Strings for comparison files
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
	
	private static final String SHIFT_SCHEDULE = ";;Monday;;Tuesday;;Wednesday;;Thursday;;Friday;;Saturday;;Sunday;\n"
			+ ";Week Nr. 33;Date:;12.08.2024;Date:;13.08.2024;Date:;14.08.2024;Date:;15.08.2024;Date:;16.08.2024;Date:;17.08.2024;Date:;18.08.2024\n"
			+ "Name;ID;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon\n"
			+ "empl1;1;;;;;x;x;x;x;x;x;;;;\n"
			+ "empl2;2;;;;;x;;x;;x;;;;;\n"
			+ "empl3;3;;;;;x;;x;;x;x;;;;\n"
			+ "empl4;4;;;;;;x;;x;x;x;;;;\n"
			+ "empl5;5;;;;;x;x;x;x;;;;;;\n"
			+ ";\n"
			+ ";Week Nr. 34;Date:;19.08.2024;Date:;20.08.2024;Date:;21.08.2024;Date:;22.08.2024;Date:;23.08.2024;Date:;24.08.2024;Date:;25.08.2024\n"
			+ "Name;ID;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon\n"
			+ "empl1;1;x;x;x;x;x;x;x;x;;;;;;\n"
			+ "empl2;2;x;;x;;x;;x;;;;;;;\n"
			+ "empl3;3;x;;x;x;x;x;x;;;;;;;\n"
			+ "empl4;4;;x;;;x;x;;x;;;;;;\n"
			+ "empl5;5;x;x;x;x;;;x;x;;;;;;\n"
			+ ";";
	
	private static final String TEST_SCHEDULE = ";;Monday;;Tuesday;;Wednesday;;Thursday;;Friday;;Saturday;;Sunday;\n"
			+ ";Week Nr. 33;Date:;12.08.2024;Date:;13.08.2024;Date:;14.08.2024;Date:;15.08.2024;Date:;16.08.2024;Date:;17.08.2024;Date:;18.08.2024\n"
			+ "Name;ID;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon\n"
			+ "empl1;1;;;;;;;x;x;x;x;;;;\n"
			+ "empl2;2;;;;;;;x;;x;;;;;\n"
			+ "empl3;3;;;;;;;x;;x;x;;;;\n"
			+ "empl4;4;;;;;;;;x;x;x;;;;\n"
			+ "empl5;5;;;;;;;x;x;;;;;;\n"
			+ ";\n"
			+ ";Week Nr. 34;Date:;19.08.2024;Date:;20.08.2024;Date:;21.08.2024;Date:;22.08.2024;Date:;23.08.2024;Date:;24.08.2024;Date:;25.08.2024\n"
			+ "Name;ID;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon;morning;afternoon\n"
			+ "empl1;1;x;x;x;x;x;x;x;x;x;;;;;\n"
			+ "empl2;2;x;;x;;x;;x;;x;;;;;\n"
			+ "empl3;3;x;;x;x;x;x;x;;x;;;;;\n"
			+ "empl4;4;;x;;;x;x;;x;x;;;;;\n"
			+ "empl5;5;x;x;x;x;;;x;x;x;;;;;\n"
			+ ";";
	
	private static final String IMPORT_SCHEDULE = ",,Monday,,Tuesday,,Wednesday,,Thursday,,Friday,,Saturday,,Sunday,\n"
			+ ",Week Nr. 33,Date:,12.08.2024,Date:,13.08.2024,Date:,14.08.2024,Date:,15.08.2024,Date:,16.08.2024,Date:,17.08.2024,Date:,18.08.2024\n"
			+ "Name,ID,morning,afternoon,morning,afternoon,morning,afternoon,morning,afternoon,morning,afternoon,morning,afternoon,morning,afternoon\n"
			+ "empl1,1,,,,,x,x,x,x,x,x,,,,\n"
			+ "empl2,2,,,,,x,,x,,x,,,,,\n"
			+ "empl3,3,,,,,x,,x,,x,x,,,,\n"
			+ "empl4,4,,,,,,x,,x,x,x,,,,\n"
			+ "empl5,5,,,,,x,x,x,x,,,,,,\n"
			+ ",,,,,,,,,,,,,,,\n"
			+ ",Week Nr. 34,Date:,19.08.2024,Date:,20.08.2024,Date:,21.08.2024,Date:,22.08.2024,Date:,23.08.2024,Date:,24.08.2024,Date:,25.08.2024\n"
			+ "Name,ID,morning,afternoon,morning,afternoon,morning,afternoon,morning,afternoon,morning,afternoon,morning,afternoon,morning,afternoon\n"
			+ "empl1,1,x,x,x,x,x,x,x,x,,,,,,\n"
			+ "empl2,2,x,,x,,x,,x,,,,,,,\n"
			+ "empl3,3,x,,x,x,x,x,x,,,,,,,\n"
			+ "empl4,4,,x,,,x,x,,x,,,,,,\n"
			+ "empl5,5,x,x,x,x,,,x,x,,,,,,\n"
			+ "";
	
	
	
	
	/*
	 * Initialization
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	@BeforeAll
	private static void checkForComparisonFiles() {
//		initialize blank schedule for comparison
		writeStringToFile(BLANK_SCHEDULE, PATH_PREFIX + "/blankSchedule" + CSV_SUFFIX);
		
//		initialize shift schedule for comparison
		writeStringToFile(SHIFT_SCHEDULE, PATH_PREFIX + "/shiftSchedule" + CSV_SUFFIX);
		
//		initialize csv file of shift schedule for import test
		writeStringToFile(IMPORT_SCHEDULE, PATH_PREFIX + "/2024-08-14" + CSV_SUFFIX);
		
//		initialize csv file of shift schedule for import test
		writeStringToFile(TEST_SCHEDULE, PATH_PREFIX + "/2024-08-15" + CSV_SUFFIX);
		
//		create schedule for tests
		int i = 0;
		for (LocalDate date = BEGIN; !date.isAfter(END); date = date.plusDays(1), i++) {
			if (!isWeekend(date)) {
				ShiftDayInterface day = addEmployees(i);
				if (day != null) {
					schedule.addDay(date, day);
				}
			}
		}
	}
	
	/*
	 * Tests
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	/**
	 * Test case for blank schedule export.
	 */
	@Test
	void testBlankScheduleExport() {
		String exportedFile = Export.exportBlankSchedule(BEGIN, END, employeeSet, PATH_PREFIX).get(true);
		assertTrue(exportedFile != null && filesAreEqual(PATH_PREFIX + "/blankSchedule" + CSV_SUFFIX, exportedFile));
	}
	
	/**
	 * Test case to evaluate if CSVExports exports a file in the expected format.
	 */
	@Test
	void testShiftScheduleExport() {
		String exportedFile = Export.CSVExport(schedule, employeeSet, PATH_PREFIX).get(true);
		assertTrue(exportedFile != null && filesAreEqual(PATH_PREFIX + "/shiftSchedule" + CSV_SUFFIX, exportedFile));
	}
	
	/**
	 * Test case for testing if the imported schedule equals the schedule the file represents by using the toString() method.
	 * The imported file represents a file which may have been manipulated by an other application.
	 */
	@Test
	void testShiftScheduleImport() {
		ShiftScheduleInterface importedSchedule = Import.importSchedule(PATH_PREFIX + "/2024-08-14" + CSV_SUFFIX, employeeSet);
		assertEquals(schedule.toString(), importedSchedule.toString());
	}
	
	/**
	 * Test case to test the combination of import and export at the example of an schedule different from the other tests.
	 * Important! Due to the way the similarity of the two files gets tested, this test doesn't represent the import of a file 
	 */
	@Test
	void testImportExportCombined() {
		ShiftScheduleInterface importedSchedule = Import.importSchedule(PATH_PREFIX + "/2024-08-15" + CSV_SUFFIX, employeeSet);
		String exportedFile = Export.CSVExport(importedSchedule, employeeSet, PATH_PREFIX).get(true);
		assertTrue(exportedFile != null && filesAreEqual(PATH_PREFIX + "/2024-08-15" + CSV_SUFFIX, exportedFile));
	}
	
	/**
	 * Test case for testing if employeeExport functions without any errors.
	 */
	@Test
	void testEmployeeExport() {
		assertTrue(Export.employeeExport(employeeSet, PATH_PREFIX));
	}
	
	/*
	 * supporting methods
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private static boolean filesAreEqual(String pathToFile1, String pathToFile2) {
		List<String> textFile1 = null;
		List<String> textFile2 = null;
		
		try (Stream<String> stream = Files.lines(Path.of(pathToFile1))){
			textFile1 = stream.toList();
		} catch (IOException e) {
		    e.printStackTrace();
		    return false;
		}
		try (Stream<String> stream = Files.lines(Path.of(pathToFile2))){
			textFile2 = stream.toList();
		} catch (IOException e) {
		    e.printStackTrace();
		    return false;
		}
		
		return textFile1.equals(textFile2);
	}
	
	private static boolean writeStringToFile(String string, String path) {
		try {
		    Files.writeString(
		          Path.of(path),
		          string
		    );
		} catch (IOException e) {
		    e.printStackTrace();
		    return false;
		}
		return true;
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
