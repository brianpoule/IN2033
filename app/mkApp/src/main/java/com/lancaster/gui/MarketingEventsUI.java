package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import com.lancaster.database.myJDBC;

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
        String[] columns = {"Event ID", "Type", "Name", "Start Date", "End Date", "Room", "People", "Venue"};
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
        try (Connection connection = myJDBC.getConnection()) {
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
                            rs.getTimestamp("startDate"),
                            rs.getTimestamp("endDate"),
                            rs.getInt("room"),
                            rs.getInt("peopleNum"),
                            rs.getString("venue")
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
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                // Fetch from film_bookings
                String filmQuery = "SELECT 'Film' AS type, filmTitle AS name, showDate AS startDate, endDate, room, duration AS peopleNum, venue FROM film_bookings";
                Statement filmStmt = connection.createStatement();
                ResultSet filmRs = filmStmt.executeQuery(filmQuery);
                insertEvents(filmRs, connection);

                // Fetch from group_bookings
                String groupQuery = "SELECT 'Group' AS type, event AS name, startDate, endDate, room, people AS peopleNum, venue FROM group_bookings";
                Statement groupStmt = connection.createStatement();
                ResultSet groupRs = groupStmt.executeQuery(groupQuery);
                insertEvents(groupRs, connection);

                // Fetch from meeting_bookings
                String meetingQuery = "SELECT 'Meeting' AS type, meetingName AS name, startDate, endDate, room, peopleNum, venue FROM meeting_bookings";
                Statement meetingStmt = connection.createStatement();
                ResultSet meetingRs = meetingStmt.executeQuery(meetingQuery);
                insertEvents(meetingRs, connection);

                // Fetch from tour_bookings
                String tourQuery = "SELECT 'Tour' AS type, organizationName AS name, startDate, endDate, room, people AS peopleNum, venue FROM tour_bookings";
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
        String insertQuery = "INSERT INTO marketing_events (type, name, startDate, endDate, room, peopleNum, venue) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String checkQuery = "SELECT COUNT(*) FROM marketing_events WHERE type = ? AND name = ? AND startDate = ? AND endDate = ? AND room = ? AND peopleNum = ? AND venue = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery);
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            while (rs.next()) {
                String type = rs.getString("type");
                String name = rs.getString("name");
                Timestamp startDate = rs.getTimestamp("startDate");
                Timestamp endDate = rs.getTimestamp("endDate");
                int room = rs.getInt("room");
                int peopleNum = rs.getInt("peopleNum");
                String venue = rs.getString("venue");

                // Check for duplicates
                checkStmt.setString(1, type);
                checkStmt.setString(2, name);
                checkStmt.setTimestamp(3, startDate);
                checkStmt.setTimestamp(4, endDate);
                checkStmt.setInt(5, room);
                checkStmt.setInt(6, peopleNum);
                checkStmt.setString(7, venue);

                ResultSet checkRs = checkStmt.executeQuery();
                if (checkRs.next() && checkRs.getInt(1) == 0) {
                    pstmt.setString(1, type);
                    pstmt.setString(2, name);
                    pstmt.setTimestamp(3, startDate);
                    pstmt.setTimestamp(4, endDate);
                    pstmt.setInt(5, room);
                    pstmt.setInt(6, peopleNum);
                    pstmt.setString(7, venue);
                    pstmt.executeUpdate();
                }
            }
        }
    }
}