package it.scheduleplanner.gui;

import it.scheduleplanner.dbutils.DBUtils;
import it.scheduleplanner.dbutils.SQLQueries;
import it.scheduleplanner.export.Export;
import it.scheduleplanner.export.ShiftScheduleInterface;
import it.scheduleplanner.planner.ScheduleCreator;
import it.scheduleplanner.utils.Employee;
import it.scheduleplanner.utils.Vacation;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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
import java.util.List;

public class Gui2 extends Application {

    private Connection connection;
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private String outputDirectory;

    private static final List<String> VALID_DAYS = Arrays.asList(
            "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"
    );

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws SQLException {
        DBUtils.initializeDatabase();
        connection = DBUtils.getConnection();

        primaryStage.setTitle("Schedule Planner");

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10, 10, 10, 10));

        // Creating Tabs
        TabPane tabPane = new TabPane();

        Tab employeeTab = createEmployeeTab(primaryStage);
        Tab vacationTab = createVacationTab();
        Tab scheduleTab = createScheduleTab(primaryStage);

        tabPane.getTabs().addAll(employeeTab, vacationTab, scheduleTab);
        mainLayout.setCenter(tabPane);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /*
    private Tab createEmployeeTab(Stage primaryStage) {
        Tab tab = new Tab("Add Employee");
        VBox vbox = new VBox(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        CheckBox weekendWorkerCheckBox = new CheckBox("Is Weekend Worker");

        TextField freeDayField = new TextField();
        freeDayField.setPromptText("Free Day");

        CheckBox fullTimeCheckBox = new CheckBox("Is Full Time");

        Button addButton = new Button("Add Employee");
        addButton.setOnAction(e -> {
            String name = nameField.getText();
            boolean isWeekendWorker = weekendWorkerCheckBox.isSelected();
            String freeDay = freeDayField.getText();
            boolean isFullTime = fullTimeCheckBox.isSelected();

            if (name.isEmpty() || freeDay.isEmpty() || !VALID_DAYS.contains(freeDay.toLowerCase())) {
                showAlert("Input Error", "Some of the inputs are wrong, try again.");
                return;
            }

            Employee employee = new Employee(name, isWeekendWorker, freeDay, isFullTime);
            employeeList.add(employee);
            try {
                SQLQueries.insertEmployee(connection, employee);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            nameField.clear();
            weekendWorkerCheckBox.setSelected(false);
            freeDayField.clear();
            fullTimeCheckBox.setSelected(false);
        });

        Button viewEmployeesButton = new Button("View Employees");
        viewEmployeesButton.setOnAction(e -> showEmployeeList(primaryStage));


        vbox.getChildren().addAll(new Label("Enter Employee Name:"), nameField, weekendWorkerCheckBox, freeDayField, fullTimeCheckBox, addButton, viewEmployeesButton);
        tab.setContent(vbox);
        return tab;
    }


*/
    private Tab createEmployeeTab(Stage primaryStage) {
        Tab tab = new Tab("Add Employee");
        VBox vbox = new VBox(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        CheckBox weekendWorkerCheckBox = new CheckBox("Is Weekend Worker");

        TextField freeDayField = new TextField();
        freeDayField.setPromptText("Free Day");

        CheckBox fullTimeCheckBox = new CheckBox("Is Full Time");

        Button addButton = new Button("Add Employee");
        addButton.setOnAction(e -> {
            String name = nameField.getText();
            boolean isWeekendWorker = weekendWorkerCheckBox.isSelected();
            String freeDay = freeDayField.getText();
            boolean isFullTime = fullTimeCheckBox.isSelected();

            if (name.isEmpty() || freeDay.isEmpty() || !VALID_DAYS.contains(freeDay.toLowerCase())) {
                showAlert("Input Error", "Some of the inputs are wrong, try again.");
                return;
            }

            Employee employee = new Employee(name, isWeekendWorker, freeDay, isFullTime);
            employeeList.add(employee);
            try {
                SQLQueries.insertEmployee(connection, employee);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            nameField.clear();
            weekendWorkerCheckBox.setSelected(false);
            freeDayField.clear();
            fullTimeCheckBox.setSelected(false);
        });

        Button viewEmployeesButton = new Button("View Employees");
        viewEmployeesButton.setOnAction(e -> showEmployeeList(primaryStage));

        ComboBox<Employee> employeeComboBox = new ComboBox<>(employeeList);
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

        Button removeButton = new Button("Remove Employee");
        removeButton.setOnAction(e -> {
            Employee selectedEmployee = employeeComboBox.getValue();
            if (selectedEmployee != null) {
                employeeList.remove(selectedEmployee);
         /*       try {
                    SQLQueries.removeEmployee(connection, selectedEmployee);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

          */
            }
        });

        vbox.getChildren().addAll(
                new Label("Enter Employee Name:"), nameField, weekendWorkerCheckBox,
                freeDayField, fullTimeCheckBox, addButton, viewEmployeesButton,
                new Label("Select Employee to Remove:"), employeeComboBox, removeButton
        );
        tab.setContent(vbox);
        return tab;
    }

    private void showEmployeeList(Stage primaryStage) {
        Stage employeeListStage = new Stage();
        employeeListStage.setTitle("Employee List");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        for (Employee employee : employeeList) {
            vbox.getChildren().add(new Label(employee.getName()));
        }

        Scene scene = new Scene(vbox, 400, 300);
        employeeListStage.setScene(scene);
        employeeListStage.initOwner(primaryStage);
        employeeListStage.show();
    }

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
        addVacationButton.setOnAction(e -> {
            Employee employee = employeeComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (employee == null || startDate == null || endDate == null) {
                showAlert("Input Error", "Some of the inputs are wrong, try again.");
                return;
            }

            Vacation vacation = new Vacation(startDate, endDate);
            employee.addVacation(vacation);

            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        });

        vbox.getChildren().addAll(new Label("Enter Vacation Details:"), employeeComboBox, startDatePicker, endDatePicker, addVacationButton);
        tab.setContent(vbox);
        return tab;
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

                if (beginDate == null || endDate == null) {
                    showAlert("Input Error", "Some of the inputs are wrong, try again.");
                    return;
                }

                ScheduleCreator.addEmployeeList((ArrayList<Employee>) employeeList);

                ShiftScheduleInterface calendar = ScheduleCreator.create(beginDate, endDate, numberOfEmployeesPerDay, false, DayOfWeek.SATURDAY);
                System.out.println(calendar);
                if (outputDirectory != null) {
                    Export.CSVExport(calendar, outputDirectory);
                    Export.employeeExport(ScheduleCreator.employeeSet, outputDirectory);
                } else {
                    System.out.println("No output directory selected.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Number of Employees per Day must be a valid integer.");
            }
        });

        vbox.getChildren().addAll(new Label("Enter Schedule Configuration:"), employeesPerDayField, beginDatePicker, endDatePicker, chooseDirectoryButton, directoryLabel, generateScheduleButton);
        tab.setContent(vbox);
        return tab;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DBUtils.closeConnection(connection);
    }
}
