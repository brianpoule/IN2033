package com.lancaster.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarUI extends JPanel {
    private JLabel headerLabel;
    private JPanel viewContainer;
    private CardLayout cardLayout;

    private YearMonth displayedYearMonth;
    private LocalDate selectedDate;


    private JPanel monthViewPanel;
    private JPanel weekViewPanel;
    private JPanel dayViewPanel;

    public CalendarUI() {
        setLayout(new BorderLayout());
        displayedYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 141, 188));
        headerPanel.setPreferredSize(new Dimension(800, 50));

        headerLabel = new JLabel();
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        updateHeaderLabel();
        headerPanel.add(headerLabel, BorderLayout.WEST);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setOpaque(false);
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        prevButton.addActionListener(e -> {
            displayedYearMonth = displayedYearMonth.minusMonths(1);
            selectedDate = displayedYearMonth.atDay(1);
            refreshViews();
        });
        nextButton.addActionListener(e -> {
            displayedYearMonth = displayedYearMonth.plusMonths(1);
            selectedDate = displayedYearMonth.atDay(1);
            refreshViews();
        });
        navPanel.add(prevButton);
        navPanel.add(nextButton);


        JPanel viewSwitchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        viewSwitchPanel.setOpaque(false);
        JButton monthViewBtn = new JButton("Month");
        JButton weekViewBtn = new JButton("Week");
        JButton dayViewBtn = new JButton("Day");
        monthViewBtn.addActionListener(e -> cardLayout.show(viewContainer, "MONTH"));
        weekViewBtn.addActionListener(e -> cardLayout.show(viewContainer, "WEEK"));
        dayViewBtn.addActionListener(e -> cardLayout.show(viewContainer, "DAY"));
        viewSwitchPanel.add(monthViewBtn);
        viewSwitchPanel.add(weekViewBtn);
        viewSwitchPanel.add(dayViewBtn);

        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.setOpaque(false);
        topRightPanel.add(navPanel, BorderLayout.EAST);
        topRightPanel.add(viewSwitchPanel, BorderLayout.CENTER);

        headerPanel.add(topRightPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Card panel for different views
        cardLayout = new CardLayout();
        viewContainer = new JPanel(cardLayout);

        monthViewPanel = createMonthViewPanel();
        weekViewPanel = createWeekViewPanel();
        dayViewPanel = createDayViewPanel();

        viewContainer.add(monthViewPanel, "MONTH");
        viewContainer.add(weekViewPanel, "WEEK");
        viewContainer.add(dayViewPanel, "DAY");

        add(viewContainer, BorderLayout.CENTER);
    }

    private void updateHeaderLabel() {
        headerLabel.setText(" " + displayedYearMonth.getMonth().toString() + " " + displayedYearMonth.getYear());
    }

    // Refresh all views when date changes
    private void refreshViews() {
        updateHeaderLabel();
        remove(viewContainer);
        monthViewPanel = createMonthViewPanel();
        weekViewPanel = createWeekViewPanel();
        dayViewPanel = createDayViewPanel();

        viewContainer.removeAll();
        viewContainer.add(monthViewPanel, "MONTH");
        viewContainer.add(weekViewPanel, "WEEK");
        viewContainer.add(dayViewPanel, "DAY");

        add(viewContainer, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Month view similar to previous implementation.
    private JPanel createMonthViewPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 7));

        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayNames) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setOpaque(true);
            label.setBackground(new Color(210, 210, 210));
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            panel.add(label);
        }

        // Get first day of the month and total days
        LocalDate firstOfMonth = displayedYearMonth.atDay(1);
        int startIndex = firstOfMonth.getDayOfWeek().getValue() % 7;

        int totalDays = displayedYearMonth.lengthOfMonth();

        // Add blank labels for days before the first day
        for (int i = 0; i < startIndex; i++) {
            panel.add(new JLabel(""));
        }

        // Add day labels
        for (int day = 1; day <= totalDays; day++) {
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            panel.add(dayLabel);
        }

        // Fill remaining cells with blank labels if needed
        int totalCells = startIndex + totalDays;
        int remainingCells = (7 - (totalCells % 7)) % 7;
        for (int i = 0; i < remainingCells; i++) {
            panel.add(new JLabel(""));
        }
        return panel;
    }

    // Week view: Display a simple week overview based on the selectedDate.
    private JPanel createWeekViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel header = new JPanel(new GridLayout(1, 7));
        LocalDate startOfWeek = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() % 7);

        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            String dayName = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
            JLabel label = new JLabel(dayName + " " + day.getDayOfMonth(), SwingConstants.CENTER);
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            header.add(label);
        }
        panel.add(header, BorderLayout.NORTH);

        // Create a dummy grid for a week view (e.g., hours of the day)
        JPanel hoursPanel = new JPanel(new GridLayout(24, 7));
        for (int hour = 0; hour < 24; hour++) {
            for (int i = 0; i < 7; i++) {
                JLabel label = new JLabel("");
                label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                hoursPanel.add(label);
            }
        }
        JScrollPane scrollPane = new JScrollPane(hoursPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Day view: Display details for the selected day.
    private JPanel createDayViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel dayHeader = new JLabel("Day View: " + selectedDate.toString(), SwingConstants.CENTER);
        dayHeader.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(dayHeader, BorderLayout.NORTH);

        // Create a simple list of time slots for the day.
        JPanel timeSlotsPanel = new JPanel(new GridLayout(24, 1));
        for (int hour = 0; hour < 24; hour++) {
            JLabel slotLabel = new JLabel(String.format("%02d:00", hour));
            slotLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            timeSlotsPanel.add(slotLabel);
        }
        JScrollPane scrollPane = new JScrollPane(timeSlotsPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}