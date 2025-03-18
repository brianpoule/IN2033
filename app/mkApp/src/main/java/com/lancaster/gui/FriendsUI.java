package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.lancaster.database.JDBC;

public class FriendsUI extends JPanel {
    private JTable friendsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    public static int friendsNum;


    public FriendsUI() {
        setLayout(new BorderLayout());
        System.out.print(friendsNum);
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 50));

        // Add title
        JLabel titleLabel = new JLabel("  Friends of Lancaster's");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add status label
        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(Color.WHITE);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Create table model
        String[] columns = {"ID", "Name", "Email", "Membership Level", "Join Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        friendsTable = new JTable(tableModel);
        friendsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        friendsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendsTable.setGridColor(new Color(230, 230, 230));
        friendsTable.setShowGrid(true);
        friendsTable.setRowHeight(25);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(friendsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadFriendsData();
    }

    private void loadFriendsData() {
        try (Connection connection = JDBC.getConnection()) {
            if (connection != null) {
                // Get total count first
                String countQuery = "SELECT COUNT(friend_id) FROM friends_lancaster";
                Statement countStmt = connection.createStatement();
                ResultSet countRs = countStmt.executeQuery(countQuery);

                if (countRs.next()) { // Move to first row of count result
                    friendsNum = countRs.getInt(1);
                }

                // Now get all friend records
                String dataQuery = "SELECT * FROM friends_lancaster";
                Statement dataStmt = connection.createStatement();
                ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                // Clear existing data
                tableModel.setRowCount(0);

                // Add data to table - no need to call next() before while loop
                while (dataRs.next()) {
                    Object[] row = {
                            dataRs.getInt("friend_id"),
                            dataRs.getString("name"),
                            dataRs.getString("email"),
                            dataRs.getString("subscription_status"),
                            dataRs.getDate("join_date"),
                            dataRs.getString("booking_num")
                    };
                    tableModel.addRow(row);
                }

                statusLabel.setText("Data loaded successfully (" + friendsNum + " records)");
                statusLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the full stack trace for debugging
            statusLabel.setText("Error loading data: " + e.getMessage());
            statusLabel.setForeground(new Color(255, 100, 100));
        }
    }
}
