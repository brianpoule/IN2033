package com.lancaster.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import BoxOfficeInterface.JDBC;

public class myJDBC {
    // Load environment variables from .env file
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = "jdbc:mysql://sst-stuproj.city.ac.uk:3306/in2033t27";
    private static final String USER = dotenv.get("ADMIN_USER");
    private static final String PASSWORD = dotenv.get("ADMIN_PASSWORD");

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to the database successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
        }
        return conn;
    }

    public static List<String> getCalendarAvailability(Date date) throws SQLException, ClassNotFoundException {

        JDBC jdbc = new JDBC();

        return jdbc.getCalendarAvailability(date);
    }
    public int getTodaysEventCount() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM marketing_events WHERE DATE(date) = 20250404"; 

        System.out.println("Executing query: " + query);
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            System.out.print(rs);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<Map<String, Object>> getTodaysMarketingEvents() {
        List<Map<String, Object>> eventsList = new ArrayList<>();
    
        String query = "SELECT * FROM marketing_events WHERE DATE(date) = CURDATE()";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("eventId", rs.getInt("eventId"));
                eventMap.put("type", rs.getString("type"));
                eventMap.put("date", rs.getDate("date"));
                eventMap.put("room", rs.getInt("room"));
                eventMap.put("duration", rs.getInt("duration"));
                eventsList.add(eventMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Print fetched events
        System.out.println("Fetched Today's Events:");
        for (Map<String, Object> event : eventsList) {
            System.out.println("Event ID: " + event.get("eventId") + ", Type: " + event.get("type") + ", Date: " + event.get("date") + ", Room: " + event.get("room") + ", Duration: " + event.get("duration"));
        }

        return eventsList;
    }
}
