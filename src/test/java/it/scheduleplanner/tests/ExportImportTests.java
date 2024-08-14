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

public class ExportImportTests {
	private static Set<Employee> eployeeSet = Set.of(
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
	
	@BeforeAll
	private static void checkForComparisonFiles() {
		if (!Files.exists(Path.of(PATH_PREFIX + "/blankSchedule" + CSV_SUFFIX))) {
			writeStringToFile(BLANK_SCHEDULE, PATH_PREFIX + "/blankSchedule" + CSV_SUFFIX);
		}
	}
	
	
	
	@Test
	void testBlankScheduleExport() {
		System.out.println(PATH_PREFIX);
		String exportedFile = Export.exportBlankSchedule(BEGIN, END, eployeeSet, PATH_PREFIX).get(true);
		assertTrue(filesAreEqual(PATH_PREFIX + "/blankSchedule" + CSV_SUFFIX, exportedFile));
	}
	
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

}
