package it.scheduleplanner.gui;

import it.scheduleplanner.dbutils.DBUtils;
import it.scheduleplanner.dbutils.SQLQueries;
import it.scheduleplanner.export.Export;
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
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main GUI class for the Schedule Planner application.
 */
public class Gui2 extends Application {

    private Connection connection;
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList(); //oslist has advantages in ui: like automatic updating in interface when list changes eg.
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
        Tab scheduleTab = createScheduleTab(primaryStage);

        tabPane.getTabs().addAll(welcomeTab, employeeTab, vacationTab, scheduleTab);
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

        /*
        -> Events happen in JavaFX everytime when something changes, or should change - eg. when a user clicks a button.
        -> When that happens the EventHandler appears.
        -> EventHandler is a functional Interface in JavaFX that defines the single method handle.
           (functional Interfaces in Java are Interfaces that have exactly one (abstract) method)
        -> The handle method takes an Event, or a subclass of Event, such as ActionEvent (used here,
           usually used when the event occurs when the Event is triggered by a user action) as an argument.
        -> button.setOnAction() is a method provided by JavaFX to register an EventHandler (implemented here as a lambda expression)
           that will be called when the button is clicked.
         */

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
                freeDayComboBox, fullTimeCheckBox, addButton, importButton, viewEmployeesButton
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
            vbox.getChildren().add(new Label(employee.getName()));
        }

        Scene scene = new Scene(vbox, 400, 300);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true); // Enable horizontal scroll if needed
        scrollPane.setFitToHeight(true); // Enable vertical scroll

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

        /*
        -> Events happen in JavaFX everytime when something changes, or should change - eg. when a user clicks a button.
        -> When that happens the EventHandler appears.
        -> EventHandler is a functional Interface in JavaFX that defines the single method handle.
           (functional Interfaces in Java are Interfaces that have exactly one (abstract) method)
        -> The handle method takes an Event, or a subclass of Event, such as ActionEvent (used here,
           usually used when the event occurs when the Event is triggered by a user action) as an argument.
        -> button.setOnAction() is a method provided by JavaFX to register an EventHandler (implemented here as a lambda expression)
           that will be called when the button is clicked.
         */


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

        vbox.getChildren().addAll(new Label("Enter Vacation Details:"), employeeComboBox, startDatePicker, endDatePicker, addVacationButton);
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
        restDayComboBox.getItems().addAll(Arrays.stream(DayOfWeek.values()) //DayOfWeek. values returns an array of the ENUM (MONDAY, TUESDAY...)
                //Arrays.stream makes out of the array a stream
                .map(DayOfWeek::toString)                                   //.map is a method of streams that transforms each element of the stream --> here the DayofWeak to String)
                .toList());                                                 //is a operation of stream, used to store all elements of the stream as an array.

        //could also be done without stream, but with stream better transformation and better readability

        restDayComboBox.setPromptText("Select Rest Day");

        Button chooseDirectoryButton = new Button("Choose Output Directory");
        Label directoryLabel = new Label("No directory chosen");

        /*
        -> Events happen in JavaFX everytime when something changes, or should change - eg. when a user clicks a button.
        -> When that happens the EventHandler appears.
        -> EventHandler is a functional Interface in JavaFX that defines the single method handle.
           (functional Interfaces in Java are Interfaces that have exactly one (abstract) method)
        -> The handle method takes an Event, or a subclass of Event, such as ActionEvent (used here,
           usually used when the event occurs when the Event is triggered by a user action) as an argument.
        -> button.setOnAction() is a method provided by JavaFX to register an EventHandler (implemented here as a lambda expression)
           that will be called when the button is clicked.
         */

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
        /*
        -> Events happen in JavaFX everytime when something changes, or should change - eg. when a user clicks a button.
        -> When that happens the EventHandler appears.
        -> EventHandler is a functional Interface in JavaFX that defines the single method handle.
           (functional Interfaces in Java are Interfaces that have exactly one (abstract) method)
        -> The handle method takes an Event, or a subclass of Event, such as ActionEvent (used here,
           usually used when the event occurs when the Event is triggered by a user action) as an argument.
        -> button.setOnAction() is a method provided by JavaFX to register an EventHandler (implemented here as a lambda expression)
           that will be called when the button is clicked.
         */

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
                    //Export.CSVExport(calendar, outputDirectory);
                    Export.employeeExport(ScheduleCreator.employeeSet, outputDirectory);
                    showAlert("Success", "Schedule exported successfully.", Alert.AlertType.INFORMATION);
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

        /*
        -> Events happen in JavaFX everytime when something changes, or should change - eg. when a user clicks a button.
        -> When that happens the EventHandler appears.
        -> EventHandler is a functional Interface in JavaFX that defines the single method handle.
           (functional Interfaces in Java are Interfaces that have exactly one (abstract) method)
        -> The handle method takes an Event, or a subclass of Event, such as ActionEvent (used here,
           usually used when the event occurs when the Event is triggered by a user action) as an argument.
        -> button.setOnAction() is a method provided by JavaFX to register an EventHandler (implemented here as a lambda expression)
           that will be called when the button is clicked.
         */

        showEmployeesButton.setOnAction(e -> showEmployeesAndOvertime());

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
