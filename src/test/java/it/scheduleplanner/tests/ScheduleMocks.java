package it.scheduleplanner.tests;

import it.scheduleplanner.export.Shift;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.export.ShiftDayInterface;
import it.scheduleplanner.utils.Employee;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock classes for testing schedule-related functionality
 */
public class ScheduleMocks {

    /**
     * Mock implementation of ShiftScheduleInterface for testing
     */
    public static class MockShiftSchedule implements ShiftScheduleInterface {
        private final Map<LocalDate, ShiftDayInterface> days = new HashMap<>();

        @Override
        public void addDay(LocalDate date, ShiftDayInterface day) {
            days.put(date, day);
        }

        @Override
        public LocalDate getBeginOfSchedule() {
            return days.keySet().stream().min(LocalDate::compareTo).orElse(null);
        }

        @Override
        public Map<LocalDate, ShiftDayInterface> getSchedule() {
            return days;
        }

        @Override
        public ShiftDayInterface getDay(LocalDate date) {
            // Create new instance only if day doesn't exist yet
            return days.computeIfAbsent(date, k -> new MockShiftDay());
        }
    }

    public static class MockShiftDay implements ShiftDayInterface {
        private final Map<Employee, Shift> employees = new HashMap<>();

        @Override
        public void addEmployee(Employee employee, Shift shift) {
            // If no shift should be assigned (deletion)
            if (shift == null) {
                removeEmployee(employee);
                return;
            }

            int requiredHours = getShiftHours(shift);

            // If employee already has a shift, remove it first
            if (employees.containsKey(employee)) {
                removeEmployee(employee);
            }

            // Check if enough hours are available
            if (employee.getWorkingHours() < requiredHours) {
                return;
            }

            // Deduct hours for new shift
            employee.setWorkingHours(employee.getWorkingHours() - requiredHours);

            // Assign shift
            employees.put(employee, shift);
        }

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

        @Override
        public boolean hasEmployee(Employee employee) {
            return employees.containsKey(employee);
        }

        @Override
        public Shift getEmployeeShift(Employee employee) {
            return employees.get(employee);
        }

        @Override
        public Map<Employee, Shift> getEmployees() {
            // Return copy to prevent external modifications
            return new HashMap<>(employees);
        }

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