package com.lancaster.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.lancaster.database.myJDBC;

public class FilmsUI extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(60, 141, 188);
    private static final Color BUTTON_COLOR = new Color(92, 184, 92);
    private static final Color ACTION_BUTTON_COLOR = new Color(66, 139, 202);
    private static final Color HEADER_TEXT_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

    private JTable filmsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JButton filmShowsButton;

    public FilmsUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Create header panel with gradient background
        JPanel headerPanel = createHeaderPanel();

        // Create table panel
        JPanel tablePanel = createTablePanel();

        // Create footer panel
        JPanel footerPanel = createFooterPanel();

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        // Load data
        loadFilmsData();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setPreferredSize(new Dimension(800, 60));

        // Title label
        JLabel titleLabel = new JLabel("Films of Lancaster's");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(HEADER_TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        statusLabel = new JLabel("Loading data...");
        statusLabel.setForeground(HEADER_TEXT_COLOR);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusPanel.add(statusLabel, BorderLayout.EAST);

        headerPanel.add(statusPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Create table model
        String[] columns = {"ID", "Name", "Description", "Duration (min)", "Price ($)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch(columnIndex) {
                    case 0: return Integer.class; // ID
                    case 3: return Integer.class; // Duration
                    case 4: return Double.class;  // Price
                    default: return String.class;
                }
            }
        };

        // Create table with improved styling
        filmsTable = new JTable(tableModel);
        filmsTable.setFont(REGULAR_FONT);
        filmsTable.setRowHeight(30);
        filmsTable.setIntercellSpacing(new Dimension(10, 5));
        filmsTable.setFillsViewportHeight(true);
        filmsTable.setSelectionBackground(new Color(232, 242, 254));
        filmsTable.setSelectionForeground(Color.BLACK);
        filmsTable.setShowGrid(true);
        filmsTable.setGridColor(new Color(230, 230, 230));
        filmsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Style table header
        JTableHeader header = filmsTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.DARK_GRAY);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Set column widths for better display
        filmsTable.getColumnModel().getColumn(0).setPreferredWidth(50);    // ID
        filmsTable.getColumnModel().getColumn(1).setPreferredWidth(150);   // Name
        filmsTable.getColumnModel().getColumn(2).setPreferredWidth(300);   // Description
        filmsTable.getColumnModel().getColumn(3).setPreferredWidth(100);   // Duration
        filmsTable.getColumnModel().getColumn(4).setPreferredWidth(100);   // Price

        // Add context menu
        addTableContextMenu();

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(filmsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void addTableContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Film Details");
        viewItem.setFont(REGULAR_FONT);
        viewItem.addActionListener(e -> {
            int selectedRow = filmsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int filmId = (Integer) filmsTable.getValueAt(selectedRow, 0);
                // Show details dialog (implementation not shown)
                JOptionPane.showMessageDialog(this,
                        "Film Details for ID: " + filmId + "\nTitle: " + filmsTable.getValueAt(selectedRow, 1),
                        "Film Details",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JMenuItem showsItem = new JMenuItem("View Film Shows");
        showsItem.setFont(REGULAR_FONT);
        showsItem.addActionListener(e -> {
            int selectedRow = filmsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int filmId = (Integer) filmsTable.getValueAt(selectedRow, 0);
                // Navigate to film shows for this film (implementation not shown)
                JOptionPane.showMessageDialog(this,
                        "Navigate to shows for film: " + filmsTable.getValueAt(selectedRow, 1),
                        "Film Shows",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        contextMenu.add(viewItem);
        contextMenu.add(showsItem);

        filmsTable.setComponentPopupMenu(contextMenu);
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        footerPanel.setLayout(new BorderLayout());

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        filmShowsButton = createStyledButton("Go to Film Shows", ACTION_BUTTON_COLOR);
        filmShowsButton.setPreferredSize(new Dimension(150, 35));
        filmShowsButton.addActionListener(e -> {
            // Implementation for navigating to film shows
            JOptionPane.showMessageDialog(this, "Navigate to Film Shows functionality would go here");
        });

        buttonContainer.add(filmShowsButton);
        footerPanel.add(buttonContainer, BorderLayout.EAST);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadFilmsData() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String countQuery = "SELECT COUNT(id) FROM films";
                try (Statement countStmt = connection.createStatement();
                     ResultSet countRs = countStmt.executeQuery(countQuery)) {

                    int filmCount = 0;
                    if (countRs.next()) {
                        filmCount = countRs.getInt(1);
                    }

                    String dataQuery = "SELECT id, name, description, duration, price FROM films";
                    try (Statement dataStmt = connection.createStatement();
                         ResultSet dataRs = dataStmt.executeQuery(dataQuery)) {

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

                        statusLabel.setText(filmCount + " films found");
                        statusLabel.setForeground(HEADER_TEXT_COLOR);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setForeground(new Color(255, 100, 100));
        }
    }


}