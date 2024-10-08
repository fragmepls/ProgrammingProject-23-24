package it.scheduleplanner.gui;

import it.scheduleplanner.dbutils.DBUtils;
import it.scheduleplanner.dbutils.SQLQueries;
import it.scheduleplanner.export.Export;
import it.scheduleplanner.export.Import;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.planner.InsufficientEmployeesException;
import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.utils.Vacation;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * Main GUI class for the Schedule Planner application.
 */
public class Gui2 extends Application {

    private Connection connection;
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private String outputDirectory;

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
        //SQLQueries.truncateDatabase(connection);

        primaryStage.setTitle("Schedule Planner"); //like the primary window

        // Main layout
        BorderPane mainLayout = new BorderPane(); //sets the layout for the primaryStage
        mainLayout.setPadding(new Insets(10, 10, 10, 10));

        // Creating Tabs
        TabPane tabPane = new TabPane(); //allows to create this "tab-layout"

        //tabs get created here
        Tab welcomeTab = welcomeTab();
        Tab employeeTab = createEmployeeTab(primaryStage);
        Tab vacationTab = createVacationTab();
        Tab importScheduleTab = createImportTab();
        Tab scheduleTab = createScheduleTab(primaryStage);

        tabPane.getTabs().addAll(welcomeTab, employeeTab, vacationTab, importScheduleTab, scheduleTab);
        mainLayout.setCenter(tabPane);

        Scene scene = new Scene(mainLayout, 800, 600); //main scene = a container that has all the elements printed in it
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
     * Displays the list of employees in a new window.
     *
     * @param primaryStage The primary stage.
     */
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
                // Delete all employees from the database
                for (Employee employee : new ArrayList<>(employeeList)) {
                    SQLQueries.deleteEmployee(connection, employee.getEmployeeId());
                }
                employeeList.clear();
                vbox.getChildren().clear();
                vbox.getChildren().add(deleteAllButton);  // Re-add the Delete All button
            } catch (SQLException ex) {
                showAlert("Deletion Error", "Failed to delete all employees.", Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        vbox.getChildren().add(deleteAllButton);  // Add the Delete All button at the bottom

        Scene scene = new Scene(scrollPane, 400, 300);
        employeeListStage.setScene(scene);
        employeeListStage.initOwner(primaryStage);
        employeeListStage.show();
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
                } else {
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
                } else {
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
                } else {
                    List<Vacation> vacations = SQLQueries.selectVacation(connection, employee.getEmployeeId());
                    if (vacations.isEmpty()) {
                        showAlert("No Vacations Found", "No vacations were found for the selected employee.", Alert.AlertType.INFORMATION);
                    } else {
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
            } else {
                String pathToFile = fileLabel.getText().substring(6); // remove "File: " prefix
                ShiftScheduleInterface importedSchedule = Import.importSchedule(pathToFile, ScheduleCreator.employeeSet);
                if (importedSchedule != null) {
                    showAlert("Success", "Schedule imported successfully.", Alert.AlertType.INFORMATION);
                    // Update the GUI or data model to reflect the imported schedule
                } else {
                    showAlert("Import Error", "Failed to import schedule. Please check the file and try again.", Alert.AlertType.ERROR);
                }
            }
        });

        vbox.getChildren().addAll(instructionLabel, chooseFileButton, fileLabel, importButton);
        tab.setContent(vbox);
        return tab;
    }


    /**
     * Creates the Schedule Configuration tab.
     *
     * @param primaryStage The primary stage.
     * @return The Schedule Configuration tab.
     */
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

                ShiftScheduleInterface calendar = ScheduleCreator.create(beginDate, endDate, numberOfEmployeesPerDay, weekendOpen, restDayEnum);

                if (outputDirectory != null) {
                    // Export the schedule to a CSV file
                    Map<Boolean, String> exportResult = Export.CSVExport(calendar, ScheduleCreator.employeeSet, outputDirectory);

                    if (exportResult.containsKey(true) && exportResult.get(true) != null) {
                        showAlert("Success", "Schedule exported successfully to: " + exportResult.get(true), Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to export schedule. Please check the output directory and try again.", Alert.AlertType.ERROR);
                    }

                    // Optionally export employee data as well
                    Export.employeeExport(ScheduleCreator.employeeSet, outputDirectory);
                } else {
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

        // Button to show employees and overtime hours
        Button showEmployeesButton = new Button("Check overtime hours");



        showEmployeesButton.setOnAction(e -> showEmployeesAndOvertime());

        // New Button to generate empty schedule
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
                // Assuming employeeSet is the set of employees to be passed
                Set<Employee> employeeSet = ScheduleCreator.employeeSet;
                Map<Boolean, String> exportResult = Export.exportBlankSchedule(beginDate, endDate, employeeSet, outputDirectory);

                if (exportResult.containsKey(true) && exportResult.get(true) != null) {
                    showAlert("Success", "Empty schedule exported successfully to: " + exportResult.get(true), Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to export empty schedule. Please check the output directory and try again.", Alert.AlertType.ERROR);
                }
            } else {
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
                showEmployeesButton
        );
        tab.setContent(vbox);
        return tab;
    }

    /**
     * Shows a new window displaying employees and their overtime hours.
     */
    private void showEmployeesAndOvertime() {
        Stage employeeOvertimeStage = new Stage();
        employeeOvertimeStage.setTitle("Employees and Overtime Hours");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        // Example logic to print employees and overtime hours
        for (Employee employee : employeeList) {
            String employeeInfo = employee.getName() + ": " + employee.getOverTimeHours() + " hours overtime";
            vbox.getChildren().add(new Label(employeeInfo));
        }

        // Create a ScrollPane and set VBox as its content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true); // Enable horizontal scroll if needed
        scrollPane.setFitToHeight(true); // Enable vertical scroll

        Scene scene = new Scene(scrollPane, 400, 300); // Adjust size as needed
        employeeOvertimeStage.setScene(scene);
        employeeOvertimeStage.show();
    }


    /**
     * Displays an alert with a given title and message.
     *
     * @param title   The title of the alert.
     * @param message The message of the alert.
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Stops the application and closes the database connection.
     *
     * @throws Exception If an error occurs.
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        DBUtils.closeConnection(connection);
    }
}
