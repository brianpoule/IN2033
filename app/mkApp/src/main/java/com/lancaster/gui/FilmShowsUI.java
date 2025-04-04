package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.lancaster.database.JDBC;

public class FilmShowsUI extends JPanel {
    private JTable showsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JButton filmsButton; // Button to switch to HomeUI

    public FilmShowsUI() {
        setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("  Film Shows");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add status label
        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(Color.WHITE);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Create table model
        String[] columns = {"Show ID", "Film ID", "Film Title", "Show Date", "End Date", "Duration", "Ticket Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        showsTable = new JTable(tableModel);
        showsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        showsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showsTable.setGridColor(new Color(230, 230, 230));
        showsTable.setShowGrid(true);
        showsTable.setRowHeight(25);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(showsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton newShowButton = new JButton("Create New Show");
        newShowButton.addActionListener(e -> createNewShow());
        buttonPanel.add(newShowButton);


        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadFilmShowsData();
    }

    private void loadFilmShowsData() {
        try (Connection connection = JDBC.getConnection()) {
            if (connection != null) {
                String dataQuery = "SELECT showId, filmId, filmTitle, showDate, endDate, duration, ticketPrice FROM film_bookings";
                Statement dataStmt = connection.createStatement();
                ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                tableModel.setRowCount(0); // Clear existing data

                while (dataRs.next()) {
                    Object[] row = {
                        dataRs.getInt("showId"),
                        dataRs.getInt("filmId"),
                        dataRs.getString("filmTitle"),
                        dataRs.getDate("showDate"),
                        dataRs.getDate("endDate"),
                        dataRs.getInt("duration"),
                        dataRs.getDouble("ticketPrice")
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

    private void createNewShow() {
        // Create dialog for new show input
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Film Show", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Film ID field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Film ID:"), gbc);
        JTextField filmIdField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(filmIdField, gbc);

        // Film Title field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Film Title:"), gbc);
        JTextField filmTitleField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(filmTitleField, gbc);

        // Show Date field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Show Date:"), gbc);
        JTextField showDateField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(showDateField, gbc);

        // End Date field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("End Date:"), gbc);
        JTextField endDateField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(endDateField, gbc);

        // Duration field
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Duration (min):"), gbc);
        JTextField durationField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(durationField, gbc);

        // Ticket Price field
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Ticket Price:"), gbc);
        JTextField ticketPriceField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(ticketPriceField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                // Get values from form
                int filmId = Integer.parseInt(filmIdField.getText());
                String filmTitle = filmTitleField.getText();
                String showDate = showDateField.getText();
                String endDate = endDateField.getText();
                int duration = Integer.parseInt(durationField.getText());
                double ticketPrice = Double.parseDouble(ticketPriceField.getText());

                // Insert new show into the database
                insertNewShow(filmId, filmTitle, showDate, endDate, duration, ticketPrice);
                loadFilmShowsData(); // Refresh the table data
                JOptionPane.showMessageDialog(dialog, "New show added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose(); // Close the dialog after successful insertion
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding new show: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void insertNewShow(int filmId, String filmTitle, String showDate, String endDate, int duration, double ticketPrice) throws Exception {
        String query = "INSERT INTO film_bookings (filmId, filmTitle, showDate, endDate, duration, ticketPrice) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = JDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, filmId);
            pstmt.setString(2, filmTitle);
            pstmt.setString(3, showDate);
            pstmt.setString(4, endDate);
            pstmt.setInt(5, duration);
            pstmt.setDouble(6, ticketPrice);
            pstmt.executeUpdate();
        }
    }

    private void navigateToHomeUI() {
        // Switch to HomeUI
        HomeUI homeUI = new HomeUI("Guest"); // Pass a default username or modify as needed
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        // Clear the current content
        parentFrame.getContentPane().removeAll();
        
        // Set the new content
        parentFrame.setContentPane(homeUI);
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
