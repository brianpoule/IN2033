package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
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


        JLabel titleLabel = new JLabel("  Friends of Lancaster's");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        // Add New User button
        JButton newUserButton = new JButton("New User");
        newUserButton.setBackground(new Color(0, 166, 90));
        newUserButton.setForeground(Color.WHITE);
        newUserButton.setFocusPainted(false);
        newUserButton.addActionListener(e -> showNewUserDialog());
        headerPanel.add(newUserButton);

        // Add status label
        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(Color.WHITE);
        headerPanel.add(statusLabel, BorderLayout.EAST);


        String[] columns = {"ID", "Name", "Email", "Membership Level", "Join Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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


        loadFriendsData();
    }
    private void showNewUserDialog() {
        // Create dialog
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "New Friend", true);
        dialog.setLayout(new BorderLayout());

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(nameField, gbc);

        // Email field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(emailField, gbc);



        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                // Get values from form
                String name = nameField.getText();
                String email = emailField.getText();

                // Validate inputs
                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all required fields.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insert new user
                insertNewUser(name, email);

                // Close dialog and refresh table
                dialog.dispose();
                loadFriendsData();

                JOptionPane.showMessageDialog(this,
                        "New friend added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding new friend: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Show dialog
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void insertNewUser(String name, String email) throws Exception {
        String query = "INSERT INTO friends_lancaster (friend_id,name, email,subscription_status, join_date, booking_num) " +
                "VALUES (friend_id,?, ?,1,  CURRENT_DATE(),0)";

        try (Connection connection = JDBC.getConnection()) {
            if (connection != null) {
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.executeUpdate();
            }
        }
    }


    private void loadFriendsData() {
        try (Connection connection = JDBC.getConnection()) {
            if (connection != null) {

                String countQuery = "SELECT COUNT(friend_id) FROM friends_lancaster";
                Statement countStmt = connection.createStatement();
                ResultSet countRs = countStmt.executeQuery(countQuery);

                if (countRs.next()) {
                    friendsNum = countRs.getInt(1);
                }


                String dataQuery = "SELECT * FROM friends_lancaster";
                Statement dataStmt = connection.createStatement();
                ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                tableModel.setRowCount(0);

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
            e.printStackTrace();
            statusLabel.setText("Error loading data: " + e.getMessage());
            statusLabel.setForeground(new Color(255, 100, 100));
        }
    }
}
