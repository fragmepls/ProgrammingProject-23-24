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
            return days.computeIfAbsent(date, k -> new MockShiftDay());
        }
    }

    /**
     * Mock implementation of ShiftDayInterface for testing
     */
    public static class MockShiftDay implements ShiftDayInterface {
        private final Map<Employee, Shift> employees = new HashMap<>();

        @Override
        public Map<Employee, Shift> getEmployees() {
            return employees;
        }

        @Override
        public void addEmployee(Employee employee, Shift shift) {
            // First handle existing shift if any
            if (employees.containsKey(employee)) {
                Shift currentShift = employees.get(employee);
                returnHours(employee, currentShift);
            }

            if (shift == null) {
                removeEmployee(employee);
                return;
            }

            // Deduct hours based on new shift
            int hoursToDeduct = getShiftHours(shift);
            employee.setWorkingHours(employee.getWorkingHours() - hoursToDeduct);

            employees.put(employee, shift);
        }

        @Override
        public void removeEmployee(Employee employee) {
            if (employees.containsKey(employee)) {
                Shift shift = employees.get(employee);
                returnHours(employee, shift);
                employees.remove(employee);
            }
        }

        private void returnHours(Employee employee, Shift shift) {
            if (shift != null) {
                int hoursToReturn = getShiftHours(shift);
                employee.setWorkingHours(employee.getWorkingHours() + hoursToReturn);
            }
        }

        private int getShiftHours(Shift shift) {
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

        @Override
        public Shift getEmployeeShift(Employee employee) {
            return employees.get(employee);
        }

        @Override
        public boolean hasEmployee(Employee employee) {
            return employees.containsKey(employee);
        }
    }
}