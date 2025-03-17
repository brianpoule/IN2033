package com.lancaster;

import com.lancaster.database.JDBC;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        try (Connection connection = JDBC.getConnection()) {
            if (connection != null) {
                System.out.println("✅ Database connected successfully!");

                // Create table if it doesn't exist
                createUsersTable(connection);
            }
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            e.printStackTrace();
        }
    }

    private static void createUsersTable(Connection connection) {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                email VARCHAR(100) NOT NULL UNIQUE,
                password_hash VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Users table created or already exists.");
        } catch (SQLException e) {
            System.out.println("❌ Failed to create users table!");
            e.printStackTrace();
        }
    }
}
