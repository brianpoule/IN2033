package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
        JButton newEventButton = new JButton("Create New Event");
        newEventButton.addActionListener(e -> createNewEvent());
        buttonPanel.add(newEventButton);

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

                // Fetch data from film_bookings
                String filmQuery = "SELECT showId AS eventId, 'Film' AS type, filmTitle AS name, showDate AS date, room, duration AS peopleNum FROM film_bookings";
                Statement filmStmt = connection.createStatement();
                ResultSet filmRs = filmStmt.executeQuery(filmQuery);
                while (filmRs.next()) {
                    Object[] row = {
                        filmRs.getInt("eventId"),
                        filmRs.getString("type"),
                        filmRs.getString("name"),
                        filmRs.getDate("date"),
                        filmRs.getString("room"),
                        filmRs.getInt("peopleNum")
                    };
                    tableModel.addRow(row);
                }

                // Fetch data from group_bookings
                String groupQuery = "SELECT booking_id AS eventId, 'Group' AS type, event AS name, date, room, people AS peopleNum FROM group_bookings";
                Statement groupStmt = connection.createStatement();
                ResultSet groupRs = groupStmt.executeQuery(groupQuery);
                while (groupRs.next()) {
                    Object[] row = {
                        groupRs.getInt("eventId"),
                        groupRs.getString("type"),
                        groupRs.getString("name"),
                        groupRs.getDate("date"),
                        groupRs.getString("room"),
                        groupRs.getInt("peopleNum")
                    };
                    tableModel.addRow(row);
                }

                // Fetch data from meeting_bookings
                String meetingQuery = "SELECT bookingID AS eventId, 'Meeting' AS type, meetingName AS name, date, room, peopleNum FROM meeting_bookings";
                Statement meetingStmt = connection.createStatement();
                ResultSet meetingRs = meetingStmt.executeQuery(meetingQuery);
                while (meetingRs.next()) {
                    Object[] row = {
                        meetingRs.getInt("eventId"),
                        meetingRs.getString("type"),
                        meetingRs.getString("name"),
                        meetingRs.getDate("date"),
                        meetingRs.getString("room"),
                        meetingRs.getInt("peopleNum")
                    };
                    tableModel.addRow(row);
                }

                // Fetch data from tour_bookings
                String tourQuery = "SELECT bookingID AS eventId, 'Tour' AS type, organizationName AS name, date, people AS peopleNum FROM tour_bookings";
                Statement tourStmt = connection.createStatement();
                ResultSet tourRs = tourStmt.executeQuery(tourQuery);
                while (tourRs.next()) {
                    Object[] row = {
                        tourRs.getInt("eventId"),
                        tourRs.getString("type"),
                        tourRs.getString("name"),
                        tourRs.getDate("date"),
                        tourRs.getInt("peopleNum")
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

    private void createNewEvent() {
        // Create dialog for new event input
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Marketing Event", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Type field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Type:"), gbc);
        JTextField typeField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(typeField, gbc);

        // Name field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(nameField, gbc);

        // Date field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date:"), gbc);
        JTextField dateField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(dateField, gbc);

        // Room field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Room:"), gbc);
        JTextField roomField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(roomField, gbc);

        // Number of People field
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Number of People:"), gbc);
        JTextField peopleNumField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(peopleNumField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                // Get values from form
                String type = typeField.getText();
                String name = nameField.getText();
                String date = dateField.getText(); // Assuming date is in a valid format
                String room = roomField.getText();
                int peopleNum = Integer.parseInt(peopleNumField.getText());

                // Insert new event into the database
                insertNewEvent(type, name, date, room, peopleNum);
                loadMarketingEventsData(); // Refresh the table data
                JOptionPane.showMessageDialog(dialog, "New event added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose(); // Close the dialog after successful insertion
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding new event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void insertNewEvent(String type, String name, String date, String room, int peopleNum) throws Exception {
        String query = "INSERT INTO marketing_events (type, name, date, room, peopleNum) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = JDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, type);
            pstmt.setString(2, name);
            pstmt.setString(3, date);
            pstmt.setString(4, room);
            pstmt.setInt(5, peopleNum);
            pstmt.executeUpdate();
        }
    }
} 