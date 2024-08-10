package it.scheduleplanner.tests;

import it.scheduleplanner.export.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import java.util.regex.*;


public class ExportTest {
	
	private static String str = "/home/isaiah/Desktop/(2)2024-06-01.csv";
	
	
	public static void main(String[] args) {
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
		
		
		Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}.csv");
		Matcher matcher = pattern.matcher(str);
		matcher.find();
		System.out.println(matcher.group().split("\\.")[0].split("-"));
	}
	
	private static void sortList(List<LocalDate> listToSort) {
		Collections.sort(listToSort);
	}
}
