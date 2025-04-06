package com.lancaster.gui;

import com.lancaster.database.myJDBC;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import javax.imageio.ImageIO;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginUI() {
        setTitle("Login into Lancaster Marketing");
        setSize(600, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a panel for the form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add logo
        try {
            Image logo = ImageIO.read(new File("src/resources/logo.png")); 
            // Scale the logo to a smaller size
            Image scaledLogo = logo.getScaledInstance(200, 150, Image.SCALE_SMOOTH); 
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(logoLabel, gbc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Username label and field
        gbc.gridx = 0; gbc.gridy = 1; 
        formPanel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0; gbc.gridy = 2; 
        formPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(passwordField, gbc);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(60, 141, 188));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(loginButton, gbc);

        // Add form panel to the frame
        add(formPanel, BorderLayout.CENTER);

        // Optional: Add a footer or logo
        JLabel footerLabel = new JLabel("Welcome to Lancaster Marketing", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        add(footerLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (validateLogin(username, password)) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            dispose();
            SwingUtilities.invokeLater(() -> new HomeUI(username).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = myJDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}
