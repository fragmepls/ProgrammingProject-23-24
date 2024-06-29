module it.scheduleplanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.databind;

    exports it.scheduleplanner.gui;
    opens it.scheduleplanner.gui to javafx.fxml;
}
