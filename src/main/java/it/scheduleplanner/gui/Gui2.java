package it.scheduleplanner.gui;

import it.scheduleplanner.dbutils.*;
import it.scheduleplanner.export.*;
import it.scheduleplanner.planner.*;
import it.scheduleplanner.utils.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.sql.*;
import java.time.*;
import java.util.*;
/**
 * Main GUI class for the Schedule Planner application.
 */
public class Gui2 extends Application {

    private Connection connection;
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private String outputDirectory;
    private ShiftScheduleInterface calendar;  // Neue Variable für den Kalender

    /**
     * Main method to launch the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the primary stage (main window) of the application.
     *
     * @param primaryStage The primary stage.
     * @throws SQLException If a database access error occurs.
     */

    @Override
    public void start(Stage primaryStage) throws SQLException {
        DBUtils.initializeDatabase();
        connection = DBUtils.getConnection();

        primaryStage.setTitle("Schedule Planner");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10, 10, 10, 10));

        TabPane tabPane = new TabPane();

        Tab welcomeTab = welcomeTab();
        Tab employeeTab = createEmployeeTab(primaryStage);
        Tab vacationTab = createVacationTab();
        Tab importScheduleTab = createImportTab();
        Tab scheduleTab = createScheduleTab(primaryStage);
        Tab modifyShiftsTab = createShiftModificationTab();

        tabPane.getTabs().addAll(welcomeTab, employeeTab, vacationTab,
                importScheduleTab, scheduleTab, modifyShiftsTab);
        mainLayout.setCenter(tabPane);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates the Welcome tab.
     *
     * @return The Welcome tab.
     */
    private Tab welcomeTab() {
        Tab tab = new Tab("Welcome to Schedule Creator");
        Label welcomeLabel = new Label("Welcome to Schedule Creator");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label("Schedule Creator is a tool designed to help you manage employee schedules effectively.\n" +
                "You can add employees, schedule their vacations, configure work schedules, and generate shift calendars.\n" +
                "The final schedule is exported as a CSV file, which can be opened in Microsoft Excel, Apple Numbers, or similar software.\n\n" +
                "Add Employee Tab:\n" +
                "To add an employee, you need to enter the following information:\n\n" +
                "    - Employee Name: Enter the name of the employee.\n" +
                "    - Is Weekend Worker: Check this box if the employee works on weekends.\n" +
                "    - Select a Fixed Free Day: Choose a day of the week when the employee is free.\n" +
                "    - Is Full Time: Check this box if the employee works full time.\n\n" +
                "Send Employee on Vacation Tab:\n" +
                "To send an employee on vacation, provide the following details:\n\n" +
                "    - Employee: Select the employee from the dropdown list.\n" +
                "    - Start Date: Choose the start date of the vacation.\n" +
                "    - End Date: Choose the end date of the vacation.\n\n" +
                "Schedule Configuration Tab:\n" +
                "To configure the work schedule, fill in the following information:\n\n" +
                "    - Number of Employees per Day: Enter the required number of employees for each day.\n" +
                "    - Begin Date: Select the start date for the schedule.\n" +
                "    - End Date: Select the end date for the schedule.\n" +
                "    - Open on Weekends: Check this box if the business operates on weekends.\n" +
                "    - Select Rest Day: Choose a day of the week when the business is closed, or select \"NO DAY\" if there is no fixed rest day.\n" +
                "    - Choose Output Directory: Click the button to select the directory where the schedule will be saved.");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 14px;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(descriptionLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(600, 400); // Adjust width and height as needed

        VBox vbox = new VBox(10); //a layout container that arranges child nodes (all components) vertically in one column
        // 10 means 10px space vertically between children - child nodes
        vbox.getChildren().addAll(welcomeLabel, scrollPane);
        vbox.setAlignment(Pos.CENTER);


        tab.setContent(vbox);
        return tab;
    }

    /**
     * Creates the Vacation tab.
     *
     * @return The Vacation tab.
     */
    private Tab createVacationTab() {
        Tab tab = new Tab("Send Employee on Vacation");
        VBox vbox = new VBox(10);

        ComboBox<Employee> employeeComboBox = new ComboBox<>(employeeList); // Bind the employee list to the ComboBox

        // Customize the ComboBox to display employee names
        employeeComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                if (empty || employee == null || employee.getName() == null) {
                    setText(null);
                }
                else {
                    setText(employee.getName());
                }
            }
        });

        employeeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                if (empty || employee == null || employee.getName() == null) {
                    setText(null);
                }
                else {
                    setText(employee.getName());
                }
            }
        });

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        Button addVacationButton = new Button("Add Vacation");


        addVacationButton.setOnAction(e -> {                //lambda expression
            Employee employee = employeeComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (employee == null || startDate == null || endDate == null) {
                showAlert("Input Error", "Some of the inputs are wrong, try again.", Alert.AlertType.ERROR);
                return;
            }

            if (startDate.isAfter(endDate)) {
                showAlert("Input Error", "Start date cannot be after end date.", Alert.AlertType.ERROR);
                return;
            }

            Vacation vacation = new Vacation(startDate, endDate);
            employee.addVacation(vacation);
            try {
                SQLQueries.insertVacation(connection, employee.getEmployeeId(), startDate.toString(), endDate.toString());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        });

        Button importVacationsButton = new Button("Import Vacation for Employee");

        importVacationsButton.setOnAction(e -> {
            try {
                Employee employee = employeeComboBox.getValue();
                if (employee == null) {
                    showAlert("Input Error", "Please select an employee.", Alert.AlertType.ERROR);
                }
                else {
                    List<Vacation> vacations = SQLQueries.selectVacation(connection, employee.getEmployeeId());
                    if (vacations.isEmpty()) {
                        showAlert("No Vacations Found", "No vacations were found for the selected employee.", Alert.AlertType.INFORMATION);
                    }
                    else {
                        for (Vacation vacation : vacations) {
                            employee.addVacation(vacation);
                        }
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        vbox.getChildren().addAll(new Label("Enter Vacation Details:"), employeeComboBox, startDatePicker, endDatePicker, addVacationButton, importVacationsButton);
        tab.setContent(vbox);
        return tab;
    }


    /**
     * Creates the Import Schedule tab.
     *
     * @return The Import Schedule tab.
     */
    private Tab createImportTab() {
        Tab tab = new Tab("Import Schedule");
        VBox vbox = new VBox(10);

        Label instructionLabel = new Label("Select a CSV file to import schedule:");
        Button chooseFileButton = new Button("Choose File");
        Label fileLabel = new Label("No file selected");

        chooseFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                fileLabel.setText("File: " + selectedFile.getAbsolutePath());
            }
        });

        Button importButton = new Button("Import Schedule");
        importButton.setOnAction(e -> {
            if (fileLabel.getText().equals("No file selected")) {
                showAlert("File Error", "Please select a file to import.", Alert.AlertType.ERROR);
            }
            else {
                String pathToFile = fileLabel.getText().substring(6); // remove "File: " prefix
                ShiftScheduleInterface importedSchedule = Import.importSchedule(pathToFile, ScheduleCreator.employeeSet);
                if (importedSchedule != null) {
                    showAlert("Success", "Schedule imported successfully.", Alert.AlertType.INFORMATION);
                    // Update the GUI or data model to reflect the imported schedule
                }
                else {
                    showAlert("Import Error", "Failed to import schedule. Please check the file and try again.", Alert.AlertType.ERROR);
                }
            }
        });

        vbox.getChildren().addAll(instructionLabel, chooseFileButton, fileLabel, importButton);
        tab.setContent(vbox);
        return tab;
    }

    /**
     * Creates the Employee tab.
     *
     * @param primaryStage The primary stage.
     * @return The Employee tab.
     */
    private Tab createEmployeeTab(Stage primaryStage) {
        Tab tab = new Tab("Add Employee");
        VBox vbox = new VBox(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        CheckBox weekendWorkerCheckBox = new CheckBox("Is Weekend Worker");

        ComboBox<String> freeDayComboBox = new ComboBox<>();
        freeDayComboBox.getItems().addAll(Arrays.stream(DayOfWeek.values()) //DayOfWeek. values returns an array of the ENUM (MONDAY, TUESDAY...)
                //Arrays.stream makes out of the array a stream
                .map(DayOfWeek::toString)                                   //.map is a method of streams that transforms each element of the stream --> here the DayofWeak to String)
                .toList());                                                 //is a operation of stream, used to store all elements of the stream as an array.

        //could also be done without stream, but with stream better transformation and better readability

        CheckBox fullTimeCheckBox = new CheckBox("Is Full Time");

        Button addButton = new Button("Add Employee");


        addButton.setOnAction(event -> {   //lambda expression -- usually used in Java with functional Interfaces
            String name = nameField.getText();
            boolean isWeekendWorker = weekendWorkerCheckBox.isSelected();
            String freeDay = freeDayComboBox.getValue();
            boolean isFullTime = fullTimeCheckBox.isSelected();

            if (name.isEmpty() || freeDay == null) {
                showAlert("Input Error", "Some of the inputs are wrong, try again.", Alert.AlertType.ERROR);
                return;
            }

            Employee employee = new Employee(name, isWeekendWorker, freeDay, isFullTime);
            try {
                employee.setEmployeeId(SQLQueries.insertEmployee(connection, employee));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            employeeList.add(employee);

            nameField.clear();
            weekendWorkerCheckBox.setSelected(false);
            fullTimeCheckBox.setSelected(false);
        });

        Button importButton = new Button("Import Employees from Database");

        importButton.setOnAction(e -> {
            try {
                employeeList.addAll(SQLQueries.selectAllEmployees(connection));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button viewEmployeesButton = new Button("View Employees");
        viewEmployeesButton.setOnAction(e -> showEmployeeList(primaryStage));

        vbox.getChildren().addAll(
                new Label("Enter Employee Name:"), nameField, weekendWorkerCheckBox,
                new Label("Select a fixed free Day:"),
                freeDayComboBox, fullTimeCheckBox, addButton, viewEmployeesButton
        );
        tab.setContent(vbox);
        return tab;
    }

    /**
     * Creates the Shift Modification tab.
     *
     * @return The Shift Modification tab.
     */
    private Tab createShiftModificationTab() {
        Tab tab = new Tab("Modify Shifts");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Date selection
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

        // Employee selection
        ComboBox<Employee> employeeComboBox = new ComboBox<>(employeeList);
        employeeComboBox.setPromptText("Select Employee");

        // Configure employee display
        employeeComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                setText(empty || employee == null ? null : employee.getName());
            }
        });
        employeeComboBox.setButtonCell(employeeComboBox.getCellFactory().call(null));

        // Shift selection
        ComboBox<Shift> shiftComboBox = new ComboBox<>();
        shiftComboBox.getItems().addAll(Shift.MORNING, Shift.AFTERNOON, Shift.FULL);
        shiftComboBox.setPromptText("Select Shift");

        // Display current schedule
        TextArea currentScheduleArea = new TextArea();
        currentScheduleArea.setEditable(false);
        currentScheduleArea.setPrefRowCount(5);
        currentScheduleArea.setWrapText(true);

        // Button to view current schedule
        Button viewCurrentButton = new Button("View Current Schedule");
        viewCurrentButton.setOnAction(e -> {
            if (calendar == null || datePicker.getValue() == null) {
                showAlert("Error", "Please select a date and ensure a schedule exists.", Alert.AlertType.ERROR);
                return;
            }
            ShiftDayInterface day = calendar.getDay(datePicker.getValue());
            if (day == null) {
                currentScheduleArea.setText("No schedule for selected date.");
                return;
            }
            StringBuilder schedule = new StringBuilder("Current assignments:\n");
            Map<Employee, Shift> assignments = day.getEmployees();
            assignments.forEach((emp, shift) ->
                    schedule.append(emp.getName()).append(": ").append(shift).append("\n")
            );
            currentScheduleArea.setText(schedule.toString());
        });

        // Button for shift modification
        Button modifyButton = new Button("Modify Shift");
        modifyButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            Employee employee = employeeComboBox.getValue();
            Shift newShift = shiftComboBox.getValue();

            if (date == null || employee == null || newShift == null) {
                showAlert("Input Error", "Please select date, employee, and shift.", Alert.AlertType.ERROR);
                return;
            }

            if (calendar == null) {
                showAlert("Error", "No schedule available. Please generate a schedule first.", Alert.AlertType.ERROR);
                return;
            }

            if (!ShiftModifier.isModificationAllowed(employee, newShift)) {
                showAlert("Error", "Employee does not have enough working hours available.", Alert.AlertType.ERROR);
                return;
            }

            try {
                if (ShiftModifier.modifyShift(calendar, date, employee, newShift)) {
                    showAlert("Success", "Shift modified successfully.", Alert.AlertType.INFORMATION);
                    // Update display
                    viewCurrentButton.fire();
                    updateScheduleDisplay();
                }
                else {
                    showAlert("Error", "Failed to modify shift.", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                showAlert("Error", "An error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Shift swap section
        Label swapLabel = new Label("Swap Shifts Between Employees");
        swapLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");

        ComboBox<Employee> employee1ComboBox = new ComboBox<>(employeeList);
        employee1ComboBox.setPromptText("Select First Employee");
        ComboBox<Employee> employee2ComboBox = new ComboBox<>(employeeList);
        employee2ComboBox.setPromptText("Select Second Employee");

        // Configure employee display for swap
        employee1ComboBox.setCellFactory(employeeComboBox.getCellFactory());
        employee2ComboBox.setCellFactory(employeeComboBox.getCellFactory());

        Button swapButton = new Button("Swap Shifts");
        swapButton.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            Employee emp1 = employee1ComboBox.getValue();
            Employee emp2 = employee2ComboBox.getValue();

            if (date == null || emp1 == null || emp2 == null) {
                showAlert("Input Error", "Please select date and both employees.", Alert.AlertType.ERROR);
                return;
            }

            if (calendar == null) {
                showAlert("Error", "No schedule available. Please generate a schedule first.", Alert.AlertType.ERROR);
                return;
            }

            try {
                if (ShiftModifier.swapShifts(calendar, date, emp1, emp2)) {
                    showAlert("Success", "Shifts swapped successfully.", Alert.AlertType.INFORMATION);
                    viewCurrentButton.fire();
                    updateScheduleDisplay();
                }
                else {
                    showAlert("Error", "Failed to swap shifts.", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                showAlert("Error", "An error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Assemble the layout
        vbox.getChildren().addAll(
                new Label("Select Date:"),
                datePicker,
                new Label("Current Schedule:"),
                currentScheduleArea,
                viewCurrentButton,
                new Separator(),
                new Label("Modify Individual Shift:"),
                employeeComboBox,
                shiftComboBox,
                modifyButton,
                new Separator(),
                swapLabel,
                new Label("First Employee:"),
                employee1ComboBox,
                new Label("Second Employee:"),
                employee2ComboBox,
                swapButton
        );

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        return tab;
    }

    private void updateScheduleDisplay() {
        if (outputDirectory != null && calendar != null) {
            Map<Boolean, String> exportResult = Export.CSVExport(calendar,
                    ScheduleCreator.employeeSet, outputDirectory);

            if (!exportResult.containsKey(true) || exportResult.get(true) == null) {
                showAlert("Export Error", "Failed to update schedule export", Alert.AlertType.WARNING);
            }
        }
    }

    private Tab createScheduleTab(Stage primaryStage) {
        Tab tab = new Tab("Schedule Configuration");
        VBox vbox = new VBox(10);

        TextField employeesPerDayField = new TextField();
        employeesPerDayField.setPromptText("Number of Employees per Day");

        DatePicker beginDatePicker = new DatePicker();
        beginDatePicker.setPromptText("Begin Date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        CheckBox weekendOpenCheckBox = new CheckBox("Open on Weekends");

        ComboBox<String> restDayComboBox = new ComboBox<>();
        restDayComboBox.getItems().add("NO DAY");
        restDayComboBox.getItems().addAll(Arrays.stream(DayOfWeek.values())
                .map(DayOfWeek::toString)
                .toList());
        restDayComboBox.setPromptText("Select Rest Day");

        Button chooseDirectoryButton = new Button("Choose Output Directory");
        Label directoryLabel = new Label("No directory chosen");

        chooseDirectoryButton.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Output Directory");
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                outputDirectory = selectedDirectory.getAbsolutePath();
                directoryLabel.setText("Output Directory: " + outputDirectory);
            }
        });

        Button generateScheduleButton = new Button("Generate Schedule");

        generateScheduleButton.setOnAction(e -> {
            try {
                int numberOfEmployeesPerDay = Integer.parseInt(employeesPerDayField.getText());
                LocalDate beginDate = beginDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                boolean weekendOpen = weekendOpenCheckBox.isSelected();
                String restDay = restDayComboBox.getValue();

                if (beginDate == null || endDate == null) {
                    showAlert("Input Error", "Some of the inputs are wrong, try again.", Alert.AlertType.ERROR);
                    return;
                }

                if (beginDate.isAfter(endDate)) {
                    showAlert("Input Error", "Begin date cannot be after end date.", Alert.AlertType.ERROR);
                    return;
                }

                if (restDay == null) {
                    showAlert("Input Error", "Please select a rest day or no day.", Alert.AlertType.ERROR);
                    return;
                }

                DayOfWeek restDayEnum = "NO DAY".equals(restDay) ? null : DayOfWeek.valueOf(restDay.toUpperCase());

                ArrayList<Employee> arrayList = new ArrayList<>(employeeList);
                ScheduleCreator.addEmployeeList(arrayList);

                // Hier wird der calendar gesetzt
                calendar = ScheduleCreator.create(beginDate, endDate, numberOfEmployeesPerDay, weekendOpen, restDayEnum);

                if (outputDirectory != null) {
                    Map<Boolean, String> exportResult = Export.CSVExport(calendar, ScheduleCreator.employeeSet, outputDirectory);

                    if (exportResult.containsKey(true) && exportResult.get(true) != null) {
                        showAlert("Success", "Schedule exported successfully to: " + exportResult.get(true), Alert.AlertType.INFORMATION);
                    }
                    else {
                        showAlert("Error", "Failed to export schedule. Please check the output directory and try again.", Alert.AlertType.ERROR);
                    }

                    Export.employeeExport(ScheduleCreator.employeeSet, outputDirectory);
                }
                else {
                    System.out.println("No output directory selected.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Number of Employees per Day must be a valid integer.", Alert.AlertType.ERROR);
            } catch (InsufficientEmployeesException ex) {
                showAlert("Scheduling Error", "There are not enough employees available, even with adding overtime hours. Please consider adding more employees or decreasing the number of employees required per day, then try again.", Alert.AlertType.ERROR);
            } catch (Exception ex) {
                showAlert("Error", "An unexpected error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button showEmployeesButton = new Button("Check overtime hours");
        showEmployeesButton.setOnAction(e -> showEmployeesAndOvertime());

        Button generateEmptyScheduleButton = new Button("Generate Empty Schedule");
        generateEmptyScheduleButton.setOnAction(e -> {
            LocalDate beginDate = beginDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (beginDate == null || endDate == null) {
                showAlert("Input Error", "Please select both a start and end date.", Alert.AlertType.ERROR);
                return;
            }

            if (beginDate.isAfter(endDate)) {
                showAlert("Input Error", "Begin date cannot be after end date.", Alert.AlertType.ERROR);
                return;
            }

            if (outputDirectory != null) {
                Set<Employee> employeeSet = ScheduleCreator.employeeSet;
                Map<Boolean, String> exportResult = Export.exportBlankSchedule(beginDate, endDate, employeeSet, outputDirectory);

                if (exportResult.containsKey(true) && exportResult.get(true) != null) {
                    showAlert("Success", "Empty schedule exported successfully to: " + exportResult.get(true), Alert.AlertType.INFORMATION);
                }
                else {
                    showAlert("Error", "Failed to export empty schedule. Please check the output directory and try again.", Alert.AlertType.ERROR);
                }
            }
            else {
                showAlert("Error", "No output directory selected.", Alert.AlertType.ERROR);
            }
        });

        vbox.getChildren().addAll(
                new Label("Enter Schedule Configuration:"),
                employeesPerDayField,
                beginDatePicker,
                endDatePicker,
                weekendOpenCheckBox,
                restDayComboBox,
                chooseDirectoryButton,
                directoryLabel,
                generateScheduleButton,
                showEmployeesButton,
                generateEmptyScheduleButton
        );
        tab.setContent(vbox);
        return tab;
    }

    private void showEmployeesAndOvertime() {
        Stage employeeOvertimeStage = new Stage();
        employeeOvertimeStage.setTitle("Employees and Overtime Hours");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        for (Employee employee : employeeList) {
            String employeeInfo = employee.getName() + ": " + employee.getOverTimeHours() + " hours overtime";
            vbox.getChildren().add(new Label(employeeInfo));
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 400, 300);
        employeeOvertimeStage.setScene(scene);
        employeeOvertimeStage.show();
    }

    private void showEmployeeList(Stage primaryStage) {
        Stage employeeListStage = new Stage();
        employeeListStage.setTitle("Employee List");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        for (Employee employee : employeeList) {
            HBox employeeRow = new HBox(10);
            Label employeeLabel = new Label(employee.getName());

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                try {
                    SQLQueries.deleteEmployee(connection, employee.getEmployeeId());
                    employeeList.remove(employee);
                    vbox.getChildren().remove(employeeRow);
                } catch (SQLException ex) {
                    showAlert("Deletion Error", "Failed to delete the employee.", Alert.AlertType.ERROR);
                    ex.printStackTrace();
                }
            });

            employeeRow.getChildren().addAll(employeeLabel, deleteButton);
            vbox.getChildren().add(employeeRow);
        }

        Button deleteAllButton = new Button("Delete All Employees");
        deleteAllButton.setOnAction(e -> {
            try {
                for (Employee employee : new ArrayList<>(employeeList)) {
                    SQLQueries.deleteEmployee(connection, employee.getEmployeeId());
                }
                employeeList.clear();
                vbox.getChildren().clear();
                vbox.getChildren().add(deleteAllButton);

                // Reset calendar when all employees are deleted
                calendar = null;
            } catch (SQLException ex) {
                showAlert("Deletion Error", "Failed to delete all employees.", Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        vbox.getChildren().add(deleteAllButton);

        Scene scene = new Scene(scrollPane, 400, 300);
        employeeListStage.setScene(scene);
        employeeListStage.initOwner(primaryStage);
        employeeListStage.show();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Zeigt den aktuellen Schichtplan in einer Tabelle an.
     */
    private void showScheduleTable() {
        if (calendar == null) {
            showAlert("Error", "No schedule available.", Alert.AlertType.ERROR);
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Current Schedule");

        TableView<Map<String, String>> table = new TableView<>();

        // Spalten erstellen
        TableColumn<Map<String, String>, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("date")));

        TableColumn<Map<String, String>, String> morningColumn = new TableColumn<>("Morning");
        morningColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("morning")));

        TableColumn<Map<String, String>, String> afternoonColumn = new TableColumn<>("Afternoon");
        afternoonColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("afternoon")));

        table.getColumns().addAll(dateColumn, morningColumn, afternoonColumn);

        // Daten hinzufügen
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList();
        // Hier müssen die Daten aus dem calendar-Objekt geholt und in die TableView eingefügt werden

        VBox vbox = new VBox(10);
        vbox.getChildren().add(table);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DBUtils.closeConnection(connection);
    }
}