package it.scheduleplanner.export;

import java.util.Map;

import it.scheduleplanner.utils.Employee;

/**
 * An object that maps Employees to their respective Shift and may be used as a way to indicate a Shift plan for a day.
 */
public interface ShiftDayInterface {

    /**
     * Adds employee to the Day with their associated shift.<br>
     * <br>
     * In case as shift Shift.HALF got accidentally selected, it will be converted into Shift.MORNING to ensure the exportability of the resulting day and therefore also schedule.
     *
     * @param employee to be added to the Work Day
     * @param shift    To select from enum Shift, determines the shift
     */
    public void addEmployee(Employee employee, Shift shift);

    public void removeEmployee(Employee employee);

    Shift getEmployeeShift(Employee employee);

    boolean hasEmployee(Employee employee);

    /**
     * @return HashMap: Employees mapped to Shifts
     */
    public Map<Employee, Shift> getEmployees();

    String toString();
}