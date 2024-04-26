package it.scheduleplanner.utils;

public class scheduleStructure {

    //class that defines a frame of the necessaries you need to know for further generation of the schedule

    private int workdays;
    private boolean weekend;
    private double hoursPerDay;
    private int necessaryWorkers;

    public scheduleStructure(int workdays, boolean weekend, double hoursPerDay, int necessaryWorkers) {
        this.workdays = workdays;
        this.weekend = weekend;
        this.hoursPerDay = hoursPerDay;
        this.necessaryWorkers = necessaryWorkers;
    }

    //getters and setters
    public int getNecessaryWorkers() {
        return necessaryWorkers;
    }

    public void setNecessaryWorkers(int necessaryWorkers) {
        this.necessaryWorkers = necessaryWorkers;
    }

    public double getHoursPerDay() {
        return hoursPerDay;
    }

    public void setHoursPerDay(double hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public void setWeekend(boolean weekend) {
        this.weekend = weekend;
    }

    public int getWorkdays() {
        return workdays;
    }

    public void setWorkdays(int workdays) {
        this.workdays = workdays;
    }
}
