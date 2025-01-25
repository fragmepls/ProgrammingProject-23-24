package it.scheduleplanner.tests;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.export.ShiftDayInterface;
import it.scheduleplanner.utils.Employee;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock classes for testing schedule-related functionality.
 * Contains mock implementations of ShiftScheduleInterface and ShiftDayInterface
 * to simulate schedule behavior in tests without using real database/persistence.
 */
public class ScheduleMocks {

    /**
     * Mock implementation of ShiftScheduleInterface for testing purposes.
     * Simulates a schedule containing multiple days with shift assignments.
     */
    public static class MockShiftSchedule implements ShiftScheduleInterface {
        // Maps dates to their corresponding shift days
        private final Map<LocalDate, ShiftDayInterface> days = new HashMap<>();

        /**
         * Adds a specific day to the schedule.
         * @param date The date to add
         * @param day The shift day to add
         */
        @Override
        public void addDay(LocalDate date, ShiftDayInterface day) {
            days.put(date, day);
        }

        /**
         * Gets the earliest date in the schedule.
         * Uses stream to find minimum date or returns null if schedule is empty.
         * @return The earliest date or null if no days exist
         */
        @Override
        public LocalDate getBeginOfSchedule() {
            return days.keySet().stream().min(LocalDate::compareTo).orElse(null);
        }

        /**
         * Returns the complete schedule mapping.
         * @return Map of dates to their shift days
         */
        @Override
        public Map<LocalDate, ShiftDayInterface> getSchedule() {
            return days;
        }

        /**
         * Gets or creates a shift day for the specified date.
         * If no day exists for the date, creates a new MockShiftDay.
         * @param date The date to get/create a day for
         * @return The existing or newly created shift day
         */
        @Override
        public ShiftDayInterface getDay(LocalDate date) {
            return days.computeIfAbsent(date, k -> new MockShiftDay());
        }
    }

    /**
     * Mock implementation of ShiftDayInterface for testing purposes.
     * Manages employee shift assignments and working hours for a single day.
     */
    public static class MockShiftDay implements ShiftDayInterface {
        // Maps employees to their assigned shifts
        private final Map<Employee, Shift> employees = new HashMap<>();

        /**
         * Adds or modifies an employee's shift assignment.
         * Handles hour calculations and shift changes.
         * @param employee The employee to assign
         * @param shift The shift to assign (null for deletion)
         */
        @Override
        public void addEmployee(Employee employee, Shift shift) {
            // Handle shift deletion
            if (shift == null) {
                removeEmployee(employee);
                return;
            }

            // Calculate required hours for the shift
            int requiredHours = getShiftHours(shift);

            // Remove existing shift if employee already has one
            if (employees.containsKey(employee)) {
                removeEmployee(employee);
            }

            // Verify sufficient hours available
            if (employee.getWorkingHours() < requiredHours) {
                return;
            }

            // Deduct hours and assign shift
            employee.setWorkingHours(employee.getWorkingHours() - requiredHours);
            employees.put(employee, shift);
        }

        /**
         * Removes an employee's shift assignment and restores their hours.
         * @param employee The employee to remove
         */
        @Override
        public void removeEmployee(Employee employee) {
            if (employees.containsKey(employee)) {
                // Return hours from current shift
                Shift currentShift = employees.get(employee);
                int hoursToReturn = getShiftHours(currentShift);
                employee.setWorkingHours(employee.getWorkingHours() + hoursToReturn);
                employees.remove(employee);
            }
        }

        /**
         * Checks if an employee has a shift assignment.
         * @param employee The employee to check
         * @return true if employee has a shift assigned
         */
        @Override
        public boolean hasEmployee(Employee employee) {
            return employees.containsKey(employee);
        }

        /**
         * Gets an employee's assigned shift.
         * @param employee The employee to check
         * @return The assigned shift or null if none assigned
         */
        @Override
        public Shift getEmployeeShift(Employee employee) {
            return employees.get(employee);
        }

        /**
         * Gets all employee shift assignments.
         * Returns a copy to prevent external modifications.
         * @return Map of employees to their shifts
         */
        @Override
        public Map<Employee, Shift> getEmployees() {
            return new HashMap<>(employees);
        }

        /**
         * Calculates required working hours for different shift types.
         * @param shift The shift type to calculate hours for
         * @return Number of hours required for the shift
         */
        private int getShiftHours(Shift shift) {
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
}