package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.lancaster.database.JDBC;

public class FilmsUI extends JPanel {
    private JTable filmsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JButton filmShowsButton;

    public FilmsUI() {
        setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("  Films of Lancaster's");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add status label
        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(Color.WHITE);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Create table model
        String[] columns = {"ID", "Name", "Description", "Duration", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        filmsTable = new JTable(tableModel);
        filmsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        filmsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        filmsTable.setGridColor(new Color(230, 230, 230));
        filmsTable.setShowGrid(true);
        filmsTable.setRowHeight(25);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(filmsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filmShowsButton = new JButton("Go to Film Shows");
        buttonPanel.add(filmShowsButton);

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadFilmsData();
    }



    private void loadFilmsData() {
        try (Connection connection = JDBC.getConnection()) {
            if (connection != null) {
                String dataQuery = "SELECT id, name, description, duration, price FROM films";
                Statement dataStmt = connection.createStatement();
                ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                tableModel.setRowCount(0); // Clear existing data

                while (dataRs.next()) {
                    Object[] row = {
                        dataRs.getInt("id"),
                        dataRs.getString("name"),
                        dataRs.getString("description"),
                        dataRs.getInt("duration"),
                        dataRs.getDouble("price")
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
}
