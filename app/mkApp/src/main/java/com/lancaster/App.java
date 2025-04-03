package com.lancaster;
import BoxOfficeInterface.JDBC;

import com.lancaster.gui.LoginUI;


import java.sql.SQLException;
import java.util.List;

/**
 * Entry point of the application
 */
public class App {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        JDBC jdbc = new JDBC();
        List<String> unavailableVenues = jdbc.getVenueUnavailability();
        for (String venueName : unavailableVenues) {
            System.out.println(venueName);
        }
        new LoginUI();
    }
}
