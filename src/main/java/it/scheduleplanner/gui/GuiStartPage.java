package it.scheduleplanner.gui;

import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.planner.ScheduleCreator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class GuiStartPage extends Application {

    Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Schedule Planner");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        //title
        Label title = new Label("Welcome to Schedule Planner!");
        GridPane.setConstraints(title, 0, 0);

        //Add Employee
        Label textRow1 = new Label("Add a new employee: ");
        GridPane.setConstraints(textRow1, 0, 3);

        //Add Employee button
        Button addEmployeeButton = new Button("Add employee");
        GridPane.setConstraints(addEmployeeButton, 8, 3);

        //Remove an employee
        Label textRow2 = new Label("Remove an employee: ");
        GridPane.setConstraints(textRow2, 0, 5);

        //Remove Employee button
        Button removeEmployeeButton = new Button("Remove Employee");
        GridPane.setConstraints(removeEmployeeButton, 8, 5);

        //Send Employee on vacation
        Label textRow3 = new Label("Send Employee on Vacation: ");
        GridPane.setConstraints(textRow3, 0, 7);

        //Add Vacation to Employee button
        Button addVacationButton = new Button("Add Vacation");
        GridPane.setConstraints(addVacationButton, 8, 7);

        //Create Shift Schedule button
        Button createShiftScheduleButton = new Button("Create Shift Schedule");
        GridPane.setConstraints(createShiftScheduleButton, 8, 9);
        createShiftScheduleButton.setOnAction(e -> displayCreateShiftScheduleDialog());

        grid.getChildren().addAll(title, textRow1, addEmployeeButton, textRow2, removeEmployeeButton, textRow3, addVacationButton, createShiftScheduleButton);

        Scene scene = new Scene(grid, 800, 600);
        window.setScene(scene);
        window.show();
    }

    private void displayCreateShiftScheduleDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create Shift Schedule");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        // Begin Date
        Label beginLabel = new Label("Begin Date:");
        GridPane.setConstraints(beginLabel, 0, 0);
        DatePicker beginDatePicker = new DatePicker();
        GridPane.setConstraints(beginDatePicker, 1, 0);

        // End Date
        Label endLabel = new Label("End Date:");
        GridPane.setConstraints(endLabel, 0, 1);
        DatePicker endDatePicker = new DatePicker();
        GridPane.setConstraints(endDatePicker, 1, 1);

        // Number of Employees Per Day
        Label numEmployeesLabel = new Label("Number of Employees Per Day:");
        GridPane.setConstraints(numEmployeesLabel, 0, 2);
        TextField numEmployeesField = new TextField();
        GridPane.setConstraints(numEmployeesField, 1, 2);

        // Weekend Open
        Label weekendOpenLabel = new Label("Weekend Open:");
        GridPane.setConstraints(weekendOpenLabel, 0, 3);
        CheckBox weekendOpenCheckBox = new CheckBox();
        GridPane.setConstraints(weekendOpenCheckBox, 1, 3);

        // Rest Day
        Label restDayLabel = new Label("Rest Day:");
        GridPane.setConstraints(restDayLabel, 0, 4);
        ComboBox<DayOfWeek> restDayComboBox = new ComboBox<>();
        restDayComboBox.getItems().addAll(DayOfWeek.values());
        GridPane.setConstraints(restDayComboBox, 1, 4);

        // Create Button
        Button createButton = new Button("Create");
        GridPane.setConstraints(createButton, 1, 5);
        createButton.setOnAction(e -> {
            LocalDate begin = beginDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            int numberOfEmployeesPerDay = Integer.parseInt(numEmployeesField.getText());
            boolean weekendOpen = weekendOpenCheckBox.isSelected();
            DayOfWeek restDay = restDayComboBox.getValue();

            createShiftSchedule(begin, end, numberOfEmployeesPerDay, weekendOpen, restDay);

            dialog.close();
        });

        grid.getChildren().addAll(beginLabel, beginDatePicker, endLabel, endDatePicker, numEmployeesLabel, numEmployeesField, weekendOpenLabel, weekendOpenCheckBox, restDayLabel, restDayComboBox, createButton);

        Scene dialogScene = new Scene(grid, 400, 300);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void createShiftSchedule(LocalDate begin, LocalDate end, int numberOfEmployeesPerDay, boolean weekendOpen, DayOfWeek restDay) {
        // Implement the method to create a shift schedule
        ShiftScheduleInterface schedule = ScheduleCreator.create(begin, end, numberOfEmployeesPerDay, weekendOpen, restDay);
        // You can add code here to handle the created schedule
    }
}
