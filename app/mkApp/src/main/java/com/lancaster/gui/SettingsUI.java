package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.lancaster.database.JDBC;

public class SettingsUI extends JPanel {
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;


    public SettingsUI() {
        setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 50));


        JLabel titleLabel = new JLabel("Users of Lancaster's");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        // Add New User button
        JButton newUserButton = new JButton("New User");
        newUserButton.setPreferredSize(new Dimension(10, 10));
        newUserButton.setBackground(new Color(116, 109, 109));
        newUserButton.setForeground(Color.BLACK);
        newUserButton.setFocusPainted(false);
        newUserButton.addActionListener(e -> showNewUserDialog());
        headerPanel.add(newUserButton);

        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(Color.WHITE);
        headerPanel.add(statusLabel, BorderLayout.EAST);


        String[] columns = {"ID", "Username", "created_at", };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        usersTable = new JTable(tableModel);
        usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setGridColor(new Color(230, 230, 230));
        usersTable.setShowGrid(true);
        usersTable.setRowHeight(25);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);


        loadUsersData();
    }
    private void showNewUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New User of Lancaster", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword()); // Convert char[] to String

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                insertNewUser(username, password);
                loadUsersData(); // Refresh table data
                JOptionPane.showMessageDialog(dialog, "New user added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose(); // Close the dialog after successful insertion
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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


    private void insertNewUser(String username, String password) throws Exception {
        String query = "INSERT INTO users (username, password, created_at) VALUES (?, ?, CURRENT_DATE())";

        try (Connection connection = JDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }
    }



    private void loadUsersData() {
        try (Connection connection = JDBC.getConnection()) {
            if (connection != null) {




                String dataQuery = "SELECT * FROM users";
                Statement dataStmt = connection.createStatement();
                ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                tableModel.setRowCount(0);

                while (dataRs.next()) {
                    Object[] row = {
                            dataRs.getInt("id"),
                            dataRs.getString("username"),
                            dataRs.getString("created_at"),
                           
                    };
                    tableModel.addRow(row);
                }


                statusLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading data: " + e.getMessage());
            statusLabel.setForeground(new Color(255, 100, 100));
        }
    }
}
