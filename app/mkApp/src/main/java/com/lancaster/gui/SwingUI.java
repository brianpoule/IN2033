package com.lancaster.gui;

import javax.swing.*;
import java.awt.*;


/**
 * Basic Java Swing UI for the mkApp application
 */
public class SwingUI extends JFrame {
    
    private JPanel mainPanel;
    private JButton connectButton;
    private JTextArea statusArea;
    private JTabbedPane tabbedPane;

    
    public SwingUI() {
        // Set up the frame
        setTitle("Marketing Lancaster app");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        
        // Create a panel for the connection controls at the top
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectButton = new JButton("Connect to Database");
        JLabel statusLabel = new JLabel("Status: Not Connected");
        connectionPanel.add(connectButton);
        connectionPanel.add(statusLabel);
        
        // Create tabbed pane for different operations
        tabbedPane = new JTabbedPane();
        
        
      
        // Create status area at the bottom
        statusArea = new JTextArea(5, 40);
        statusArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusArea);
        
        // Add components to main panel
        mainPanel.add(connectionPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        
        // Add main panel to frame
        add(mainPanel);
        
        // Initially disable tabs until connection is established
        tabbedPane.setEnabled(false);
    }
    
       /**
     * Main method to launch the application
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
                SwingUI ui = new SwingUI();
                ui.setVisible(true);
            }
        });
    }
} 
