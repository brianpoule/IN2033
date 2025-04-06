package com.lancaster.gui;

import com.lancaster.database.myJDBC;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class CalendarUI extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(60, 141, 188);
    private static final Color HEADER_TEXT_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Color EVENT_COLOR = new Color(92, 184, 92);
    private static final Color TODAY_COLOR = new Color(255, 255, 0, 50);
    private static final Color SELECTED_COLOR = new Color(135, 206, 250, 100);

    private JLabel headerLabel;
    private JPanel viewContainer;
    private CardLayout cardLayout;

    private YearMonth displayedYearMonth;
    private LocalDate selectedDate;
    private String currentView = "MONTH";

    private JPanel monthViewPanel;
    private JPanel weekViewPanel;
    private JPanel dayViewPanel;

    private Map<LocalDate, List<Map<String, Object>>> eventsByDate;

    public CalendarUI() {
        setLayout(new BorderLayout());
        displayedYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();
        eventsByDate = new HashMap<>();

        // Create header panel
        JPanel headerPanel = createHeaderPanel();

        // Card panel for different views
        cardLayout = new CardLayout();
        viewContainer = new JPanel(cardLayout);

        // Initialize views
        monthViewPanel = createMonthViewPanel();
        weekViewPanel = createWeekViewPanel();
        dayViewPanel = createDayViewPanel();

        viewContainer.add(monthViewPanel, "MONTH");
        viewContainer.add(weekViewPanel, "WEEK");
        viewContainer.add(dayViewPanel, "DAY");

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(viewContainer, BorderLayout.CENTER);

        // Load events
        loadEvents();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setPreferredSize(new Dimension(800, 60));

        // Title label
        headerLabel = new JLabel();
        headerLabel.setFont(TITLE_FONT);
        headerLabel.setForeground(HEADER_TEXT_COLOR);
        updateHeaderLabel();
        headerPanel.add(headerLabel, BorderLayout.WEST);

        // Navigation and view buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // Navigation buttons
        JButton prevButton = createStyledButton("<", PRIMARY_COLOR);
        JButton nextButton = createStyledButton(">", PRIMARY_COLOR);

        prevButton.addActionListener(e -> {
            navigatePrevious();
            refreshViews();
        });

        nextButton.addActionListener(e -> {
            navigateNext();
            refreshViews();
        });

        // View buttons
        JButton monthViewBtn = createStyledButton("Month", PRIMARY_COLOR);
        JButton weekViewBtn = createStyledButton("Week", PRIMARY_COLOR);
        JButton dayViewBtn = createStyledButton("Day", PRIMARY_COLOR);

        monthViewBtn.addActionListener(e -> {
            currentView = "MONTH";
            cardLayout.show(viewContainer, "MONTH");
            updateHeaderLabel();
        });

        weekViewBtn.addActionListener(e -> {
            currentView = "WEEK";
            cardLayout.show(viewContainer, "WEEK");
            updateHeaderLabel();
        });

        dayViewBtn.addActionListener(e -> {
            currentView = "DAY";
            cardLayout.show(viewContainer, "DAY");
            updateHeaderLabel();
        });

        // Add buttons to panel
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(monthViewBtn);
        buttonPanel.add(weekViewBtn);
        buttonPanel.add(dayViewBtn);

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
        button.setPreferredSize(new Dimension(80, 30));
        return button;
    }

    private void navigatePrevious() {
        switch (currentView) {
            case "MONTH":
                displayedYearMonth = displayedYearMonth.minusMonths(1);
                selectedDate = displayedYearMonth.atDay(1);
                break;
            case "WEEK":
                selectedDate = selectedDate.minusWeeks(1);
                displayedYearMonth = YearMonth.from(selectedDate);
                break;
            case "DAY":
                selectedDate = selectedDate.minusDays(1);
                displayedYearMonth = YearMonth.from(selectedDate);
                break;
        }
    }

    private void navigateNext() {
        switch (currentView) {
            case "MONTH":
                displayedYearMonth = displayedYearMonth.plusMonths(1);
                selectedDate = displayedYearMonth.atDay(1);
                break;
            case "WEEK":
                selectedDate = selectedDate.plusWeeks(1);
                displayedYearMonth = YearMonth.from(selectedDate);
                break;
            case "DAY":
                selectedDate = selectedDate.plusDays(1);
                displayedYearMonth = YearMonth.from(selectedDate);
                break;
        }
    }

    private void updateHeaderLabel() {
        switch (currentView) {
            case "MONTH":
                headerLabel.setText(" " + displayedYearMonth.getMonth().toString() + " " + displayedYearMonth.getYear());
                break;
            case "WEEK":
                LocalDate startOfWeek = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() % 7);
                LocalDate endOfWeek = startOfWeek.plusDays(6);
                headerLabel.setText(" Week: " + startOfWeek.format(java.time.format.DateTimeFormatter.ofPattern("MMM d")) +
                        " - " + endOfWeek.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy")));
                break;
            case "DAY":
                headerLabel.setText(" " + selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
                break;
        }
    }

    private JPanel createMonthViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create day names header
        JPanel dayNamesPanel = new JPanel(new GridLayout(1, 7));
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayNames) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setOpaque(true);
            label.setBackground(new Color(240, 240, 240));
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            dayNamesPanel.add(label);
        }
        panel.add(dayNamesPanel, BorderLayout.NORTH);

        // Create calendar grid
        JPanel calendarGrid = new JPanel(new GridLayout(0, 7));
        calendarGrid.setBackground(Color.WHITE);

        // Get first day of the month and total days
        LocalDate firstOfMonth = displayedYearMonth.atDay(1);
        int startIndex = firstOfMonth.getDayOfWeek().getValue() % 7;
        int totalDays = displayedYearMonth.lengthOfMonth();

        // Add blank labels for days before the first day
        for (int i = 0; i < startIndex; i++) {
            calendarGrid.add(new JLabel(""));
        }

        // Add day labels with events
        for (int day = 1; day <= totalDays; day++) {
            LocalDate currentDay = displayedYearMonth.atDay(day);
            JPanel dayPanel = createDayPanel(currentDay);
            calendarGrid.add(dayPanel);
        }

        // Fill remaining cells with blank labels if needed
        int totalCells = startIndex + totalDays;
        int remainingCells = (7 - (totalCells % 7)) % 7;
        for (int i = 0; i < remainingCells; i++) {
            calendarGrid.add(new JLabel(""));
        }

        panel.add(calendarGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDayPanel(LocalDate date) {
        JPanel dayPanel = new JPanel();
        dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
        dayPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        dayPanel.setBackground(Color.WHITE);

        // Add date label
        JLabel dateLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
        dateLabel.setFont(REGULAR_FONT);
        dayPanel.add(dateLabel);

        // Highlight today's date
        if (date.equals(LocalDate.now())) {
            dayPanel.setBackground(TODAY_COLOR);
        }

        // Highlight selected date
        if (date.equals(selectedDate)) {
            dayPanel.setBackground(SELECTED_COLOR);
        }

        // Add events for this day
        List<Map<String, Object>> dayEvents = eventsByDate.get(date);
        if (dayEvents != null && !dayEvents.isEmpty()) {
            JPanel eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            eventsPanel.setBackground(Color.WHITE);

            for (Map<String, Object> event : dayEvents) {
                JLabel eventLabel = new JLabel(event.get("type").toString());
                eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                eventLabel.setForeground(Color.WHITE);
                eventLabel.setBackground(EVENT_COLOR);
                eventLabel.setOpaque(true);
                eventLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
                eventsPanel.add(eventLabel);
            }

            dayPanel.add(eventsPanel);
        }

        // Add click listener
        dayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedDate = date;
                if (evt.getClickCount() == 2) {
                    // Double click switches to day view
                    currentView = "DAY";
                    cardLayout.show(viewContainer, "DAY");
                }
                refreshViews();
            }
        });

        return dayPanel;
    }

    private JPanel createWeekViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create day headers
        JPanel dayHeadersPanel = new JPanel(new GridLayout(1, 7));
        LocalDate startOfWeek = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() % 7);

        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            headerPanel.setBackground(new Color(240, 240, 240));

            // Day of week
            JLabel dowLabel = new JLabel(day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            dowLabel.setHorizontalAlignment(SwingConstants.CENTER);
            dowLabel.setFont(new Font("Arial", Font.BOLD, 14));

            // Day of month
            JLabel domLabel = new JLabel(String.valueOf(day.getDayOfMonth()));
            domLabel.setHorizontalAlignment(SwingConstants.CENTER);
            domLabel.setFont(new Font("Arial", Font.PLAIN, 12));

            headerPanel.add(dowLabel, BorderLayout.NORTH);
            headerPanel.add(domLabel, BorderLayout.CENTER);

            // Highlight today and selected day
            if (day.equals(LocalDate.now())) {
                headerPanel.setBackground(TODAY_COLOR);
            }
            if (day.equals(selectedDate)) {
                headerPanel.setBackground(SELECTED_COLOR);
            }

            // Make day headers clickable
            final LocalDate clickDate = day;
            headerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    selectedDate = clickDate;
                    if (evt.getClickCount() == 2) {
                        // Double click switches to day view
                        currentView = "DAY";
                        cardLayout.show(viewContainer, "DAY");
                    }
                    refreshViews();
                }
            });

            dayHeadersPanel.add(headerPanel);
        }
        panel.add(dayHeadersPanel, BorderLayout.NORTH);

        // Create events grid
        JPanel eventsGrid = new JPanel(new GridLayout(1, 7));
        eventsGrid.setBackground(Color.WHITE);

        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            JPanel dayEventsPanel = createDayEventsPanel(day);
            eventsGrid.add(dayEventsPanel);
        }

        panel.add(eventsGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDayEventsPanel(LocalDate date) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setBackground(Color.WHITE);

        // Highlight today's date and selected date
        if (date.equals(LocalDate.now())) {
            panel.setBackground(TODAY_COLOR);
        }
        if (date.equals(selectedDate)) {
            panel.setBackground(SELECTED_COLOR);
        }

        // Add events for this day
        List<Map<String, Object>> dayEvents = eventsByDate.get(date);
        if (dayEvents != null && !dayEvents.isEmpty()) {
            for (Map<String, Object> event : dayEvents) {
                JPanel eventPanel = new JPanel();
                eventPanel.setLayout(new BorderLayout());
                eventPanel.setBackground(EVENT_COLOR);
                eventPanel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

                JLabel eventLabel = new JLabel(event.get("type").toString());
                eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                eventLabel.setForeground(Color.WHITE);
                eventPanel.add(eventLabel, BorderLayout.CENTER);

                panel.add(eventPanel);
                panel.add(Box.createRigidArea(new Dimension(0, 2))); // Space between events
            }
        }

        // Make day panel clickable
        final LocalDate clickDate = date;
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedDate = clickDate;
                if (evt.getClickCount() == 2) {
                    // Double click switches to day view
                    currentView = "DAY";
                    cardLayout.show(viewContainer, "DAY");
                }
                refreshViews();
            }
        });

        return panel;
    }

    private JPanel createDayViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create time slots panel (left side)
        JPanel timeSlotPanel = new JPanel(new GridLayout(24, 1));
        timeSlotPanel.setBackground(Color.WHITE);
        timeSlotPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        for (int hour = 0; hour < 24; hour++) {
            String timeText = String.format("%02d:00", hour);
            JLabel timeLabel = new JLabel(timeText);
            timeLabel.setFont(REGULAR_FONT);
            timeLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
            timeSlotPanel.add(timeLabel);
        }

        // Create events panel (right side)
        JPanel eventsContainer = new JPanel(new GridLayout(24, 1));
        eventsContainer.setBackground(Color.WHITE);

        // Events mapped by hour (for simplicity)
        Map<Integer, List<Map<String, Object>>> eventsByHour = new HashMap<>();

        // Distribute events to hours (simplified approach)
        List<Map<String, Object>> dayEvents = eventsByDate.get(selectedDate);
        if (dayEvents != null && !dayEvents.isEmpty()) {
            // For this example, just distribute events across hours
            for (int i = 0; i < dayEvents.size(); i++) {
                int hour = 8 + i; // Start events at 8 AM
                if (hour < 24) {
                    eventsByHour.computeIfAbsent(hour, k -> new ArrayList<>()).add(dayEvents.get(i));
                }
            }
        }

        // Create hour panels with events
        for (int hour = 0; hour < 24; hour++) {
            JPanel hourPanel = new JPanel();
            hourPanel.setLayout(new BoxLayout(hourPanel, BoxLayout.X_AXIS));
            hourPanel.setBackground(Color.WHITE);
            hourPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

            // Add events for this hour
            List<Map<String, Object>> hourEvents = eventsByHour.get(hour);
            if (hourEvents != null && !hourEvents.isEmpty()) {
                for (Map<String, Object> event : hourEvents) {
                    JPanel eventBlock = createEventBlock(event);
                    hourPanel.add(eventBlock);
                    hourPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Space between blocks
                }
            }

            eventsContainer.add(hourPanel);
        }

        // Combine time slots and events
        JPanel dayViewContent = new JPanel(new BorderLayout());
        dayViewContent.add(timeSlotPanel, BorderLayout.WEST);
        dayViewContent.add(eventsContainer, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(dayViewContent);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEventBlock(Map<String, Object> event) {
        JPanel block = new JPanel();
        block.setPreferredSize(new Dimension(80, 30)); // Fixed size for event blocks
        block.setBackground(EVENT_COLOR);
        block.setBorder(BorderFactory.createLineBorder(new Color(60, 130, 60)));
        block.setLayout(new BorderLayout());

        JLabel typeLabel = new JLabel(event.get("type").toString());
        typeLabel.setFont(new Font("Arial", Font.BOLD, 10));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        block.add(typeLabel, BorderLayout.CENTER);

        // Add tooltip with details
        block.setToolTipText("Room: " + event.get("room") + " | Duration: " + event.get("duration") + " min");

        // Add click listener for event details
        block.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showEventDetails(event);
            }
        });

        return block;
    }

    private void showEventDetails(Map<String, Object> event) {
        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Event Details");
        detailsDialog.setSize(300, 200);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel typeLabel = new JLabel("Type: " + event.get("type"));
        JLabel roomLabel = new JLabel("Room: " + event.get("room"));
        JLabel durationLabel = new JLabel("Duration: " + event.get("duration") + " minutes");
        JLabel dateLabel = new JLabel("Date: " + selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));

        typeLabel.setFont(REGULAR_FONT);
        roomLabel.setFont(REGULAR_FONT);
        durationLabel.setFont(REGULAR_FONT);
        dateLabel.setFont(REGULAR_FONT);

        detailsPanel.add(typeLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(roomLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(durationLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(dateLabel);

        detailsDialog.add(detailsPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailsDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);

        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        detailsDialog.setVisible(true);
    }

    private void loadEvents() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM marketing_events WHERE date BETWEEN ? AND ?";
                PreparedStatement pstmt = connection.prepareStatement(query);

                // Set date range based on the current view
                LocalDate startDate;
                LocalDate endDate;

                switch (currentView) {
                    case "DAY":
                        startDate = selectedDate;
                        endDate = selectedDate;
                        break;
                    case "WEEK":
                        startDate = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() % 7);
                        endDate = startDate.plusDays(6);
                        break;
                    default: // MONTH
                        startDate = displayedYearMonth.atDay(1);
                        endDate = displayedYearMonth.atEndOfMonth();
                        break;
                }

                pstmt.setDate(1, Date.valueOf(startDate));
                pstmt.setDate(2, Date.valueOf(endDate));

                ResultSet rs = pstmt.executeQuery();
                eventsByDate.clear();

                while (rs.next()) {
                    LocalDate eventDate = rs.getDate("date").toLocalDate();
                    Map<String, Object> event = new HashMap<>();
                    event.put("type", rs.getString("type"));
                    event.put("room", rs.getInt("room"));
                    event.put("duration", rs.getInt("duration"));

                    eventsByDate.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshViews() {
        updateHeaderLabel();
        loadEvents();

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

        cardLayout.show(viewContainer, currentView);
    }
}