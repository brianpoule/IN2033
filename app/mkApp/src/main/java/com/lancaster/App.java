package com.lancaster;

import com.lancaster.gui.HomeUI;
import com.lancaster.database.JDBC;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        // Launch the HomeUI
        HomeUI.main(args);
    }

}
