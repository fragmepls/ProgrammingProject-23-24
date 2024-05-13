package it.scheduleplanner.utils;

import java.time.LocalDate;
import java.time.Period;

public class Vacation {

    LocalDate vacationStart;
    LocalDate vacationEnd;
    Period vacation = Period.between(vacationStart, vacationEnd);
    LocalDate checkingDate;

    public Vacation(LocalDate vacationStart, LocalDate vacationEnd) {
        this.vacationStart = vacationStart;
        this.vacationEnd = vacationEnd;
    }

    public boolean  isAvailable(LocalDate checkingDate) {
        this.checkingDate = checkingDate;

        if (!checkingDate.isAfter(vacationEnd) && !checkingDate.isBefore(vacationStart)) {
            return false;
        } else {
            return true;
        }
    }

    public LocalDate getVacationStart() {
        return vacationStart;
    }

    public void setVacationStart(LocalDate vacationStart) {
        this.vacationStart = vacationStart;
    }

    public LocalDate getVacationEnd() {
        return vacationEnd;
    }

    public void setVacationEnd(LocalDate vacationEnd) {
        this.vacationEnd = vacationEnd;
    }
}
