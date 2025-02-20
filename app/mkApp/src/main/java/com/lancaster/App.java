package com.lancaster;

import com.lancaster.database.JDBC;
import java.sql.Connection;
import java.sql.SQLException;


public class App
{
    public static void main( String[] args )
    {
        try (Connection connection = JDBC.getConnection()) {
            System.out.println("Database connected successfully!");



        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
