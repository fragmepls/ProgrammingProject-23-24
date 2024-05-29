package it.scheduleplanner;

import it.scheduleplanner.dbutils.DBUtils;
import it.scheduleplanner.dbutils.SQLQueries;
import it.scheduleplanner.export.Shift;
import it.scheduleplanner.planner.EmployeeComparator;
import it.scheduleplanner.utils.Employee;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class SchedulePlannerMain {

    public static void main(String[] args) throws SQLException {
        DBUtils.initializeDatabase();
        Connection connection = DBUtils.getConnection();

        Employee employee1 = SQLQueries.selectEmployeeById(connection, 1);
        Employee employee2 = SQLQueries.selectEmployeeById(connection, 2);
        System.out.println(employee1.getName());
        System.out.println(employee2.getName());

        ArrayList<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee1);
        employeeList.add(employee2);

        Map<Employee, Shift> map = EmployeeComparator.getNext(employeeList, LocalDate.now());
        System.out.println(map);

    }

}
