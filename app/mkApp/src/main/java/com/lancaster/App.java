package com.lancaster;

import com.lancaster.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;


public class App
{
    public static void main( String[] args )
    {
        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Database connected successfully!");



        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
