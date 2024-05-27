package it.scheduleplanner.planner;

import it.scheduleplanner.export.Export;
import it.scheduleplanner.export.FixedShiftsSchedule;
import it.scheduleplanner.export.ShiftScheduleInterface;

import java.time.LocalDate;

public class ScheduleCreator {
    // TODO check if enough  employees per day
    // TODO working half or full? if half add either morning or afternoon
    //TODO add necessary functions: adding employees, checking if all shifts complete, export
    //example if time span 7 days ...export after 7 days
    public static void create(LocalDate begin, LocalDate end) {
        ShiftScheduleInterface schedule = new FixedShiftsSchedule(begin);
        Export.CSVExport(schedule, null); // TODO change path
    }


}
