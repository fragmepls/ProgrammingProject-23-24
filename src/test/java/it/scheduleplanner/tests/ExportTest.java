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

public class ExportTest {
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
		System.out.println(map.get(true));
	}
	
	private static void sortList(List<LocalDate> listToSort) {
		Collections.sort(listToSort);
	}
}
