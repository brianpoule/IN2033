package com.lancaster.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.lancaster.database.myJDBC;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class HomeUI extends JFrame {

    private JPanel mainPanel;
    private JLabel statusLabel;
    private JPanel contentPanel;
    private JPanel dashboardPanel;
    private FriendsUI friendsUI;
    private FilmsUI filmsUI;
    private FilmShowsUI filmShowsUI;
    private CalendarUI calendarUI;
    private SettingsUI settingsUI;
    private JPanel sidebarPanel;
    private String username;
    private Map<String, Boolean> categoryStates = new HashMap<>();
    private Map<String, JPanel> categoryItemPanels = new HashMap<>();
    private Map<String, JLabel> categoryToggleIcons = new HashMap<>();
    private Map<String, JButton> navButtons = new HashMap<>();
    private Color primaryColor = new Color(47, 54, 64);
    private Color accentColor = new Color(86, 101, 115);
    private Color hoverColor = new Color(39, 60, 117);
    private Color selectedColor = new Color(25, 42, 86);
    private Color cardHeaderColor = new Color(52, 152, 219);

    // Sidebar categories
    private final String[] CATEGORIES = {"Dashboard", "Bookings", "Content", "Administration"};
    private final Map<String, String[]> CATEGORY_ITEMS = new HashMap<String, String[]>() {{
        put("Dashboard", new String[]{"Dashboard"});
        put("Bookings", new String[]{"Tour Bookings", "Meeting Bookings", "Film Shows"});
        put("Content", new String[]{"Friends", "Films", "Marketing Events"});
        put("Administration", new String[]{"Calendar", "Invoices", "Settings"});
    }};

    /**
     * Constructor for the HomeUI
     */
    public HomeUI(String username) {
        this.username = username;
        setTitle("Lancaster Marketing");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize category states (expanded by default)
        for (String category : CATEGORIES) {
            categoryStates.put(category, true);
        }

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        createHeaderPanel();
        createSidebarPanel();

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(245, 246, 250));

        // Initialize UI components
        friendsUI = new FriendsUI();
        filmsUI = new FilmsUI();
        filmShowsUI = new FilmShowsUI();
        settingsUI = new SettingsUI();
        calendarUI = new CalendarUI();
        dashboardPanel = createDashboardPanel();

        // Add dashboard panel to content by default
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);

        // Add panels to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        connectToDatabase();
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Logo and title
        JLabel logoLabel = new JLabel("Lancaster Marketing");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel, BorderLayout.WEST);

        // User info panel
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfoPanel.setOpaque(false);

        // User icon and username
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userIcon.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel(username);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);

        statusLabel = new JLabel("●");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(new Color(255, 150, 150));

        userInfoPanel.add(userIcon);
        userInfoPanel.add(userLabel);
        userInfoPanel.add(Box.createHorizontalStrut(5));
        userInfoPanel.add(statusLabel);
        userInfoPanel.add(Box.createHorizontalStrut(10));

        headerPanel.add(userInfoPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createSidebarPanel() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(primaryColor);
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, accentColor));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setBackground(new Color(31, 36, 42));
        brandPanel.setMaximumSize(new Dimension(220, 80));
        brandPanel.setPreferredSize(new Dimension(220, 80));

        // Add logo
        try {
            Image logo = ImageIO.read(new File("src/resources/logo.png"));
            // Scale the logo to a smaller size
            Image scaledLogo = logo.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            gbc.gridx = 0; gbc.gridy = 0;
            brandPanel.add(logoLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sidebarPanel.add(brandPanel);
        sidebarPanel.add(Box.createVerticalStrut(10));

        // Create category sections
        for (String category : CATEGORIES) {
            createCategoryPanel(category, CATEGORY_ITEMS.get(category));
        }

        // Add bottom padding
        sidebarPanel.add(Box.createVerticalGlue());

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
    }

    private void createCategoryPanel(String category, String[] items) {
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBackground(primaryColor);
        categoryPanel.setMaximumSize(new Dimension(220, 1000));

        // Create category header with toggle icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setMaximumSize(new Dimension(220, 40));
        headerPanel.setPreferredSize(new Dimension(220, 40));

        JLabel categoryLabel = new JLabel("  " + category);
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        categoryLabel.setForeground(Color.WHITE);

        JLabel toggleIcon = new JLabel(categoryStates.get(category) ? "▼ " : "► ");
        toggleIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toggleIcon.setForeground(Color.WHITE);

        // Store reference to toggle icon
        categoryToggleIcons.put(category, toggleIcon);

        headerPanel.add(categoryLabel, BorderLayout.WEST);
        headerPanel.add(toggleIcon, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Make the entire header panel clickable to toggle category
        headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Simple one-click toggle
                toggleCategory(category);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                headerPanel.setBackground(new Color(accentColor.getRed() + 10,
                        accentColor.getGreen() + 10,
                        accentColor.getBlue() + 10));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                headerPanel.setBackground(accentColor);
            }
        });

        categoryPanel.add(headerPanel);

        // Add menu items panel
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(primaryColor);

        for (String item : items) {
            JButton button = createMenuButton(item);
            itemsPanel.add(button);
            navButtons.put(item, button);
        }

        categoryPanel.add(itemsPanel);

        // Store references
        categoryItemPanels.put(category, itemsPanel);

        // Apply initial state
        itemsPanel.setVisible(categoryStates.get(category));

        sidebarPanel.add(categoryPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
    }

    private void toggleCategory(String category) {
        // Get the current state
        boolean isExpanded = categoryStates.get(category);

        // Toggle the state
        categoryStates.put(category, !isExpanded);

        // Update the toggle icon
        JLabel toggleIcon = categoryToggleIcons.get(category);
        toggleIcon.setText(categoryStates.get(category) ? "▼ " : "► ");

        // Show/hide the items panel
        categoryItemPanels.get(category).setVisible(categoryStates.get(category));

        // Refresh the sidebar
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(220, 40));
        button.setPreferredSize(new Dimension(220, 40));
        button.setBackground(primaryColor);
        button.setForeground(Color.LIGHT_GRAY);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // If it's Dashboard, select it by default
        if (text.equals("Dashboard")) {
            button.setBackground(selectedColor);
            button.setForeground(Color.WHITE);
        }

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.getBackground().equals(selectedColor)) {
                    button.setBackground(hoverColor);
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(selectedColor)) {
                    button.setBackground(primaryColor);
                    button.setForeground(Color.LIGHT_GRAY);
                }
            }
        });

        // Add action listener
        button.addActionListener(e -> handleNavigation(text, button));

        return button;
    }

    private void handleNavigation(String destination, JButton clickedButton) {
        // Reset all buttons
        navButtons.values().forEach(button -> {
            button.setBackground(primaryColor);
            button.setForeground(Color.LIGHT_GRAY);
        });

        // Highlight selected button
        clickedButton.setBackground(selectedColor);
        clickedButton.setForeground(Color.WHITE);

        // Navigate to appropriate panel
        contentPanel.removeAll();

        switch (destination) {
            case "Dashboard":
                contentPanel.add(dashboardPanel, BorderLayout.CENTER);
                break;
            case "Friends":
                contentPanel.add(friendsUI, BorderLayout.CENTER);
                break;
            case "Films":
                contentPanel.add(filmsUI, BorderLayout.CENTER);
                break;
            case "Film Shows":
                contentPanel.add(filmShowsUI, BorderLayout.CENTER);
                break;
            case "Calendar":
                contentPanel.add(calendarUI, BorderLayout.CENTER);
                break;
            case "Settings":
                contentPanel.add(settingsUI, BorderLayout.CENTER);
                break;
            case "Tour Bookings":
                contentPanel.add(new TourBookingsUI(), BorderLayout.CENTER);
                break;
            case "Meeting Bookings":
                contentPanel.add(new MeetingBookingsUI(), BorderLayout.CENTER);
                break;
            case "Marketing Events":
                contentPanel.add(new MarketingEventsUI(), BorderLayout.CENTER);
                break;
            case "Invoices":
                contentPanel.add(new InvoiceUI(), BorderLayout.CENTER);
                break;
        }

        // Refresh the content panel
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void connectToDatabase() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                statusLabel.setText("●");
                statusLabel.setForeground(new Color(100, 255, 100));
            }
        } catch (Exception e) {
            statusLabel.setText("●");
            statusLabel.setForeground(new Color(255, 70, 70));
        }
    }

    /**
     * Create the dashboard panel with summary information
     */
    private JPanel createDashboardPanel() {
        JPanel dashboardContent = new JPanel(new BorderLayout());
        dashboardContent.setBackground(new Color(245, 246, 250));

        // Dashboard header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(52, 73, 94));

        JLabel dateLabel = new JLabel("Today: " + java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(86, 101, 115));

        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(dateLabel, BorderLayout.CENTER);

        // Summary cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setOpaque(false);

        // Create summary cards with more modern styling
        JPanel friendsCard = createModernCard("Friends of Lancaster",
                Integer.toString(FriendsUI.friendsNum),
                new Color(52, 152, 219),
                "👥");

        JPanel todaysEventsCard = createModernCard("Today's Events",
                Integer.toString(new myJDBC().getTodaysEventCount()),
                new Color(46, 204, 113),
                "📅");

        JPanel tourBookingsCard = createModernCard("Tour Bookings",
                getTourBookingsCount(),
                new Color(155, 89, 182),
                "🚌");

        JPanel filmShowsCard = createModernCard("Film Shows",
                getFilmShowsCount(),
                new Color(231, 76, 60),
                "🎬");

        // Add cards to panel
        cardsPanel.add(friendsCard);
        cardsPanel.add(todaysEventsCard);
        cardsPanel.add(tourBookingsCard);
        cardsPanel.add(filmShowsCard);

        // Create a chart panel placeholder
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        chartPanel.setPreferredSize(new Dimension(200, 300));

        JLabel chartLabel = new JLabel("Activity Overview", JLabel.CENTER);
        chartLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chartPanel.add(chartLabel, BorderLayout.CENTER);

        // Arrange panels in main dashboard
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(cardsPanel, BorderLayout.NORTH);
        centerPanel.add(chartPanel, BorderLayout.CENTER);

        dashboardContent.add(headerPanel, BorderLayout.NORTH);
        dashboardContent.add(centerPanel, BorderLayout.CENTER);

        return dashboardContent;
    }

    private JPanel createModernCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Top panel with icon and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(52, 73, 94));

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Value panel
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setOpaque(false);
        valuePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        valuePanel.add(valueLabel, BorderLayout.WEST);

        // Add a colored indicator bar at the bottom
        JPanel indicatorPanel = new JPanel();
        indicatorPanel.setBackground(color);
        indicatorPanel.setPreferredSize(new Dimension(card.getWidth(), 5));

        // Assemble the card
        card.add(topPanel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        card.add(indicatorPanel, BorderLayout.SOUTH);

        return card;
    }

    // Database query methods
    private String getTourBookingsCount() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT COUNT(*) FROM tour_bookings";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        return Integer.toString(rs.getInt(1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String getFilmShowsCount() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT COUNT(*) FROM film_bookings";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        return Integer.toString(rs.getInt(1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    // Table styling method (kept for reference)
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setIntercellSpacing(new Dimension(10, 5));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(false);
        table.setGridColor(new Color(245, 245, 245));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(245, 246, 250));
        header.setForeground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            String loggedInUser = "Guest";
            if (args.length > 0) {
                loggedInUser = args[0];
            }
            HomeUI homeUI = new HomeUI(loggedInUser);
            homeUI.setVisible(true);
        });
    }
}