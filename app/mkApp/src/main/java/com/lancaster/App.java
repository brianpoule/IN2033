package com.lancaster;
//import BoxOfficeInterface.JDBC;

import com.lancaster.database.OperationsInterface.JDBC;
import com.lancaster.gui.LoginUI;


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
