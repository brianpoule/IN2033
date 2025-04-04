package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import com.lancaster.database.JDBC;

public class MarketingEventsUI extends JPanel {
    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public MarketingEventsUI() {
        setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("  Marketing Events");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add status label
        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(Color.WHITE);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Create table model
        String[] columns = {"Event ID", "Type", "Name", "Date", "Room", "People"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        eventsTable = new JTable(tableModel);
        eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventsTable.setGridColor(new Color(230, 230, 230));
        eventsTable.setShowGrid(true);
        eventsTable.setRowHeight(25);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton fetchEventsButton = new JButton("Fetch New Events");
        fetchEventsButton.addActionListener(e -> fetchAndInsertEvents());
        buttonPanel.add(fetchEventsButton);

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadMarketingEventsData();
    }

    private void loadMarketingEventsData() {
        try (Connection connection = JDBC.getConnection()) {
            if (connection != null) {
                // Clear existing data
                tableModel.setRowCount(0);

                // Fetch data from marketing_events
                String query = "SELECT * FROM marketing_events";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("eventId"),
                        rs.getString("type"),
                        rs.getString("name"),
                        rs.getDate("date"),
                        rs.getInt("room"),
                        rs.getInt("peopleNum")
                    };
                    tableModel.addRow(row);
                }

                statusLabel.setText("Data loaded successfully");
                statusLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading data: " + e.getMessage());
            statusLabel.setForeground(new Color(255, 100, 100));
        }
    }

    private void fetchAndInsertEvents() {
        try (Connection connection = JDBC.getConnection()) {
            if (connection != null) {
                // Fetch from film_bookings
                String filmQuery = "SELECT 'Film' AS type, filmTitle AS name, showDate AS date, room, duration AS peopleNum FROM film_bookings";
                Statement filmStmt = connection.createStatement();
                ResultSet filmRs = filmStmt.executeQuery(filmQuery);
                insertEvents(filmRs, connection);

                // Fetch from group_bookings
                String groupQuery = "SELECT 'Group' AS type, event AS name, date, room, people AS peopleNum FROM group_bookings";
                Statement groupStmt = connection.createStatement();
                ResultSet groupRs = groupStmt.executeQuery(groupQuery);
                insertEvents(groupRs, connection);

                // Fetch from meeting_bookings
                String meetingQuery = "SELECT 'Meeting' AS type, meetingName AS name, date, room, peopleNum FROM meeting_bookings";
                Statement meetingStmt = connection.createStatement();
                ResultSet meetingRs = meetingStmt.executeQuery(meetingQuery);
                insertEvents(meetingRs, connection);

                // Fetch from tour_bookings
                String tourQuery = "SELECT 'Tour' AS type, organizationName AS name, date, room, people AS peopleNum FROM tour_bookings";
                Statement tourStmt = connection.createStatement();
                ResultSet tourRs = tourStmt.executeQuery(tourQuery);
                insertEvents(tourRs, connection);

                statusLabel.setText("Events fetched and inserted successfully");
                loadMarketingEventsData(); // Refresh the table data
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error fetching events: " + e.getMessage());
            statusLabel.setForeground(new Color(255, 100, 100));
        }
    }

    private void insertEvents(ResultSet rs, Connection connection) throws Exception {
        String insertQuery = "INSERT INTO marketing_events (type, name, date, room, peopleNum) VALUES (?, ?, ?, ?, ?)";
        String checkQuery = "SELECT COUNT(*) FROM marketing_events WHERE type = ? AND name = ? AND date = ? AND room = ? AND peopleNum = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery);
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            while (rs.next()) {
                String type = rs.getString("type");
                String name = rs.getString("name");
                Date date = rs.getDate("date");
                int room = rs.getInt("room");
                int peopleNum = rs.getInt("peopleNum");

                // Check for duplicates
                checkStmt.setString(1, type);
                checkStmt.setString(2, name);
                checkStmt.setDate(3, date);
                checkStmt.setInt(4, room);
                checkStmt.setInt(5, peopleNum);

                ResultSet checkRs = checkStmt.executeQuery();
                if (checkRs.next() && checkRs.getInt(1) == 0) { // No duplicates found
                    pstmt.setString(1, type);
                    pstmt.setString(2, name);
                    pstmt.setDate(3, date);
                    pstmt.setInt(4, room);
                    pstmt.setInt(5, peopleNum);
                    pstmt.executeUpdate();
                }
            }
        }
    }
} 