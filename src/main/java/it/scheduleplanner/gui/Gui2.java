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
import java.util.Optional;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.sql.*;
import java.time.*;
import java.util.*;

public class Gui2 extends Application {

    private Connection connection;
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private String outputDirectory;
    private ShiftScheduleInterface calendar;

    public static void main(String[] args) {
        try {
            System.out.println("Starting Schedule Planner application...");
            launch(args);
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Initializing application components...");
            initializeDatabase();
            initializeGUI(primaryStage);
        } catch (Exception e) {
            System.err.println("Error during application startup: " + e.getMessage());
            e.printStackTrace();
            showAlert("Startup Error", "Failed to start application: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void initializeDatabase() throws SQLException {
        System.out.println("Initializing database connection...");
        DBUtils.initializeDatabase();
        connection = DBUtils.getConnection();
        System.out.println("Database connection established successfully");
    }

    private void initializeGUI(Stage primaryStage) {
        System.out.println("Setting up main window...");
        primaryStage.setTitle("Schedule Planner");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        TabPane tabPane = new TabPane();
        setupTabs(tabPane, primaryStage);

        mainLayout.setCenter(tabPane);
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);

        System.out.println("Showing main window...");
        primaryStage.show();
    }

    private void setupTabs(TabPane tabPane, Stage primaryStage) {
        System.out.println("Creating application tabs...");
        tabPane.getTabs().addAll(
                welcomeTab(),
                createEmployeeTab(primaryStage),
                createVacationTab(),
                createImportTab(),
                createScheduleTab(primaryStage),
                createShiftModificationTab()
        );
    }

    private Tab welcomeTab() {
        System.out.println("Creating welcome tab...");
        Tab tab = new Tab("Welcome to Schedule Creator");
        tab.setClosable(false);

        Label welcomeLabel = new Label("Welcome to Schedule Creator");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label(getWelcomeText());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 14px;");

        ScrollPane scrollPane = new ScrollPane(descriptionLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(600, 400);

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(welcomeLabel, scrollPane);
        vbox.setAlignment(Pos.CENTER);

        tab.setContent(vbox);
        return tab;
    }

    private String getWelcomeText() {
        return "Schedule Creator is a tool designed to help you manage employee schedules effectively.\n\n" +
                "You can add employees, schedule their vacations, configure work schedules, and generate shift calendars.\n" +
                "The final schedule is exported as a CSV file, which can be opened in Microsoft Excel, Apple Numbers, or similar software.\n\n" +
                "Add Employee Tab:\n" +
                "- Employee Name: Enter the name of the employee.\n" +
                "- Is Weekend Worker: Check this box if the employee works on weekends.\n" +
                "- Select a Fixed Free Day: Choose a day of the week when the employee is free.\n" +
                "- Is Full Time: Check this box if the employee works full time.\n\n" +
                "Send Employee on Vacation Tab:\n" +
                "- Employee: Select the employee from the dropdown list.\n" +
                "- Start Date: Choose the start date of the vacation.\n" +
                "- End Date: Choose the end date of the vacation.\n\n" +
                "Schedule Configuration Tab:\n" +
                "- Number of Employees per Day: Enter the required number of employees for each day.\n" +
                "- Begin Date: Select the start date for the schedule.\n" +
                "- End Date: Select the end date for the schedule.\n" +
                "- Open on Weekends: Check this box if the business operates on weekends.\n" +
                "- Select Rest Day: Choose a day of the week when the business is closed, or select \"NO DAY\" if there is no fixed rest day.\n" +
                "- Choose Output Directory: Click the button to select the directory where the schedule will be saved.";
    }

    private Tab createEmployeeTab(Stage primaryStage) {
        System.out.println("Creating employee management tab...");
        Tab tab = new Tab("Add Employee");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        CheckBox weekendWorkerCheckBox = new CheckBox("Is Weekend Worker");

        ComboBox<String> freeDayComboBox = new ComboBox<>();
        freeDayComboBox.getItems().addAll(Arrays.stream(DayOfWeek.values())
                .map(DayOfWeek::toString)
                .toList());

        CheckBox fullTimeCheckBox = new CheckBox("Is Full Time");

        Button addButton = new Button("Add Employee");
        addButton.setOnAction(event -> handleAddEmployee(
                nameField, weekendWorkerCheckBox, freeDayComboBox, fullTimeCheckBox));

        Button importButton = new Button("Import Employees from Database");
        importButton.setOnAction(e -> handleImportEmployees());

        Button viewEmployeesButton = new Button("View Employees");
        viewEmployeesButton.setOnAction(e -> showEmployeeList(primaryStage));

        vbox.getChildren().addAll(
                new Label("Enter Employee Name:"), nameField,
                weekendWorkerCheckBox,
                new Label("Select a fixed free Day:"), freeDayComboBox,
                fullTimeCheckBox,
                addButton,
                importButton,
                viewEmployeesButton
        );

        tab.setContent(vbox);
        return tab;
    }

    private void handleAddEmployee(TextField nameField, CheckBox weekendWorkerCheckBox,
                                   ComboBox<String> freeDayComboBox, CheckBox fullTimeCheckBox) {
        try {
            String name = nameField.getText();
            boolean isWeekendWorker = weekendWorkerCheckBox.isSelected();
            String freeDay = freeDayComboBox.getValue();
            boolean isFullTime = fullTimeCheckBox.isSelected();

            if (name.isEmpty() || freeDay == null) {
                showAlert("Input Error", "Please fill in all required fields.", Alert.AlertType.ERROR);
                return;
            }

            Employee employee = new Employee(name, isWeekendWorker, freeDay, isFullTime);
            int id = SQLQueries.insertEmployee(connection, employee);
            employee.setEmployeeId(id);
            employeeList.add(employee);

            // Clear fields
            nameField.clear();
            weekendWorkerCheckBox.setSelected(false);
            fullTimeCheckBox.setSelected(false);
            freeDayComboBox.setValue(null);

            showAlert("Success", "Employee added successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            System.err.println("Error adding employee: " + ex.getMessage());
            showAlert("Database Error", "Failed to add employee: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Tab createVacationTab() {
        System.out.println("Creating vacation management tab...");
        Tab tab = new Tab("Send Employee on Vacation");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        ComboBox<Employee> employeeComboBox = new ComboBox<>(employeeList);
        setupEmployeeComboBox(employeeComboBox);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        Button addVacationButton = new Button("Add Vacation");
        addVacationButton.setOnAction(e -> handleAddVacation(
                employeeComboBox, startDatePicker, endDatePicker));

        Button importVacationsButton = new Button("Import Vacation for Employee");
        importVacationsButton.setOnAction(e -> handleImportVacations(employeeComboBox));

        vbox.getChildren().addAll(
                new Label("Select Employee:"), employeeComboBox,
                new Label("Start Date:"), startDatePicker,
                new Label("End Date:"), endDatePicker,
                addVacationButton,
                importVacationsButton
        );

        tab.setContent(vbox);
        return tab;
    }

    private void setupEmployeeComboBox(ComboBox<Employee> comboBox) {
        comboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                setText(empty || employee == null ? null : employee.getName());
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                setText(empty || employee == null ? null : employee.getName());
            }
        });
    }

    private void handleAddVacation(ComboBox<Employee> employeeComboBox,
                                   DatePicker startDatePicker,
                                   DatePicker endDatePicker) {
        try {
            Employee employee = employeeComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (employee == null || startDate == null || endDate == null) {
                showAlert("Input Error", "Please fill in all required fields.", Alert.AlertType.ERROR);
                return;
            }

            if (startDate.isAfter(endDate)) {
                showAlert("Input Error", "Start date cannot be after end date.", Alert.AlertType.ERROR);
                return;
            }

            Vacation vacation = new Vacation(startDate, endDate);
            employee.addVacation(vacation);
            SQLQueries.insertVacation(connection, employee.getEmployeeId(),
                    startDate.toString(), endDate.toString());

            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            showAlert("Success", "Vacation added successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            System.err.println("Error adding vacation: " + ex.getMessage());
            showAlert("Database Error", "Failed to add vacation: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Tab createImportTab() {
        System.out.println("Creating import tab...");
        Tab tab = new Tab("Import Schedule");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label instructionLabel = new Label("Select a CSV file to import schedule:");
        Label fileLabel = new Label("No file selected");

        Button chooseFileButton = new Button("Choose File");
        chooseFileButton.setOnAction(e -> handleChooseFile(fileLabel));

        Button importButton = new Button("Import Schedule");
        importButton.setOnAction(e -> handleImportSchedule(fileLabel));

        vbox.getChildren().addAll(
                instructionLabel,
                chooseFileButton,
                fileLabel,
                importButton
        );

        tab.setContent(vbox);
        return tab;
    }

    private void handleChooseFile(Label fileLabel) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            fileLabel.setText("File: " + selectedFile.getAbsolutePath());
        }
    }

    private void handleImportSchedule(Label fileLabel) {
        if (fileLabel.getText().equals("No file selected")) {
            showAlert("File Error", "Please select a file to import.", Alert.AlertType.ERROR);
            return;
        }

        String pathToFile = fileLabel.getText().substring(6);
        ShiftScheduleInterface importedSchedule = Import.importSchedule(pathToFile,
                ScheduleCreator.employeeSet);
        if (importedSchedule != null) {
            showAlert("Success", "Schedule imported successfully.", Alert.AlertType.INFORMATION);
            calendar = importedSchedule;
        } else {
            showAlert("Import Error", "Failed to import schedule.", Alert.AlertType.ERROR);
        }
    }

    private Tab createScheduleTab(Stage primaryStage) {
        System.out.println("Creating schedule configuration tab...");
        Tab tab = new Tab("Schedule Configuration");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

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

        Label directoryLabel = new Label("No directory chosen");
        Button chooseDirectoryButton = new Button("Choose Output Directory");
        chooseDirectoryButton.setOnAction(e -> handleChooseDirectory(primaryStage, directoryLabel));

        Button generateScheduleButton = new Button("Generate Schedule");
        generateScheduleButton.setOnAction(e -> handleGenerateSchedule(
                employeesPerDayField, beginDatePicker, endDatePicker,
                weekendOpenCheckBox, restDayComboBox));

        Button showEmployeesButton = new Button("Check overtime hours");
        showEmployeesButton.setOnAction(e -> showEmployeesAndOvertime());

        Button generateEmptyScheduleButton = new Button("Generate Empty Schedule");
        generateEmptyScheduleButton.setOnAction(e -> handleGenerateEmptySchedule(
                beginDatePicker, endDatePicker));

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

    private void handleChooseDirectory(Stage primaryStage, Label directoryLabel) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Output Directory");
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            outputDirectory = selectedDirectory.getAbsolutePath();
            directoryLabel.setText("Output Directory: " + outputDirectory);
        }
    }

    private void handleGenerateSchedule(TextField employeesPerDayField,
                                        DatePicker beginDatePicker,
                                        DatePicker endDatePicker,
                                        CheckBox weekendOpenCheckBox,
                                        ComboBox<String> restDayComboBox) {
        try {
            int numberOfEmployeesPerDay = Integer.parseInt(employeesPerDayField.getText());
            LocalDate beginDate = beginDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            boolean weekendOpen = weekendOpenCheckBox.isSelected();
            String restDay = restDayComboBox.getValue();

            if (beginDate == null || endDate == null || restDay == null) {
                showAlert("Input Error", "Please fill in all required fields.", Alert.AlertType.ERROR);
                return;
            }

            if (beginDate.isAfter(endDate)) {
                showAlert("Input Error", "Begin date cannot be after end date.", Alert.AlertType.ERROR);
                return;
            }

            DayOfWeek restDayEnum = "NO DAY".equals(restDay) ? null : DayOfWeek.valueOf(restDay.toUpperCase());

            ArrayList<Employee> arrayList = new ArrayList<>(employeeList);
            ScheduleCreator.addEmployeeList(arrayList);

            calendar = ScheduleCreator.create(beginDate, endDate, numberOfEmployeesPerDay,
                    weekendOpen, restDayEnum);

            if (outputDirectory != null) {
                exportSchedule(calendar);
            } else {
                showAlert("Warning", "No output directory selected.", Alert.AlertType.WARNING);
            }

        } catch (NumberFormatException ex) {
            showAlert("Input Error", "Number of Employees per Day must be a valid number.",
                    Alert.AlertType.ERROR);
        } catch (InsufficientEmployeesException ex) {
            showAlert("Scheduling Error",
                    "Not enough employees available. Please add more employees or decrease the required number.",
                    Alert.AlertType.ERROR);
        } catch (Exception ex) {
            System.err.println("Error generating schedule: " + ex.getMessage());
            showAlert("Error", "Failed to generate schedule: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void handleGenerateEmptySchedule(DatePicker beginDatePicker, DatePicker endDatePicker) {
        LocalDate beginDate = beginDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (beginDate == null || endDate == null) {
            showAlert("Input Error", "Please select both start and end dates.", Alert.AlertType.ERROR);
            return;
        }

        if (beginDate.isAfter(endDate)) {
            showAlert("Input Error", "Begin date cannot be after end date.", Alert.AlertType.ERROR);
            return;
        }

        if (outputDirectory != null) {
            Set<Employee> employeeSet = ScheduleCreator.employeeSet;
            Map<Boolean, String> exportResult = Export.exportBlankSchedule(beginDate, endDate,
                    employeeSet, outputDirectory);

            if (exportResult.containsKey(true) && exportResult.get(true) != null) {
                showAlert("Success",
                        "Empty schedule exported to: " + exportResult.get(true),
                        Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to export empty schedule.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "No output directory selected.", Alert.AlertType.ERROR);
        }
    }

    private void exportSchedule(ShiftScheduleInterface schedule) {
        Map<Boolean, String> exportResult = Export.CSVExport(schedule,
                ScheduleCreator.employeeSet,
                outputDirectory);

        if (exportResult.containsKey(true) && exportResult.get(true) != null) {
            showAlert("Success",
                    "Schedule exported successfully to: " + exportResult.get(true),
                    Alert.AlertType.INFORMATION);
            Export.employeeExport(ScheduleCreator.employeeSet, outputDirectory);
        } else {
            showAlert("Error", "Failed to export schedule.", Alert.AlertType.ERROR);
        }
    }
    private Tab createShiftModificationTab() {
        System.out.println("Creating shift modification tab...");
        Tab tab = new Tab("Modify Shifts");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

        ComboBox<Employee> employeeComboBox = new ComboBox<>(employeeList);
        setupEmployeeComboBox(employeeComboBox);

        ComboBox<Shift> shiftComboBox = new ComboBox<>();
        shiftComboBox.getItems().addAll(Shift.MORNING, Shift.AFTERNOON, Shift.FULL);

        TextArea currentScheduleArea = new TextArea();
        currentScheduleArea.setEditable(false);
        currentScheduleArea.setPrefRowCount(5);

        Button viewCurrentButton = new Button("View Current Schedule");
        viewCurrentButton.setOnAction(e -> handleViewCurrentSchedule(datePicker, currentScheduleArea));

        Button modifyButton = new Button("Modify Shift");
        modifyButton.setOnAction(e -> handleModifyShift(datePicker, employeeComboBox,
                shiftComboBox, viewCurrentButton));

        // Neuer Delete Button
        Button deleteShiftButton = new Button("Delete Shift");
        deleteShiftButton.setOnAction(e -> handleDeleteShift(datePicker, employeeComboBox, viewCurrentButton));

        // Shift swap section
        Label swapLabel = new Label("Swap Shifts Between Employees");
        swapLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");

        ComboBox<Employee> employee1ComboBox = new ComboBox<>(employeeList);
        ComboBox<Employee> employee2ComboBox = new ComboBox<>(employeeList);
        setupEmployeeComboBox(employee1ComboBox);
        setupEmployeeComboBox(employee2ComboBox);

        Button swapButton = new Button("Swap Shifts");
        swapButton.setOnAction(e -> handleSwapShifts(datePicker, employee1ComboBox,
                employee2ComboBox, viewCurrentButton));

        vbox.getChildren().addAll(
                new Label("Select Date:"), datePicker,
                new Label("Current Schedule:"), currentScheduleArea,
                viewCurrentButton,
                new Separator(),
                new Label("Modify Individual Shift:"),
                employeeComboBox,
                shiftComboBox,
                modifyButton,
                deleteShiftButton,  // Neuer Button
                new Separator(),
                swapLabel,
                new Label("First Employee:"), employee1ComboBox,
                new Label("Second Employee:"), employee2ComboBox,
                swapButton
        );

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        return tab;
    }

    private void handleViewCurrentSchedule(DatePicker datePicker, TextArea currentScheduleArea) {
        if (calendar == null || datePicker.getValue() == null) {
            showAlert("Error", "Please select a date and ensure a schedule exists.",
                    Alert.AlertType.ERROR);
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
    }

    private void handleModifyShift(DatePicker datePicker,
                                   ComboBox<Employee> employeeComboBox,
                                   ComboBox<Shift> shiftComboBox,
                                   Button viewCurrentButton) {
        LocalDate date = datePicker.getValue();
        Employee employee = employeeComboBox.getValue();
        Shift newShift = shiftComboBox.getValue();

        if (date == null || employee == null || newShift == null) {
            showAlert("Input Error", "Please select date, employee, and shift.", Alert.AlertType.ERROR);
            return;
        }

        if (calendar == null) {
            showAlert("Error", "No schedule available.", Alert.AlertType.ERROR);
            return;
        }

        if (!ShiftModifier.isModificationAllowed(employee, newShift)) {
            showAlert("Error", "Employee doesn't have enough working hours.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Create a temporary copy of the day to check coverage
            ShiftDayInterface currentDay = calendar.getDay(date);
            ShiftDayInterface tempDay = new FixedShiftDay();

            // Copy all assignments except the one being modified
            for (Map.Entry<Employee, Shift> entry : currentDay.getEmployees().entrySet()) {
                if (!entry.getKey().equals(employee)) {
                    tempDay.addEmployee(entry.getKey(), entry.getValue());
                }
            }
            // Add the new shift
            tempDay.addEmployee(employee, newShift);

            // Check if this would result in insufficient coverage
            if (!hasRequiredCoverage(tempDay, 2)) { // Replace 2 with your required number of employees
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Insufficient Coverage");
                confirm.setHeaderText("Warning: Shift Change Will Result in Insufficient Coverage");
                confirm.setContentText("This modification will result in some time slots having fewer employees than required. Do you want to proceed?");

                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    return;
                }
            }

            // Proceed with modification
            if (ShiftModifier.modifyShift(calendar, date, employee, newShift)) {
                showAlert("Success", "Shift modified successfully.", Alert.AlertType.INFORMATION);
                viewCurrentButton.fire();
                updateScheduleDisplay();
            } else {
                showAlert("Error", "Failed to modify shift.", Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            showAlert("Error", "An error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleDeleteShift(DatePicker datePicker, ComboBox<Employee> employeeComboBox, Button viewCurrentButton) {
        LocalDate date = datePicker.getValue();
        Employee employee = employeeComboBox.getValue();

        if (date == null || employee == null) {
            showAlert("Input Error", "Please select date and employee.", Alert.AlertType.ERROR);
            return;
        }

        if (calendar == null) {
            showAlert("Error", "No schedule available.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Create a temporary copy of the day to check coverage
            ShiftDayInterface currentDay = calendar.getDay(date);
            ShiftDayInterface tempDay = new FixedShiftDay();

            // Copy all assignments except the one being deleted
            for (Map.Entry<Employee, Shift> entry : currentDay.getEmployees().entrySet()) {
                if (!entry.getKey().equals(employee)) {
                    tempDay.addEmployee(entry.getKey(), entry.getValue());
                }
            }

            // Check if this would result in insufficient coverage
            if (!hasRequiredCoverage(tempDay, 2)) { // 2 ist die Mindestanzahl an Mitarbeitern
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Insufficient Coverage Warning");
                confirm.setHeaderText("Warning: Deleting this shift will result in insufficient coverage");
                confirm.setContentText("This deletion will result in some time slots having fewer employees than required. Do you want to proceed?");

                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    return;
                }
            }

            // Proceed with deletion
            if (ShiftModifier.modifyShift(calendar, date, employee, null)) {
                showAlert("Success", "Shift deleted successfully.", Alert.AlertType.INFORMATION);
                viewCurrentButton.fire(); // Aktualisiere die Anzeige
                updateScheduleDisplay();
            } else {
                showAlert("Error", "Failed to delete shift.", Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            showAlert("Error", "An error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleSwapShifts(DatePicker datePicker,
                                  ComboBox<Employee> employee1ComboBox,
                                  ComboBox<Employee> employee2ComboBox,
                                  Button viewCurrentButton) {
        LocalDate date = datePicker.getValue();
        Employee emp1 = employee1ComboBox.getValue();
        Employee emp2 = employee2ComboBox.getValue();

        if (date == null || emp1 == null || emp2 == null) {
            showAlert("Input Error", "Please select date and both employees.", Alert.AlertType.ERROR);
            return;
        }

        if (calendar == null) {
            showAlert("Error", "No schedule available.", Alert.AlertType.ERROR);
            return;
        }

        try {
            if (ShiftModifier.swapShifts(calendar, date, emp1, emp2)) {
                showAlert("Success", "Shifts swapped successfully.", Alert.AlertType.INFORMATION);
                viewCurrentButton.fire();
                updateScheduleDisplay();
            } else {
                showAlert("Error", "Failed to swap shifts.", Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            showAlert("Error", "An error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean hasRequiredCoverage(ShiftDayInterface day, int requiredEmployees) {
        Map<Employee, Shift> assignments = day.getEmployees();
        int morningCount = 0;
        int afternoonCount = 0;

        // Count employees for each time slot
        for (Shift shift : assignments.values()) {
            switch (shift) {
                case MORNING:
                    morningCount++;
                    break;
                case AFTERNOON:
                    afternoonCount++;
                    break;
                case FULL:
                    morningCount++;
                    afternoonCount++;
                    break;
            }
        }

        return morningCount >= requiredEmployees && afternoonCount >= requiredEmployees;
    }

    private void updateScheduleDisplay() {
        if (outputDirectory != null && calendar != null) {
            exportSchedule(calendar);
        }
    }

    private void handleImportEmployees() {
        try {
            employeeList.addAll(SQLQueries.selectAllEmployees(connection));
            showAlert("Success", "Employees imported successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            System.err.println("Error importing employees: " + ex.getMessage());
            showAlert("Database Error", "Failed to import employees: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void handleImportVacations(ComboBox<Employee> employeeComboBox) {
        try {
            Employee employee = employeeComboBox.getValue();
            if (employee == null) {
                showAlert("Input Error", "Please select an employee.", Alert.AlertType.ERROR);
                return;
            }

            List<Vacation> vacations = SQLQueries.selectVacation(connection, employee.getEmployeeId());
            if (vacations.isEmpty()) {
                showAlert("No Vacations Found",
                        "No vacations found for the selected employee.",
                        Alert.AlertType.INFORMATION);
            } else {
                for (Vacation vacation : vacations) {
                    employee.addVacation(vacation);
                }
                showAlert("Success", "Vacations imported successfully.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException ex) {
            System.err.println("Error importing vacations: " + ex.getMessage());
            showAlert("Database Error", "Failed to import vacations: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void showEmployeesAndOvertime() {
        Stage stage = new Stage();
        stage.setTitle("Employees and Overtime Hours");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        for (Employee employee : employeeList) {
            String info = employee.getName() + ": " + employee.getOverTimeHours() + " hours overtime";
            vbox.getChildren().add(new Label(info));
        }

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void showEmployeeList(Stage primaryStage) {
        Stage stage = new Stage();
        stage.setTitle("Employee List");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        for (Employee employee : employeeList) {
            HBox row = new HBox(10);
            Label nameLabel = new Label(employee.getName());
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> handleDeleteEmployee(employee, row, vbox));
            row.getChildren().addAll(nameLabel, deleteButton);
            vbox.getChildren().add(row);
        }

        Button deleteAllButton = new Button("Delete All Employees");
        deleteAllButton.setOnAction(e -> handleDeleteAllEmployees(vbox, deleteAllButton));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        vbox.getChildren().add(deleteAllButton);

        Scene scene = new Scene(scrollPane, 400, 300);
        stage.setScene(scene);
        stage.initOwner(primaryStage);
        stage.show();
    }

    private void handleDeleteEmployee(Employee employee, HBox row, VBox vbox) {
        try {
            SQLQueries.deleteEmployee(connection, employee.getEmployeeId());
            employeeList.remove(employee);
            vbox.getChildren().remove(row);
            showAlert("Success", "Employee deleted successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            System.err.println("Error deleting employee: " + ex.getMessage());
            showAlert("Deletion Error", "Failed to delete the employee: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void handleDeleteAllEmployees(VBox vbox, Button deleteAllButton) {
        try {
            for (Employee employee : new ArrayList<>(employeeList)) {
                SQLQueries.deleteEmployee(connection, employee.getEmployeeId());
            }
            employeeList.clear();
            vbox.getChildren().clear();
            vbox.getChildren().add(deleteAllButton);
            calendar = null;
            showAlert("Success", "All employees deleted successfully.", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            System.err.println("Error deleting all employees: " + ex.getMessage());
            showAlert("Deletion Error", "Failed to delete all employees: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void stop() {
        try {
            System.out.println("Closing application and database connection...");
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed successfully");
            }
        } catch (SQLException ex) {
            System.err.println("Error closing database connection: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}