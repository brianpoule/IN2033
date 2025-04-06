package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import com.lancaster.database.myJDBC;

public class FilmShowsUI extends JPanel {
    private JTable showsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JButton filmsButton;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public FilmShowsUI() {
        setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("  Film Shows");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add status label
        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(Color.BLACK);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 240, 240));
        searchField = new JTextField(20);
        searchField.setToolTipText("Search for shows by title, date, or room");
        JLabel searchLabel = new JLabel("Search: ");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        // Create table model
        String[] columns = {"Show ID", "Film ID", "Film Title", "Show Date", "End Date", "Duration", "Ticket Price", "Room"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1 || columnIndex == 5) {
                    return Integer.class; // Show ID, Film ID, Duration as integers
                } else if (columnIndex == 6) {
                    return Double.class; // Ticket Price as double
                } else if (columnIndex == 3 || columnIndex == 4) {
                    return Date.class; // Dates as Date objects
                }
                return String.class;
            }
        };

        // Create table with sorting capability
        showsTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        showsTable.setRowSorter(sorter);
        showsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        showsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showsTable.setGridColor(new Color(230, 230, 230));
        showsTable.setShowGrid(true);
        showsTable.setRowHeight(30);
        showsTable.setIntercellSpacing(new Dimension(5, 5));
        showsTable.setFillsViewportHeight(true);

        // Customize header appearance
        JTableHeader header = showsTable.getTableHeader();
        header.setBackground(new Color(100, 150, 200));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Center align content in cells and customize appearance
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(135, 206, 250)); // Light blue selection
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245)); // Zebra striping
                    c.setForeground(Color.BLACK);
                }

                // Format currency
                if (column == 6 && value != null) { // Ticket price column
                    setText("£" + String.format("%.2f", value));
                }

                // Format duration
                if (column == 5 && value != null) { // Duration column
                    setText(value + " min");
                }

                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Apply renderer to all columns
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            showsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        showsTable.getColumnModel().getColumn(0).setPreferredWidth(70); // Show ID
        showsTable.getColumnModel().getColumn(1).setPreferredWidth(70); // Film ID
        showsTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Film Title

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(showsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Setup search functionality
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchFilter();
            }
        });

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setIcon(UIManager.getIcon("FileView.refreshIcon"));
        refreshButton.setBackground(new Color(220, 220, 220));
        refreshButton.addActionListener(e -> loadFilmShowsData());

        JButton newShowButton = new JButton("Create New Show");
        newShowButton.setBackground(new Color(92, 184, 92));
        newShowButton.setForeground(Color.BLACK);
        newShowButton.addActionListener(e -> createNewShow());

        JButton deleteShowButton = new JButton("Delete Show");
        deleteShowButton.setBackground(new Color(217, 83, 79));
        deleteShowButton.setForeground(Color.BLACK);
        deleteShowButton.addActionListener(e -> deleteSelectedShow());

        buttonPanel.add(refreshButton);
        buttonPanel.add(newShowButton);
        buttonPanel.add(deleteShowButton);

        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add right-click context menu
        addContextMenu();

        loadFilmShowsData();
    }

    private void searchFilter() {
        String text = searchField.getText().trim().toLowerCase();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
            statusLabel.setText("Showing all records");
        } else {
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            filters.add(RowFilter.regexFilter("(?i)" + text, 2)); // Film Title
            filters.add(RowFilter.regexFilter("(?i)" + text, 3)); // Show Date
            filters.add(RowFilter.regexFilter("(?i)" + text, 7)); // Room

            RowFilter<Object, Object> combinedFilter = RowFilter.orFilter(filters);
            sorter.setRowFilter(combinedFilter);

            int rowCount = showsTable.getRowCount();
            statusLabel.setText("Found " + rowCount + " matching records");
        }
    }

    private void addContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewDetailsItem = new JMenuItem("View Details");
        viewDetailsItem.addActionListener(e -> viewShowDetails());

        JMenuItem editItem = new JMenuItem("Edit Show");
        editItem.addActionListener(e -> editSelectedShow());

        JMenuItem deleteItem = new JMenuItem("Delete Show");
        deleteItem.addActionListener(e -> deleteSelectedShow());

        contextMenu.add(viewDetailsItem);
        contextMenu.add(editItem);
        contextMenu.add(deleteItem);

        showsTable.setComponentPopupMenu(contextMenu);
    }

    private void viewShowDetails() {
        int selectedRow = showsTable.getSelectedRow();
        if (selectedRow != -1) {
            // Convert to model index in case table is sorted
            int modelRow = showsTable.convertRowIndexToModel(selectedRow);

            // Get the data for the selected row
            int showId = (Integer) tableModel.getValueAt(modelRow, 0);
            String filmTitle = (String) tableModel.getValueAt(modelRow, 2);
            Date showDate = (Date) tableModel.getValueAt(modelRow, 3);
            Date endDate = (Date) tableModel.getValueAt(modelRow, 4);
            int duration = (Integer) tableModel.getValueAt(modelRow, 5);
            double price = (Double) tableModel.getValueAt(modelRow, 6);
            String room = (String) tableModel.getValueAt(modelRow, 7);

            // Format the details
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String details = "Show ID: " + showId + "\n" +
                    "Film: " + filmTitle + "\n" +
                    "Show Date: " + dateFormat.format(showDate) + "\n" +
                    "End Date: " + dateFormat.format(endDate) + "\n" +
                    "Duration: " + duration + " minutes\n" +
                    "Ticket Price: £" + String.format("%.2f", price) + "\n" +
                    "Room: " + room;

            // Show details in a dialog
            JOptionPane.showMessageDialog(this, details, "Show Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a show first", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editSelectedShow() {
        int selectedRow = showsTable.getSelectedRow();
        if (selectedRow != -1) {
            // Convert to model index in case table is sorted
            int modelRow = showsTable.convertRowIndexToModel(selectedRow);

            // Get the data for the selected row
            int showId = (Integer) tableModel.getValueAt(modelRow, 0);
            int filmId = (Integer) tableModel.getValueAt(modelRow, 1);
            String filmTitle = (String) tableModel.getValueAt(modelRow, 2);
            Date showDate = (Date) tableModel.getValueAt(modelRow, 3);
            Date endDate = (Date) tableModel.getValueAt(modelRow, 4);
            int duration = (Integer) tableModel.getValueAt(modelRow, 5);
            double price = (Double) tableModel.getValueAt(modelRow, 6);
            String room = (String) tableModel.getValueAt(modelRow, 7);

            // Create edit dialog similar to createNewShow but prepopulated
            // ... (dialog creation code would go here)
            // This would be similar to your createNewShow method but prefilling the fields

            JOptionPane.showMessageDialog(this, "Edit functionality would go here", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a show first", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedShow() {
        int selectedRow = showsTable.getSelectedRow();
        if (selectedRow != -1) {
            // Convert to model index in case table is sorted
            int modelRow = showsTable.convertRowIndexToModel(selectedRow);
            int showId = (Integer) tableModel.getValueAt(modelRow, 0);
            String filmTitle = (String) tableModel.getValueAt(modelRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete show #" + showId + " (" + filmTitle + ")?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    deleteShow(showId);
                    loadFilmShowsData();
                    statusLabel.setText("Show #" + showId + " deleted successfully");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error deleting show: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a show first", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteShow(int showId) throws Exception {
        String query = "DELETE FROM film_bookings WHERE showId = ?";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, showId);
            pstmt.executeUpdate();
        }
    }

    private void loadFilmShowsData() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String dataQuery = "SELECT showId, filmId, filmTitle, showDate, endDate, duration, ticketPrice, room, venue, description FROM film_bookings";
                Statement dataStmt = connection.createStatement();
                ResultSet dataRs = dataStmt.executeQuery(dataQuery);

                tableModel.setRowCount(0); // Clear existing data

                while (dataRs.next()) {
                    // Handle zero date values and convert them to null
                    Date showDate = dataRs.getDate("showDate");
                    if (showDate != null && showDate.toString().equals("0000-00-00")) {
                        showDate = null;
                    }

                    Date endDate = dataRs.getDate("endDate");
                    if (endDate != null && endDate.toString().equals("0000-00-00")) {
                        endDate = null;
                    }

                    Object[] row = {
                            dataRs.getInt("showId"),
                            dataRs.getInt("filmId"),
                            dataRs.getString("filmTitle"),
                            showDate,
                            endDate,
                            dataRs.getInt("duration"),
                            dataRs.getDouble("ticketPrice"),
                            dataRs.getString("room"),
                            dataRs.getString("venue"),
                            dataRs.getString("description")
                    };
                    tableModel.addRow(row);
                }

                statusLabel.setText("Data loaded successfully. Total shows: " + tableModel.getRowCount());
                statusLabel.setForeground(Color.BLACK);
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
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

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

        // Duration field
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Duration (min):"), gbc);
        JTextField durationField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(durationField, gbc);

        // Ticket Price field
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Ticket Price (£):"), gbc);
        JTextField ticketPriceField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(ticketPriceField, gbc);

        // Room field
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Room:"), gbc);
        JTextField roomField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(roomField, gbc);

        // Venue field
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Venue:"), gbc);
        JTextField venueField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 7;
        formPanel.add(venueField, gbc);

        // Description field
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Description:"), gbc);
        JTextArea descriptionArea = new JTextArea(3, 20);
        gbc.gridx = 1; gbc.gridy = 8;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                // Basic validation
                if (filmIdField.getText().trim().isEmpty() || filmTitleField.getText().trim().isEmpty() ||
                        startDateField.getText().trim().isEmpty() || endDateField.getText().trim().isEmpty() ||
                        durationField.getText().trim().isEmpty() || ticketPriceField.getText().trim().isEmpty() ||
                        roomField.getText().trim().isEmpty() || venueField.getText().trim().isEmpty() ||
                        descriptionArea.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog, "All fields are required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get values from form
                int filmId = Integer.parseInt(filmIdField.getText());
                String filmTitle = filmTitleField.getText();
                String startDateStr = startDateField.getText();
                String endDateStr = endDateField.getText();
                int duration = Integer.parseInt(durationField.getText());
                double ticketPrice = Double.parseDouble(ticketPriceField.getText());
                String room = roomField.getText();
                String venue = venueField.getText();
                String description = descriptionArea.getText();

                // Validate timestamp format
                if (!startDateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}") ||
                        !endDateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid dates and times in the format YYYY-MM-DD HH:MM:SS", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Convert startDateStr and endDateStr to Timestamp
                java.sql.Timestamp startTimestamp = java.sql.Timestamp.valueOf(startDateStr);
                java.sql.Timestamp endTimestamp = java.sql.Timestamp.valueOf(endDateStr);

                // Insert new show into the database
                insertNewShow(filmId, filmTitle, startTimestamp, endTimestamp, duration, ticketPrice, room, venue, description);
                loadFilmShowsData(); // Refresh the table data
                JOptionPane.showMessageDialog(dialog, "New show added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose(); // Close the dialog after successful insertion
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid dates and times in the format YYYY-MM-DD HH:MM:SS", "Validation Error", JOptionPane.ERROR_MESSAGE);

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

    private void insertNewShow(int filmId, String filmTitle, java.sql.Timestamp startTimestamp, java.sql.Timestamp endTimestamp, int duration, double ticketPrice, String room, String venue, String description) throws Exception {
        String query = "INSERT INTO film_bookings (filmId, filmTitle, showDate, endDate, duration, ticketPrice, room, venue, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = myJDBC.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, filmId);
            pstmt.setString(2, filmTitle);
            pstmt.setTimestamp(3, startTimestamp);
            pstmt.setTimestamp(4, endTimestamp);
            pstmt.setInt(5, duration);
            pstmt.setDouble(6, ticketPrice);
            pstmt.setString(7, room);
            pstmt.setString(8, venue);
            pstmt.setString(9, description);
            pstmt.executeUpdate();
        }
    }

    private void navigateToHomeUI() {
        // Switch to HomeUI
        HomeUI homeUI = new HomeUI("Guest");
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // Clear the current content
        parentFrame.getContentPane().removeAll();

        // Set the new content
        parentFrame.setContentPane(homeUI);
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}