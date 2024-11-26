package it.scheduleplanner.utils;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.export.ShiftDayInterface;
import java.time.LocalDate;
import java.util.Map;

public class ShiftModifier {

    /**
     * Modifies an existing shift assignment for an employee
     */
    public static boolean modifyShift(ShiftScheduleInterface calendar,
                                      LocalDate date,
                                      Employee employee,
                                      Shift newShift) {
        try {
            ShiftDayInterface day = calendar.getDay(date);
            if (day == null) {
                return false;
            }

            // Determine current shift
            Map<Employee, Shift> currentAssignments = day.getEmployees();
            Shift currentShift = currentAssignments.get(employee);

            if (currentShift != null) {
                // Return existing shift hours
                adjustWorkingHours(employee, currentShift, true);
            }

            // Assign new shift
            day.addEmployee(employee, newShift);
            // Subtract new shift hours
            adjustWorkingHours(employee, newShift, false);

            return true;
        } catch (Exception e) {
            System.err.println("Error modifying shift: " + e.getMessage());
            return false;
        }
    }

    /**
     * Swaps shifts between two employees
     */
    public static boolean swapShifts(ShiftScheduleInterface calendar,
                                     LocalDate date,
                                     Employee employee1,
                                     Employee employee2) {
        try {
            ShiftDayInterface day = calendar.getDay(date);
            if (day == null) {
                return false;
            }

            Map<Employee, Shift> assignments = day.getEmployees();
            Shift shift1 = assignments.get(employee1);
            Shift shift2 = assignments.get(employee2);

            // Remove both shifts (new assignment will override old ones)
            if (shift1 != null) {
                adjustWorkingHours(employee1, shift1, true);
            }
            if (shift2 != null) {
                adjustWorkingHours(employee2, shift2, true);
            }

            // Assign new shifts
            if (shift1 != null) {
                day.addEmployee(employee2, shift1);
                adjustWorkingHours(employee2, shift1, false);
            }
            if (shift2 != null) {
                day.addEmployee(employee1, shift2);
                adjustWorkingHours(employee1, shift2, false);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error swapping shifts: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adjust the working hours of the employee
     */
    private static void adjustWorkingHours(Employee employee, Shift shift, boolean isAdding) {
        int hours;
        switch (shift) {
            case FULL:
                hours = 8;
                break;
            case MORNING:
            case AFTERNOON:
            case HALF:  // HALF is treated like MORNING( see FixedShiftDay)
                hours = 4;
                break;
            default:
                hours = 0;
                break;
        }

        if (isAdding) {
            employee.setWorkingHours(employee.getWorkingHours() + hours);
        } else {
            employee.setWorkingHours(employee.getWorkingHours() - hours);
        }
    }

    /**
     * Checks if a shift modification is allowed
     */
    public static boolean isModificationAllowed(Employee employee, Shift newShift) {
        int requiredHours = newShift == Shift.FULL ? 8 : 4;
        return employee.getWorkingHours() >= requiredHours;
    }
}