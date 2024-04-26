package it.scheduleplanner.utils;

public class Employee implements EmployeeInterface {
	
	private String name;
	private double overTimeHours = 0.0; //starts always from 0 - not in constructor needed
	private boolean weekendWorker;
	private String freeDay;
	private int workingHours;

	//constructor for Employee - overTimeHours does not need to get taken since every employee starts at 0.0
	public Employee(String name, boolean weekendWorker, String freeDay, int workingHours) {
		this.name = name;
		this.weekendWorker = weekendWorker;
		this.freeDay = freeDay;
		this.workingHours = ((workingHours/100)*40);
	}

	//all getters and setters here
	public String getName() {
		return name;

	}
	public void setName(String name) {
		this.name = name;
	}

	public double getOverTimeHours() {
		return overTimeHours;
	}

	public void setOverTimeHours(double overTimeHours) {
		this.overTimeHours = overTimeHours;
	}

	public boolean isWeekendWorker() {
		return weekendWorker;
	}

	public void setWeekendWorker(boolean weekendWorker) {
		this.weekendWorker = weekendWorker;
	}

	public String getFreeDay() {
		return freeDay;
	}

	public void setFreeDay(String freeDay) {
		this.freeDay = freeDay;
	}

	public int getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(int workingHours) {
		this.workingHours = workingHours;
	}


	@Override
	//method that adds all hours that go beyond the 20/40 weekly hours
	public int overTimeCounter() {
        /* ToDo */
		return 0;
	}
}
