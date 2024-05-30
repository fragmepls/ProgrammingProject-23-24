package it.scheduleplanner.utils;

import java.time.LocalDate;
import java.time.Period;

public class Vacation {

    private LocalDate vacationStart;
    private LocalDate vacationEnd;
    LocalDate checkingDate;

    public Vacation(LocalDate vacationStart, LocalDate vacationEnd) {
        this.vacationStart = vacationStart;
        this.vacationEnd = vacationEnd;
    }


    public boolean isOnVacation(LocalDate checkingDate) {
        return !checkingDate.isBefore(vacationStart) && !checkingDate.isAfter(vacationEnd);
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

    public void sendOnVacation(LocalDate VacationStart, LocalDate VacationEnd) {
        this.vacationStart = VacationStart;
        this.vacationEnd = VacationEnd;
    }
}
