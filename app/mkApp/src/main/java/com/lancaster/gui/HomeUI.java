package com.lancaster.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.lancaster.database.JDBC;
import java.sql.Connection;


public class HomeUI extends JFrame {

    private JPanel mainPanel;
    private JLabel statusLabel;
    private JLabel userLabel;
    private JPanel contentPanel;
    private JPanel dashboardPanel;
    private FriendsUI friendsUI;
    private SettingsUI settingsUI;
    private JPanel navPanel;
    private String username;

    /**
     * Constructor for the HomeUI
     */
    public HomeUI(String username) {
        this.username = username;
        setTitle("Lancaster Marketing Home");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 70));


        JLabel titleLabel = new JLabel("Lancaster Marketing Home");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);


        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel("username:"+username);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);

        statusLabel = new JLabel("Status: Connecting...");
        statusLabel.setForeground(Color.WHITE);


        rightPanel.add(userLabel);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(statusLabel);


        headerPanel.add(rightPanel, BorderLayout.EAST);


        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        friendsUI = new FriendsUI();
        settingsUI = new SettingsUI();
        dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);


        navPanel = createNavigationPanel();


        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);


        connectToDatabase();
    }

    private void connectToDatabase() {
        try (Connection connection = JDBC.getConnection()) {
            if (connection != null) {
                statusLabel.setText("Status: Connected");
                statusLabel.setForeground(Color.WHITE);

            }
        } catch (Exception e) {
            statusLabel.setText("Status: Connection Failed");
            statusLabel.setForeground(new Color(255, 100, 100));

        }
    }

    /**
     * Create the dashboard panel with summary information
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(Color.WHITE);

        // Create summary cards
        JPanel friendsCard = createSummaryCard("Friends of Lancaster", Integer.toString(FriendsUI.friendsNum), new Color(60, 141, 188));
        JPanel bookingsCard = createSummaryCard("Bookings", "45", new Color(0, 166, 90));
        JPanel usersCard = createSummaryCard("Users", "8", new Color(243, 156, 18));
        JPanel promotionsCard = createSummaryCard("Promotions", "3", new Color(221, 75, 57));

        panel.add(friendsCard);
        panel.add(bookingsCard);
        panel.add(usersCard);
        panel.add(promotionsCard);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wrapperPanel.add(panel, BorderLayout.CENTER);

        return wrapperPanel;
    }

    /**
     * Create a summary card with title, value, and color
     */
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(color, 1));

        // Create header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(color);
        headerPanel.setPreferredSize(new Dimension(100, 40));
        JLabel headerLabel = new JLabel(title);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);

        // Create content
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(valueLabel);

        // Add components to panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create the navigation panel (sidebar)
     */
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(34, 45, 50));
        panel.setPreferredSize(new Dimension(200, 600));


        // Create menu buttons
        JButton dashboardButton = createMenuButton("Dashboard", true);
        JButton friendsButton = createMenuButton("Friends", false);
        JButton moviesButton = createMenuButton("Movies", false);
        JButton bookingsButton = createMenuButton("Bookings", false);
        JButton usersButton = createMenuButton("Users", false);
        JButton promotionsButton = createMenuButton("Promotions", false);
        JButton settingsButton = createMenuButton("Settings", false);

        // Add components to panel

        panel.add(dashboardButton,BorderLayout.WEST);
        panel.add(moviesButton);
        panel.add(bookingsButton);
        panel.add(usersButton);
        panel.add(friendsButton);
        panel.add(promotionsButton);
        panel.add(settingsButton);


        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Create a menu button for the sidebar
     */
    private JButton createMenuButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 50));
        button.setPreferredSize(new Dimension(200, 50));

        if (selected) {
            button.setBackground(new Color(30, 39, 46));
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(34, 45, 50));
            button.setForeground(Color.LIGHT_GRAY);
        }

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        // Add action listener
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleMenuButtonClick(text, button);
            }
        });

        return button;
    }


    private void handleMenuButtonClick(String text, JButton clickedButton) {

        for (Component c : navPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                if (b == clickedButton) {
                    b.setBackground(new Color(30, 39, 46));
                    b.setForeground(Color.WHITE);
                } else {
                    b.setBackground(new Color(34, 45, 50));
                    b.setForeground(Color.LIGHT_GRAY);
                }
            }
        }

        // Handle different menu items
        switch (text) {
            case "Dashboard":
                contentPanel.removeAll();
                contentPanel.add(dashboardPanel, BorderLayout.CENTER);
                break;
            case "Friends":
                if (friendsUI == null) {
                    friendsUI = new FriendsUI();
                }
                contentPanel.removeAll();
                contentPanel.add(friendsUI, BorderLayout.CENTER);
                break;
            case "Settings":
                if(settingsUI == null){
                    settingsUI = new SettingsUI();
                }
                contentPanel.removeAll();
                contentPanel.add(settingsUI, BorderLayout.CENTER);
                break;

        }

        // Refresh the content panel
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
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


