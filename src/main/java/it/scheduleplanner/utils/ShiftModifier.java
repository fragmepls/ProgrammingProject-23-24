package it.scheduleplanner.utils;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.export.ShiftDayInterface;
import java.time.LocalDate;
import java.util.Map;

public class ShiftModifier {

    /**
     * Modifies a shift for an employee on a specific date. (interacting with MockShiftDay)
     *
     * @param calendar The schedule to modify
     * @param date The date of the shift
     * @param employee The employee whose shift should be modified
     * @param newShift The new shift to assign (null for deletion)
     * @return true if modification successful, false otherwise
     */
    public static boolean modifyShift(ShiftScheduleInterface calendar,
                                      LocalDate date,
                                      Employee employee,
                                      Shift newShift) {
        try {
            // Get the day for the given date (creates new day if none exists)
            ShiftDayInterface day = calendar.getDay(date);
            if (day == null) {
                System.err.println("Day not found in calendar");
                return false;
            }

            // Handle shift deletion
            if (newShift == null) {
                day.removeEmployee(employee);  // Removes employee and restores their hours
                return true;
            }

            // Verify employee has enough hours for new shift
            if (!isModificationAllowed(employee, newShift)) {
                System.err.println("Employee doesn't have enough hours for shift");
                return false;
            }

            // Add employee to new shift (MockShiftDay handles hour calculations)
            day.addEmployee(employee, newShift);
            return true;

        } catch (Exception e) {
            System.err.println("Error modifying shift: " + e.getMessage());
            return false;
        }
    }

    /**
     * Swaps shifts between two employees on a specific date. (interacting with MockShiftDay)
     *
     * @param calendar The schedule containing the shifts
     * @param date The date of the shifts to swap
     * @param employee1 First employee
     * @param employee2 Second employee
     * @return true if swap successful, false otherwise
     */
    public static boolean swapShifts(ShiftScheduleInterface calendar,
                                     LocalDate date,
                                     Employee employee1,
                                     Employee employee2) {
        try {
            // Get the day and verify it exists
            ShiftDayInterface day = calendar.getDay(date);
            if (day == null) return false;

            // Get current shifts of both employees
            Map<Employee, Shift> assignments = day.getEmployees();
            Shift shift1 = assignments.get(employee1);
            Shift shift2 = assignments.get(employee2);

            // Verify both employees have shifts to swap
            if (shift1 == null || shift2 == null) return false;

            // Store shifts temporarily
            Shift tempShift1 = shift1;
            Shift tempShift2 = shift2;

            // Remove both employees (this restores their hours)
            day.removeEmployee(employee1);
            day.removeEmployee(employee2);

            // Reassign with swapped shifts (this deducts hours appropriately)
            day.addEmployee(employee1, tempShift2);
            day.addEmployee(employee2, tempShift1);

            return true;
        } catch (Exception e) {
            System.err.println("Error swapping shifts: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an employee has enough working hours for a shift modification.
     *
     * @param employee The employee to check
     * @param newShift The new shift to verify
     * @return true if modification allowed, false if insufficient hours
     */
    public static boolean isModificationAllowed(Employee employee, Shift newShift) {
        // Shift deletion is always allowed
        if (newShift == null) return true;

        // Check if employee has enough hours for the shift
        int requiredHours = getShiftHours(newShift);
        return employee.getWorkingHours() >= requiredHours;
    }

    /**
     * Calculates required hours for different shift types.
     *
     * @param shift The shift type to check
     * @return Number of hours required for the shift
     */
    private static int getShiftHours(Shift shift) {
        if (shift == null) return 0;
        switch (shift) {
            case FULL:           // Full day shift
                return 8;
            case MORNING:        // Half day shifts
            case AFTERNOON:
            case HALF:
                return 4;
            default:
                return 0;
        }
    }
}