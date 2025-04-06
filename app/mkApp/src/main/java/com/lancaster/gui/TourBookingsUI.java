package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.lancaster.database.myJDBC;

public class TourBookingsUI extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public TourBookingsUI() {
        setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("  Tour Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add status label
        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(Color.WHITE);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Create table model
        String[] columns = {"Booking ID", "Organization Type", "Organization Name", "Date"};
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

        loadTourBookingsData();
    }

    private void loadTourBookingsData() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String dataQuery = "SELECT bookingID, organizationType, organizationName, date FROM tour_bookings";
                Statement dataStmt = connection.createStatement();
                ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                tableModel.setRowCount(0); // Clear existing data

                while (dataRs.next()) {
                    Object[] row = {
                        dataRs.getInt("bookingID"),
                        dataRs.getString("organizationType"),
                        dataRs.getString("organizationName"),
                        dataRs.getDate("date")
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Tour Booking", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Organization Type field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Organization Type:"), gbc);
        JTextField organizationTypeField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(organizationTypeField, gbc);

        // Organization Name field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Organization Name:"), gbc);
        JTextField organizationNameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(organizationNameField, gbc);

        // Date field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date:"), gbc);
        JTextField dateField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(dateField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                // Get values from form
                String organizationType = organizationTypeField.getText();
                String organizationName = organizationNameField.getText();
                String date = dateField.getText(); // Assuming date is in a valid format

                // Insert new booking into the database
                insertNewBooking(organizationType, organizationName, date);
                loadTourBookingsData(); // Refresh the table data
                JOptionPane.showMessageDialog(dialog, "New booking added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose(); // Close the dialog after successful insertion
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

    private void insertNewBooking(String organizationType, String organizationName, String date) throws Exception {
        String query = "INSERT INTO tour_bookings (organizationType, organizationName, date) VALUES (?, ?, ?)";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, organizationType);
            pstmt.setString(2, organizationName);
            pstmt.setString(3, date);
            pstmt.executeUpdate();
        }
    }
} 