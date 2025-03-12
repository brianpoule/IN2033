package com.lancaster.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Login UI for the mkApp application
 * This class provides a login screen that authenticates users before allowing access to the main application
 */
public class LoginUI extends JFrame {
    
    // Dummy credentials for testing
    private static final String DUMMY_USERNAME = "user";
    private static final String DUMMY_PASSWORD = "password";

    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    /**
     * Constructor for the LoginUI
     */
    public LoginUI() {
        // Set up the frame
        setTitle("Lancaster Marketing login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 245));
        
        // Create header panel with title
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(400, 60));
        JLabel titleLabel = new JLabel("Movie Booking System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);     
        formPanel.setBackground(new Color(240, 240, 245));
        
        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 20, 100, 25);
        usernameField = new JTextField();
        usernameField.setBounds(150, 20, 200, 25);
        
        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 60, 100, 25);
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 60, 200, 25);
        
        // Buttons
        loginButton = new JButton("Login");
        loginButton.setBounds(150, 100, 90, 30);
        loginButton.setBackground(new Color(60, 141, 188));
        loginButton.setForeground(Color.WHITE);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(260, 100, 90, 30);
        
        statusLabel = new JLabel("");
        statusLabel.setBounds(50, 140, 300, 25);
        statusLabel.setForeground(Color.RED);
        
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(cancelButton);
        formPanel.add(statusLabel);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Set focus to username field
        usernameField.requestFocusInWindow();
    }
    
    /**
     * Authenticate the user against the database
     * For demonstration purposes, this method accepts:
     * 1. Username "user" with password "password" - goes to HomeUI
     * In a real application, you would check against a users table in the database
     */
    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }
        
        // Check for dummy credentials to navigate to HomeUI
        if (username.equals(DUMMY_USERNAME) && password.equals(DUMMY_PASSWORD)) {
            // Authentication successful with dummy credentials
            statusLabel.setText("Login successful!");
            statusLabel.setForeground(new Color(0, 150, 0));
            
            // Launch the home page
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Close the login window
                    dispose();
                    
                    // Open the home page
                    HomeUI homeUI = new HomeUI(username);
                    homeUI.setVisible(true);
                }
            });
        }
       
         else {
            // Authentication failed
            statusLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
        
           }
    
    /**
     * Main method to launch the login UI
     */
    public static void main(String[] args) {
        // Set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginUI loginUI = new LoginUI();
                loginUI.setVisible(true);
            }
        });
    }
} 
