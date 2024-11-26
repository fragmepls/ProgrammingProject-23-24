module it.scheduleplanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires jdk.security.jgss;

    exports it.scheduleplanner.planner;
    exports it.scheduleplanner.dbutils;
    exports it.scheduleplanner.utils;
    exports it.scheduleplanner.gui;
    exports it.scheduleplanner.export;
    opens it.scheduleplanner.gui to javafx.fxml;
}
