package it.scheduleplanner.utils;

import java.time.LocalDate;

/**
 * Represents a vacation period with a start and end date.
 */
public class Vacation {

    private LocalDate vacationStart;
    private LocalDate vacationEnd;

    /**
     * Constructs a Vacation object with the specified start and end dates.
     *
     * @param vacationStart the start date of the vacation
     * @param vacationEnd the end date of the vacation
     */
    public Vacation(LocalDate vacationStart, LocalDate vacationEnd) {
        this.vacationStart = vacationStart;
        this.vacationEnd = vacationEnd;
    }

    /**
     * Checks if the specified date falls within the vacation period.
     *
     * @param checkingDate the date to check
     * @return true if the specified date is within the vacation period, false otherwise
     */
    public boolean isOnVacation(LocalDate checkingDate) {
        return !checkingDate.isBefore(vacationStart) && !checkingDate.isAfter(vacationEnd);
    }

    /**
     * Returns the end date of the vacation.
     *
     * @return the end date of the vacation
     */
    public LocalDate getVacationEnd() {
        return vacationEnd;
    }
}
