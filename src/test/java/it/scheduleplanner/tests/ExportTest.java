package it.scheduleplanner.tests;

import it.scheduleplanner.export.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import java.util.regex.*;


public class ExportTest {
	
	private static String str = "/home/isaiah/Desktop/(2)2024-06-01.csv";
	private static final DateTimeFormatter FORMATTER_ddMMyyyy = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	
	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");
		Matcher matcher = pattern.matcher(";KW Nr.;Date:;01.06.2024;Date:;02.06.2024;Date:;03.06.2024;Date:;04.06.2024;Date:;05.06.2024;Date:;06.06.2024;Date:;07.06.2024\n");

		List<LocalDate> datesList = new ArrayList<LocalDate>();
		
		matcher.results().toList().forEach((date) -> datesList.add(LocalDate.parse(date.toString(), FORMATTER_ddMMyyyy)));
		
		System.out.println(datesList.toString());
		
		
		
		Map<Boolean, String> map = new HashMap<>();
		map.put(true, "Hello");
		map.replace(true, map.get(true) + " World");
		List<LocalDate> list = new ArrayList<>();
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			int j = random.nextInt(29) + 1;
			list.add(LocalDate.of(2024, Month.APRIL, j));
		}
		sortList(list);
		//Collections.sort(list);
		//System.out.println(map.get(true));
		
		
		//Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}.csv");
		//Matcher matcher = pattern.matcher(str);
		matcher.find();
	//	LocalDate date = LocalDate.parse(matcher.group().split("\\.")[0]);
		LocalDate date = LocalDate.parse("01.11.2003", FORMATTER_ddMMyyyy);
		System.out.println(date.toString());
	}
	
	private static void sortList(List<LocalDate> listToSort) {
		Collections.sort(listToSort);
	}
}
