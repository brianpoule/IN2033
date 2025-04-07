package com.lancaster;

//import com.lancaster.database.OperationsInterface.OperationsData.;
import com.lancaster.gui.LoginUI;
import BoxOfficeInterface.JDBC;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Entry point of the application
 */
public class App {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        new LoginUI();
    }
}
