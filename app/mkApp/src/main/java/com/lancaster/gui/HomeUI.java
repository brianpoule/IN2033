package com.lancaster.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Home UI for the mkApp application
 * This class provides the main home page 
 */
public class HomeUI extends JFrame {
    
    private JPanel mainPanel;
    private String username;
    
    /**
     * Constructor for the HomeUI
     * @param username The username of the logged-in user
     */
    public HomeUI(String username) {
        this.username = username;
        
        setTitle("Lancaster Marketing Home");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        mainPanel = new JPanel(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 70));
        
        // Add welcome message to header
        JLabel welcomeLabel = new JLabel("  Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Add logout button to header
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        buttonPanel.add(logoutButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create content panel with card layout for different sections
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create dashboard panel
        JPanel dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        
        // Create navigation panel (sidebar)
        JPanel navPanel = createNavigationPanel();
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    /**
     * Create the dashboard panel with summary information
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(Color.WHITE);
        
        // Create summary cards
        JPanel moviesCard = createSummaryCard("Movies", "12", new Color(60, 141, 188));
        JPanel bookingsCard = createSummaryCard("Bookings", "45", new Color(0, 166, 90));
        JPanel usersCard = createSummaryCard("Users", "8", new Color(243, 156, 18));
        JPanel promotionsCard = createSummaryCard("Promotions", "3", new Color(221, 75, 57));
        
        // Add cards to panel
        panel.add(moviesCard);
        panel.add(bookingsCard);
        panel.add(usersCard);
        panel.add(promotionsCard);
        
        // Create a wrapper panel with some padding
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
        
        // Add application name
        JLabel appLabel = new JLabel("Movie Booking System");
        appLabel.setFont(new Font("Arial", Font.BOLD, 14));
        appLabel.setForeground(Color.WHITE);
        appLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        appLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create menu buttons
        JButton dashboardButton = createMenuButton("Dashboard", true);
        JButton moviesButton = createMenuButton("Movies", false);
        JButton bookingsButton = createMenuButton("Bookings", false);
        JButton usersButton = createMenuButton("Users", false);
        JButton promotionsButton = createMenuButton("Promotions", false);
        JButton settingsButton = createMenuButton("Settings", false);
        
        // Add components to panel
        panel.add(appLabel);
        panel.add(dashboardButton);
        panel.add(moviesButton);
        panel.add(bookingsButton);
        panel.add(usersButton);
        panel.add(promotionsButton);
        panel.add(settingsButton);
        
        // Add filler to push everything to the top
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
                JOptionPane.showMessageDialog(HomeUI.this, 
                    text + " functionality will be implemented in future versions.", 
                    "Feature Coming Soon", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        return button;
    }
    
    /**
     * Logout and return to login screen
     */
    private void logout() {
        // Confirm logout
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // Close this window
            dispose();
            
            // Open login window
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    LoginUI loginUI = new LoginUI();
                    loginUI.setVisible(true);
                }
            });
        }
    
    }
}
    

