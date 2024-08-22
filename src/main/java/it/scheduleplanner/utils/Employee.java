package it.scheduleplanner.utils;

import it.scheduleplanner.planner.ScheduleCreator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an employee with various attributes such as name, overtime hours,
 * weekend working status, free day, working hours, full-time status, and vacation list.
 */
public class Employee {

    private String name;
    private int overTimeHours = 0; // Starts always from 0 - not in constructor needed
    private boolean weekendWorker;
    private DayOfWeek freeDay;
    private int workingHours;
    private boolean fullTimeWorker;
    private List<Vacation> vacationList = new ArrayList<>();
    private int employeeId;

    //    An Employee object (Employee) has a list of Vacation objects (vacationList).
    //    This list (vacationList) keeps track of all the vacations an employee has taken or is scheduled to take.
    //    When an employee plans or takes a vacation, a new Vacation object is created with the start and end dates,
    //    and then added to the employee's vacationList.


    // Adding a Vacation - example:
    // Create a new Vacation object for an employee:
    // Vacation = new Vacation(LocalDate.of(2024, 7, 15), LocalDate.of(2024, 7, 30));
    // employee.addVacation(vacation);

    /**
     * Constructs an Employee object with the specified attributes.
     *
     * @param name           the name of the employee
     * @param weekendWorker  whether the employee works on weekends
     * @param freeDay        the free day of the employee
     * @param fullTimeWorker whether the employee is a full-time worker
     */
    public Employee(String name, boolean weekendWorker, String freeDay, boolean fullTimeWorker) {
        this.name = name;
        this.weekendWorker = weekendWorker;
        this.fullTimeWorker = fullTimeWorker;
        this.freeDay = DayOfWeek.valueOf(freeDay.toUpperCase());
        ScheduleCreator.addEmployee(this);
        employeeId = ScheduleCreator.idCreator();
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * Adds a vacation to the employee's vacation list.
     *
     * @param vacation the vacation to add
     */
    public void addVacation(Vacation vacation) {
        vacationList.add(vacation);
    }


    /**
     * Removes expired vacations from the employee's vacation list based on the current date.
     *
     * @param currentDate the current date to compare with vacation end dates
     */
    public void removeExpiredVacations(LocalDate currentDate) {
        // Create a list to store the vacations to be removed
        List<Vacation> vacationsToRemove = new ArrayList<>();

        // Iterate through the vacationList and identify the expired vacations
        for (Vacation vacation : vacationList) {
            if (vacation.getVacationEnd().isBefore(currentDate)) {
                vacationsToRemove.add(vacation);
            }
        }

        // Remove the expired vacations from the vacationList
        vacationList.removeAll(vacationsToRemove);
    }

    /**
     * Checks if the employee is on vacation on a specific date.
     *
     * @param date the date to check
     * @return true if the employee is on vacation on the specified date, false otherwise
     */
    public boolean isOnVacation(LocalDate date) {
        for (Vacation vacation : vacationList) {
            if (vacation.isOnVacation(date)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the name of the employee.
     *
     * @return the name of the employee
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the overtime hours of the employee.
     *
     * @return the overtime hours of the employee
     */
    public int getOverTimeHours() {
        return overTimeHours;
    }

    /**
     * Returns whether the employee works on weekends.
     *
     * @return true if the employee works on weekends, false otherwise
     */
    public boolean isWeekendWorker() {
        return weekendWorker;
    }

    /**
     * Returns the free day of the employee.
     *
     * @return the free day of the employee
     */
    public DayOfWeek getFreeDay() {
        return freeDay;
    }

    /**
     * Returns the working hours of the employee.
     *
     * @return the working hours of the employee
     */
    public int getWorkingHours() {
        return workingHours;
    }

    /**
     * Sets the working hours of the employee.
     *
     * @param workingHours the working hours to set
     */
    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }

    /**
     * Adds overtime hours to the employee's total overtime hours.
     *
     * @param hours the number of overtime hours to add
     */
    public void addOverTimeHours(int hours) {
        this.overTimeHours += hours;
    }

    /**
     * Returns whether the employee is a full-time worker.
     *
     * @return true if the employee is a full-time worker, false otherwise
     */
    public boolean isFullTimeWorker() {
        return fullTimeWorker;
    }
}
