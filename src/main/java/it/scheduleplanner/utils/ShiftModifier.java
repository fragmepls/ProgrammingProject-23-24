package it.scheduleplanner.utils;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.export.ShiftDayInterface;
import java.time.LocalDate;
import java.util.Map;

public class ShiftModifier {

    public static boolean modifyShift(ShiftScheduleInterface calendar,
                                      LocalDate date,
                                      Employee employee,
                                      Shift newShift) {
        try {
            ShiftDayInterface day = calendar.getDay(date);
            if (day == null) {
                System.err.println("Day not found in calendar");
                return false;
            }

            // If we're deleting the shift (newShift is null)
            if (newShift == null) {
                // Just remove the employee and let MockShiftDay handle hour restoration
                day.removeEmployee(employee);
                return true;
            }

            // Check if employee has enough hours for the new shift
            if (!isModificationAllowed(employee, newShift)) {
                System.err.println("Employee doesn't have enough hours for shift");
                return false;
            }

            // Let MockShiftDay handle hour calculations
            day.addEmployee(employee, newShift);
            return true;

        } catch (Exception e) {
            System.err.println("Error modifying shift: " + e.getMessage());
            return false;
        }
    }

    public static boolean swapShifts(ShiftScheduleInterface calendar,
                                     LocalDate date,
                                     Employee employee1,
                                     Employee employee2) {
        try {
            ShiftDayInterface day = calendar.getDay(date);
            if (day == null) return false;

            Map<Employee, Shift> assignments = day.getEmployees();
            Shift shift1 = assignments.get(employee1);
            Shift shift2 = assignments.get(employee2);

            if (shift1 == null || shift2 == null) return false;

            // Store shifts
            Shift tempShift1 = shift1;
            Shift tempShift2 = shift2;

            // Remove both employees first
            day.removeEmployee(employee1);
            day.removeEmployee(employee2);

            // Add them back with swapped shifts
            day.addEmployee(employee1, tempShift2);
            day.addEmployee(employee2, tempShift1);

            return true;
        } catch (Exception e) {
            System.err.println("Error swapping shifts: " + e.getMessage());
            return false;
        }
    }

    public static boolean isModificationAllowed(Employee employee, Shift newShift) {
        if (newShift == null) return true;  // Deletion is always allowed

        int requiredHours = getShiftHours(newShift);
        return employee.getWorkingHours() >= requiredHours;
    }

    private static int getShiftHours(Shift shift) {
        if (shift == null) return 0;
        switch (shift) {
            case FULL:
                return 8;
            case MORNING:
            case AFTERNOON:
            case HALF:
                return 4;
            default:
                return 0;
        }
    }
}