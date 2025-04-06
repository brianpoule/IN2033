package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.lancaster.database.myJDBC;

public class FriendsUI extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(60, 141, 188);
    private static final Color BUTTON_COLOR = new Color(92, 184, 92);
    private static final Color HEADER_TEXT_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

    private JTable friendsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    public static int friendsNum;

    public FriendsUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Create header panel with gradient background
        JPanel headerPanel = createHeaderPanel();

        // Create table panel
        JPanel tablePanel = createTablePanel();

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        // Load data
        loadFriendsData();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setPreferredSize(new Dimension(800, 60));

        // Title label
        JLabel titleLabel = new JLabel("Friends of Lancaster's");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(HEADER_TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // Add New Friend button
        JButton newUserButton = createStyledButton("New Friend", BUTTON_COLOR);
        newUserButton.addActionListener(e -> showNewUserDialog());
        buttonPanel.add(newUserButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(HEADER_TEXT_COLOR);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        headerPanel.add(statusPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());


        // Create table model
        String[] columns = {"ID", "Name", "Email", "Status", "Join Date", "Bookings"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 5) {
                    return Integer.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        // Create table with improved styling
        friendsTable = new JTable(tableModel);
        styleTable(friendsTable);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(friendsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void addTableContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Details");
        viewItem.addActionListener(e -> {
            int selectedRow = friendsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int friendId = (Integer) friendsTable.getValueAt(selectedRow, 0);
                // Show details dialog (implementation not shown)
                JOptionPane.showMessageDialog(this, "Details view for friend ID: " + friendId);
            }
        });

        JMenuItem editItem = new JMenuItem("Edit Friend");
        editItem.addActionListener(e -> {
            int selectedRow = friendsTable.getSelectedRow();
            if (selectedRow >= 0) {
                // Show edit dialog (implementation not shown)
                JOptionPane.showMessageDialog(this, "Edit functionality would go here");
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete Friend");
        deleteItem.addActionListener(e -> {
            int selectedRow = friendsTable.getSelectedRow();
            if (selectedRow >= 0) {
                // Show delete confirmation dialog (implementation not shown)
                int option = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this friend?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    // Delete functionality would go here
                    JOptionPane.showMessageDialog(this, "Delete functionality would go here");
                }
            }
        });

        contextMenu.add(viewItem);
        contextMenu.add(editItem);
        contextMenu.addSeparator();
        contextMenu.add(deleteItem);

        friendsTable.setComponentPopupMenu(contextMenu);
    }

    private void showNewUserDialog() {
        // Create dialog
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "New Friend of Lancaster", true);
        dialog.setLayout(new BorderLayout());

        // Create header for dialog
        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(PRIMARY_COLOR);
        dialogHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel dialogTitle = new JLabel("Add New Friend");
        dialogTitle.setFont(new Font("Arial", Font.BOLD, 16));
        dialogTitle.setForeground(Color.WHITE);
        dialogHeader.add(dialogTitle, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 0.1;

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(REGULAR_FONT);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        nameField.setFont(REGULAR_FONT);
        nameField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(REGULAR_FONT);
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.1;
        formPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        emailField.setFont(REGULAR_FONT);
        emailField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 15));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = createStyledButton("Save", BUTTON_COLOR);
        saveButton.setPreferredSize(new Dimension(100, 30));
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

                // Insert new friend
                insertNewUser(name, email);

                // Refresh data
                loadFriendsData();

                // Close dialog
                dialog.dispose();

                // Show success message
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

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add all components to dialog
        dialog.add(dialogHeader, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        dialog.pack();
        dialog.setSize(new Dimension(400, 250));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void insertNewUser(String name, String email) throws Exception {
        String query = "INSERT INTO friends_lancaster (name, email, subscription_status, join_date, booking_num) " +
                "VALUES (?, ?, 1, CURRENT_DATE(), 0)";

        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.executeUpdate();
            }
        }
    }

    private void loadFriendsData() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                // Get total count of friends
                String countQuery = "SELECT COUNT(friend_id) FROM friends_lancaster";
                try (Statement countStmt = connection.createStatement();
                     ResultSet countRs = countStmt.executeQuery(countQuery)) {
                    if (countRs.next()) {
                        friendsNum = countRs.getInt(1);
                    }
                }

                // Get friends data
                String dataQuery = "SELECT friend_id, name, email, subscription_status, join_date, booking_num FROM friends_lancaster";
                try (Statement dataStmt = connection.createStatement();
                     ResultSet dataRs = dataStmt.executeQuery(dataQuery)) {

                    tableModel.setRowCount(0);

                    while (dataRs.next()) {
                        Object[] row = {
                                dataRs.getInt("friend_id"),
                                dataRs.getString("name"),
                                dataRs.getString("email"),
                                dataRs.getInt("subscription_status") == 1 ? "Active" : "Inactive",
                                dataRs.getDate("join_date"),
                                dataRs.getInt("booking_num")
                        };
                        tableModel.addRow(row);
                    }

                    statusLabel.setText(friendsNum + " friends found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setForeground(new Color(255, 100, 100));
        }
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setIntercellSpacing(new Dimension(10, 5));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.DARK_GRAY);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
    }
}