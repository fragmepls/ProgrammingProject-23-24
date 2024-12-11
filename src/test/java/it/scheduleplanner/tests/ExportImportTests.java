package it.scheduleplanner.tests;

import it.scheduleplanner.export.*;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.planner.ScheduleCreator;

import java.time.LocalDate;
import java.util.*;
import java.nio.file.*;
import java.io.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

public class ExportImportTests {
	private static final LocalDate BEGIN = LocalDate.of(2024, 8, 14);
	private static final LocalDate END = LocalDate.of(2024, 8, 22);

	@TempDir
	static Path tempDir;

	private Set<Employee> employeeSet;
	private ShiftScheduleInterface schedule;

	@BeforeEach
	void setUp() {
		// Clear any existing state
		ScheduleCreator.employeeSet.clear();
		ScheduleCreator.employeeSetByID.clear();

		// Create test employees with explicit IDs
		employeeSet = new HashSet<>();
		Employee emp1 = createEmployee("empl1", 1);
		Employee emp2 = createEmployee("empl2", 2);
		Employee emp3 = createEmployee("empl3", 3);
		Employee emp4 = createEmployee("empl4", 4);
		Employee emp5 = createEmployee("empl5", 5);

		employeeSet.addAll(Arrays.asList(emp1, emp2, emp3, emp4, emp5));

		// Create test schedule
		schedule = new FixedShiftsSchedule(BEGIN);
		setupTestSchedule();
	}

	private Employee createEmployee(String name, int id) {
		Employee emp = new Employee(name, false, "SUNDAY", false);
		emp.setEmployeeId(id);
		return emp;
	}

	private void setupTestSchedule() {
		List<Employee> employeeList = new ArrayList<>(employeeSet);

		// Add shifts for each day in the range
		LocalDate date = BEGIN;
		while (!date.isAfter(END)) {
			ShiftDayInterface day = new FixedShiftDay();
			addTestShifts(day, employeeList);
			schedule.addDay(date, day);
			date = date.plusDays(1);
		}

		// Debug print schedule
		System.out.println("Original schedule:");
		schedule.getSchedule().forEach((d, day) -> {
			System.out.println("Date: " + d);
			day.getEmployees().forEach((emp, shift) ->
					System.out.println("  " + emp.getName() + ": " + shift));
		});
	}

	private void addTestShifts(ShiftDayInterface day, List<Employee> employees) {
		// Ensure consistent shift assignment
		day.addEmployee(employees.get(0), Shift.FULL);
		day.addEmployee(employees.get(1), Shift.MORNING);
		day.addEmployee(employees.get(2), Shift.MORNING);
		day.addEmployee(employees.get(3), Shift.AFTERNOON);
		day.addEmployee(employees.get(4), Shift.FULL);
	}

	@Test
	void testShiftScheduleImport() throws IOException {
		// First export the schedule
		String exportPath = tempDir.resolve("2024-08-14.csv").toString();
		Map<Boolean, String> exportResult = Export.CSVExport(schedule, employeeSet, tempDir.toString());
		assertTrue(exportResult.containsKey(true), "Export should succeed");
		assertTrue(Files.exists(Paths.get(exportResult.get(true))), "Export file should exist");

		// Debug print exported content
		System.out.println("Exported file content:");
		System.out.println(Files.readString(Paths.get(exportResult.get(true))));

		// Then import it
		ShiftScheduleInterface importedSchedule = Import.importSchedule(exportResult.get(true), employeeSet);
		assertNotNull(importedSchedule, "Imported schedule should not be null");

		// Debug print imported schedule
		System.out.println("\nImported schedule:");
		importedSchedule.getSchedule().forEach((date, day) -> {
			System.out.println("Date: " + date);
			day.getEmployees().forEach((emp, shift) ->
					System.out.println("  " + emp.getName() + ": " + shift));
		});

		// Compare schedules
		assertEquals(schedule.getBeginOfSchedule(), importedSchedule.getBeginOfSchedule(),
				"Begin dates should match");

		// Compare each day's assignments
		schedule.getSchedule().forEach((date, originalDay) -> {
			ShiftDayInterface importedDay = importedSchedule.getDay(date);
			assertNotNull(importedDay, "Day should exist in imported schedule: " + date);

			Map<Employee, Shift> originalAssignments = originalDay.getEmployees();
			Map<Employee, Shift> importedAssignments = importedDay.getEmployees();

			assertEquals(originalAssignments.size(), importedAssignments.size(),
					"Number of assignments should match for date: " + date);

			originalAssignments.forEach((employee, shift) -> {
				assertTrue(importedAssignments.containsKey(employee),
						"Employee should exist in imported schedule: " + employee.getName());
				assertEquals(shift, importedAssignments.get(employee),
						"Shift should match for employee: " + employee.getName());
			});
		});
	}

	@Test
	void testBlankScheduleExport() {
		Map<Boolean, String> result = Export.exportBlankSchedule(BEGIN, END, employeeSet, tempDir.toString());

		assertTrue(result.containsKey(true) && result.get(true) != null,
				"Export should succeed and return file path");
		assertTrue(Files.exists(Paths.get(result.get(true))),
				"Export file should exist at: " + result.get(true));
	}

	@Test
	void testShiftScheduleExport() {
		Map<Boolean, String> result = Export.CSVExport(schedule, employeeSet, tempDir.toString());

		assertTrue(result.containsKey(true) && result.get(true) != null,
				"Export should succeed and return file path");
		assertTrue(Files.exists(Paths.get(result.get(true))),
				"Export file should exist at: " + result.get(true));
	}

	@Test
	void testImportExportCombined() throws IOException {
		// Export schedule
		String exportPath = tempDir.resolve("2024-08-14.csv").toString();
		Map<Boolean, String> exportResult = Export.CSVExport(schedule, employeeSet, tempDir.toString());
		assertTrue(exportResult.containsKey(true), "Export should succeed");
		String exportedContent = Files.readString(Paths.get(exportResult.get(true)));

		// Import and re-export
		ShiftScheduleInterface importedSchedule = Import.importSchedule(exportResult.get(true), employeeSet);
		assertNotNull(importedSchedule, "Imported schedule should not be null");

		Map<Boolean, String> reExportResult = Export.CSVExport(importedSchedule, employeeSet, tempDir.toString());
		assertTrue(reExportResult.containsKey(true), "Re-export should succeed");

		String reExportedContent = Files.readString(Paths.get(reExportResult.get(true)));
		assertEquals(exportedContent.trim(), reExportedContent.trim(),
				"Export content should match after round trip");
	}
}