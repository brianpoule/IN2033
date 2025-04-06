package com.lancaster.gui;

import com.lancaster.database.myJDBC;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class InvoiceUI extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(60, 141, 188);
    private static final Color HEADER_TEXT_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField totalField;

    public InvoiceUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Create header panel
        JPanel headerPanel = createHeaderPanel();

        // Create table panel
        JPanel tablePanel = createTablePanel();

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        // Load data
        loadInvoiceData();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setPreferredSize(new Dimension(800, 60));

        // Title label
        JLabel titleLabel = new JLabel("Invoices");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(HEADER_TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // Add New Invoice button
        JButton newInvoiceButton = createStyledButton("New Invoice", PRIMARY_COLOR);
        newInvoiceButton.addActionListener(e -> createNewInvoice());
        buttonPanel.add(newInvoiceButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model
        String[] columns = {"Invoice ID", "Client Name", "Date", "Cost", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch(columnIndex) {
                    case 0: return Integer.class; // Invoice ID
                    case 2: return Timestamp.class; // Date
                    case 3: return Double.class; // Cost
                    case 4: return Double.class; // Total
                    default: return String.class;
                }
            }
        };

        // Create table with improved styling
        invoiceTable = new JTable(tableModel);
        styleTable(invoiceTable);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Create total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        totalPanel.add(new JLabel("Total: £"));
        totalField = new JTextField(10);
        totalField.setEditable(false);
        totalField.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(totalField);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(totalPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void createNewInvoice() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Invoice", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Client Name field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Client Name:"), gbc);
        JTextField clientNameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(clientNameField, gbc);

        // Cost field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Cost (£):"), gbc);
        JTextField costField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(costField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                // Basic validation
                if (clientNameField.getText().trim().isEmpty() || costField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get values from form
                String clientName = clientNameField.getText();
                double cost = Double.parseDouble(costField.getText());
                double total = cost; // In this case, total equals cost

                // Insert new invoice into the database
                insertNewInvoice(clientName, cost, total);
                loadInvoiceData(); // Refresh the table data
                JOptionPane.showMessageDialog(dialog, "New invoice added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for Cost", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding new invoice: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void insertNewInvoice(String clientName, double cost, double total) throws Exception {
        String query = "INSERT INTO invoices (clientName, date, cost, total) VALUES (?, NOW(), ?, ?)";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, clientName);
            pstmt.setDouble(2, cost);
            pstmt.setDouble(3, total);
            pstmt.executeUpdate();
        }
    }

    private void loadInvoiceData() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM invoices ORDER BY date DESC";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                tableModel.setRowCount(0); // Clear existing data
                double totalSum = 0;

                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("invoiceID"),
                            rs.getString("clientName"),
                            rs.getTimestamp("date"),
                            rs.getDouble("cost"),
                            rs.getDouble("total")
                    };
                    tableModel.addRow(row);
                    totalSum += rs.getDouble("total");
                }

                totalField.setText(String.format("%.2f", totalSum));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading invoice data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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