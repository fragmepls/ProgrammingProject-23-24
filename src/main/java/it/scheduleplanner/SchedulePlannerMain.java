package it.scheduleplanner;

import it.scheduleplanner.dbutils.DBUtils;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        DBUtils.initializeDatabase();
        DBUtils.getConnection();
    }

}
