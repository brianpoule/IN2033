package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.lancaster.database.myJDBC;

public class MeetingBookingsUI extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public MeetingBookingsUI() {
        setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("  Meeting Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add status label
        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(Color.WHITE);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Create table model
        String[] columns = {"Booking ID", "Meeting Name", "Room Name", "Start Date", "End Date", "Number of People", "Room", "Venue"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        bookingsTable = new JTable(tableModel);
        bookingsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setGridColor(new Color(230, 230, 230));
        bookingsTable.setShowGrid(true);
        bookingsTable.setRowHeight(25);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton newBookingButton = new JButton("Create New Booking");
        newBookingButton.addActionListener(e -> createNewBooking());
        buttonPanel.add(newBookingButton);

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadMeetingBookingsData();
    }

    private void loadMeetingBookingsData() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String dataQuery = "SELECT bookingID, meetingName, roomName, startDate, endDate, peopleNum, room, venue FROM meeting_bookings";
                Statement dataStmt = connection.createStatement();
                ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                tableModel.setRowCount(0); // Clear existing data

                while (dataRs.next()) {
                    Object[] row = {
                            dataRs.getInt("bookingID"),
                            dataRs.getString("meetingName"),
                            dataRs.getString("roomName"),
                            dataRs.getTimestamp("startDate"),
                            dataRs.getTimestamp("endDate"),
                            dataRs.getInt("peopleNum"),
                            dataRs.getString("room"),
                            dataRs.getString("venue")
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

    private void createNewBooking() {
        // Create dialog for new booking input
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Meeting Booking", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Meeting Name field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Meeting Name:"), gbc);
        JTextField meetingNameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(meetingNameField, gbc);

        // Room Name field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Room Name:"), gbc);
        JTextField roomNameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(roomNameField, gbc);

        // Start Date field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD HH:MM:SS):"), gbc);
        JTextField startDateField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(startDateField, gbc);

        // End Date field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("End Date (YYYY-MM-DD HH:MM:SS):"), gbc);
        JTextField endDateField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(endDateField, gbc);

        // Number of People field
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Number of People:"), gbc);
        JTextField peopleNumField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(peopleNumField, gbc);

        // Room field
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Room:"), gbc);
        JTextField roomField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(roomField, gbc);

        // Venue field
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Venue:"), gbc);
        JTextField venueField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(venueField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                // Get values from form
                String meetingName = meetingNameField.getText();
                String roomName = roomNameField.getText();
                String startDateStr = startDateField.getText();
                String endDateStr = endDateField.getText();
                int peopleNum = Integer.parseInt(peopleNumField.getText());
                String room = roomField.getText();
                String venue = venueField.getText();

                // Validate timestamp format
                if (!startDateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}") ||
                        !endDateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid dates and times in the format YYYY-MM-DD HH:MM:SS", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Convert startDateStr and endDateStr to Timestamp
                java.sql.Timestamp startTimestamp = java.sql.Timestamp.valueOf(startDateStr);
                java.sql.Timestamp endTimestamp = java.sql.Timestamp.valueOf(endDateStr);

                // Insert new booking into the database
                insertNewBooking(meetingName, roomName, startTimestamp, endTimestamp, peopleNum, room, venue);
                loadMeetingBookingsData(); // Refresh the table data
                JOptionPane.showMessageDialog(dialog, "New booking added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose(); // Close the dialog after successful insertion
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid dates and times in the format YYYY-MM-DD HH:MM:SS", "Validation Error", JOptionPane.ERROR_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding new booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void insertNewBooking(String meetingName, String roomName, java.sql.Timestamp startTimestamp, java.sql.Timestamp endTimestamp, int peopleNum, String room, String venue) throws Exception {
        String query = "INSERT INTO meeting_bookings (meetingName, roomName, startDate, endDate, peopleNum, room, venue) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, meetingName);
            pstmt.setString(2, roomName);
            pstmt.setTimestamp(3, startTimestamp);
            pstmt.setTimestamp(4, endTimestamp);
            pstmt.setInt(5, peopleNum);
            pstmt.setString(6, room);
            pstmt.setString(7, venue);
            pstmt.executeUpdate();
        }
    }
}