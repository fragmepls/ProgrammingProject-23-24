package it.scheduleplanner.utils;

import it.scheduleplanner.planner.ScheduleCreator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Employee implements EmployeeInterface {

    private String name;
    private int overTimeHours = 0; //starts always from 0 - not in constructor needed
    private boolean weekendWorker;
    private DayOfWeek freeDay;
    private int workingHours;
    private boolean fullTimeWorker;
    private List<Vacation> vacationList = new ArrayList<>();;



    //constructor for Employee - overTimeHours does not need to get taken since every employee starts at 0
    public Employee(String name, boolean weekendWorker, String freeDay, boolean fullTimeWorker) {
        this.name = name;
        this.freeDay = DayOfWeek.valueOf(freeDay.toUpperCase());
        this.weekendWorker = weekendWorker;
        this.fullTimeWorker = fullTimeWorker;
        ScheduleCreator.addEmployee(this);
    }


    // Method to add a vacation
    public void addVacation(Vacation vacation) {
        vacationList.add(vacation);
    }

    // Method to check if the employee is on vacation on a specific date
    public boolean isOnVacation(LocalDate date) {
        for (Vacation vacation : vacationList) {
            if (vacation.isOnVacation(date)) {
                return true;
            }
        }
        return false;
    }

    //all getters and setters here
    public String getName() {
        return name;
    }

    public boolean isFullTimeWorker() {
        return fullTimeWorker;
    }

    public void setFullTimeWorker(boolean fullTimeWorker) {
        this.fullTimeWorker = fullTimeWorker;
    }

    public void setFreeDay(DayOfWeek freeDay) {
        this.freeDay = freeDay;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOverTimeHours() {
        return overTimeHours;
    }

    public void setOverTimeHours(int overTimeHours) {
        this.overTimeHours = overTimeHours;
    }

    public boolean isWeekendWorker() {
        return weekendWorker;
    }

    public void setWeekendWorker(boolean weekendWorker) {
        this.weekendWorker = weekendWorker;
    }

    public DayOfWeek getFreeDay() {
        return freeDay;
    }

    public void setFreeDay(String freeDay) {
        this.freeDay = DayOfWeek.valueOf(freeDay);
    }

    public int getWorkingHours() {
            return workingHours;

    }

    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }


    @Override
    //method that adds all hours that go beyond the 20/40 weekly hours
    public void addOverTimeHours(int hours) {
        this.overTimeHours += hours;
    }

    //all methods needed for vacation


}
