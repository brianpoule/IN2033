package com.lancaster.gui;

import com.lancaster.database.myJDBC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class CalendarUI extends JPanel {
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);  // Soft blue
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219); // Lighter blue
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Light gray background
    private static final Color TEXT_COLOR = new Color(52, 58, 64); // Dark gray for text
    private static final Color ACCENT_COLOR = new Color(46, 204, 113); // Green for events
    private static final Color HEADER_COLOR = new Color(33, 37, 41); // Dark color for headers
    private static final Color HOVER_COLOR = new Color(236, 240, 241); // Light hover effect
    private static final Color TODAY_COLOR = new Color(255, 238, 173); // Soft yellow for today
    private static final Color SELECTED_COLOR = new Color(174, 214, 241); // Light blue for selection
    private static final Color BORDER_COLOR = new Color(222, 226, 230); // Light gray for borders

    // Modern fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    private JLabel headerLabel;
    private JPanel viewContainer;
    private CardLayout cardLayout;

    private YearMonth displayedYearMonth;
    private LocalDate selectedDate;
    private String currentView = "MONTH";

    private JPanel monthViewPanel;
    private JPanel weekViewPanel;
    private JPanel dayViewPanel;
    private JButton monthViewBtn, weekViewBtn, dayViewBtn;
    private JButton todayButton;

    private Map<LocalDate, List<Map<String, Object>>> eventsByDate;

    public CalendarUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        displayedYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();
        eventsByDate = new HashMap<>();

        // Create header panel
        JPanel headerPanel = createHeaderPanel();

        // Card panel for different views
        cardLayout = new CardLayout();
        viewContainer = new JPanel(cardLayout);
        viewContainer.setBackground(BACKGROUND_COLOR);
        viewContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

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
        loadMarketingEvents();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Title and navigation area (left side)
        JPanel titleNavPanel = new JPanel(new BorderLayout(15, 0));
        titleNavPanel.setOpaque(false);

        // Title label
        headerLabel = new JLabel();
        headerLabel.setFont(TITLE_FONT);
        headerLabel.setForeground(HEADER_COLOR);
        updateHeaderLabel();

        // Navigation buttons with material design style
        JPanel navButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        navButtonPanel.setOpaque(false);

        JButton prevButton = createIconButton("◀");
        todayButton = createStyledButton("Today");
        todayButton.setFont(BUTTON_FONT);
        JButton nextButton = createIconButton("▶");

        prevButton.addActionListener(e -> {
            navigatePrevious();
            refreshViews();
        });

        todayButton.addActionListener(e -> {
            selectedDate = LocalDate.now();
            displayedYearMonth = YearMonth.from(selectedDate);
            refreshViews();
        });

        nextButton.addActionListener(e -> {
            navigateNext();
            refreshViews();
        });

        navButtonPanel.add(prevButton);
        navButtonPanel.add(todayButton);
        navButtonPanel.add(nextButton);

        titleNavPanel.add(headerLabel, BorderLayout.CENTER);
        titleNavPanel.add(navButtonPanel, BorderLayout.EAST);

        // View toggle buttons (right side)
        JPanel viewTogglePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        viewTogglePanel.setOpaque(false);

        // Create tab-like view selection
        JPanel viewTabsPanel = new JPanel();
        viewTabsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        viewTabsPanel.setOpaque(false);

        monthViewBtn = createTabButton("Month", true);
        weekViewBtn = createTabButton("Week", false);
        dayViewBtn = createTabButton("Day", false);

        // Add borders only between buttons for a connected look
        monthViewBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        weekViewBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        dayViewBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        monthViewBtn.addActionListener(e -> {
            setActiveTab(monthViewBtn);
            currentView = "MONTH";
            cardLayout.show(viewContainer, "MONTH");
            updateHeaderLabel();
        });

        weekViewBtn.addActionListener(e -> {
            setActiveTab(weekViewBtn);
            currentView = "WEEK";
            cardLayout.show(viewContainer, "WEEK");
            updateHeaderLabel();
        });

        dayViewBtn.addActionListener(e -> {
            setActiveTab(dayViewBtn);
            currentView = "DAY";
            cardLayout.show(viewContainer, "DAY");
            updateHeaderLabel();
        });

        viewTabsPanel.add(monthViewBtn);
        viewTabsPanel.add(weekViewBtn);
        viewTabsPanel.add(dayViewBtn);

        viewTogglePanel.add(viewTabsPanel);

        // Add components to the header panel
        headerPanel.add(titleNavPanel, BorderLayout.WEST);
        headerPanel.add(viewTogglePanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void setActiveTab(JButton activeButton) {
        // Reset all buttons
        monthViewBtn.setBackground(Color.WHITE);
        monthViewBtn.setForeground(TEXT_COLOR);
        weekViewBtn.setBackground(Color.WHITE);
        weekViewBtn.setForeground(TEXT_COLOR);
        dayViewBtn.setBackground(Color.WHITE);
        dayViewBtn.setForeground(TEXT_COLOR);

        // Set active button
        activeButton.setBackground(PRIMARY_COLOR);
        activeButton.setForeground(Color.WHITE);
    }

    private JButton createTabButton(String text, boolean isActive) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBackground(isActive ? PRIMARY_COLOR : Color.WHITE);
        button.setForeground(isActive ? Color.WHITE : TEXT_COLOR);
        button.setPreferredSize(new Dimension(100, 36));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createIconButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(PRIMARY_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 36));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(80, 36));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });

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
                headerLabel.setText(displayedYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + displayedYearMonth.getYear());
                break;
            case "WEEK":
                LocalDate startOfWeek = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() % 7);
                LocalDate endOfWeek = startOfWeek.plusDays(6);
                headerLabel.setText(startOfWeek.format(java.time.format.DateTimeFormatter.ofPattern("MMM d")) +
                        " - " + endOfWeek.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy")));
                break;
            case "DAY":
                headerLabel.setText(selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
                break;
        }
    }

    private JPanel createMonthViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Create day names header
        JPanel dayNamesPanel = new JPanel(new GridLayout(1, 7, 1, 0));
        dayNamesPanel.setBackground(PRIMARY_COLOR);
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayNames) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(SUBTITLE_FONT);
            label.setForeground(Color.WHITE);
            label.setOpaque(true);
            label.setBackground(PRIMARY_COLOR);
            label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            dayNamesPanel.add(label);
        }
        panel.add(dayNamesPanel, BorderLayout.NORTH);

        // Create calendar grid
        JPanel calendarGrid = new JPanel(new GridLayout(0, 7, 1, 1));
        calendarGrid.setBackground(BORDER_COLOR);

        // Get first day of the month and total days
        LocalDate firstOfMonth = displayedYearMonth.atDay(1);
        int startIndex = firstOfMonth.getDayOfWeek().getValue() % 7;
        int totalDays = displayedYearMonth.lengthOfMonth();

        // Add blank labels for days before the first day
        for (int i = 0; i < startIndex; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(new Color(245, 245, 245)); // Slightly different shade for empty days
            calendarGrid.add(emptyPanel);
        }

        // Add day panels with events
        for (int day = 1; day <= totalDays; day++) {
            LocalDate currentDay = displayedYearMonth.atDay(day);
            JPanel dayPanel = createDayPanel(currentDay);
            calendarGrid.add(dayPanel);
        }

        // Fill remaining cells with blank labels if needed
        int totalCells = startIndex + totalDays;
        int remainingCells = (7 - (totalCells % 7)) % 7;
        for (int i = 0; i < remainingCells; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(new Color(245, 245, 245));
            calendarGrid.add(emptyPanel);
        }

        panel.add(calendarGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDayPanel(LocalDate date) {
        JPanel dayPanel = new JPanel();
        dayPanel.setLayout(new BorderLayout());
        dayPanel.setBackground(Color.WHITE);

        // Date number in top-right corner
        JPanel dateNumberPanel = new JPanel(new BorderLayout());
        dateNumberPanel.setOpaque(false);
        dateNumberPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JLabel dateLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
        dateLabel.setFont(REGULAR_FONT);
        dateLabel.setForeground(TEXT_COLOR);

        // Create circular background for today's date
        if (date.equals(LocalDate.now())) {
            dateLabel.setOpaque(true);
            dateLabel.setBackground(PRIMARY_COLOR);
            dateLabel.setForeground(Color.WHITE);
            dateLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
            dateLabel.setVerticalAlignment(SwingConstants.CENTER);
        }

        dateNumberPanel.add(dateLabel, BorderLayout.NORTH);
        dayPanel.add(dateNumberPanel, BorderLayout.NORTH);

        // Highlight today's date
        if (date.equals(LocalDate.now())) {
            dayPanel.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        }

        // Highlight selected date
        if (date.equals(selectedDate)) {
            dayPanel.setBackground(SELECTED_COLOR);
        }

        // Panel for events
        JPanel eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setBackground(dayPanel.getBackground());
        eventsPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 5));

        // Add events for this day
        List<Map<String, Object>> dayEvents = eventsByDate.get(date);
        if (dayEvents != null && !dayEvents.isEmpty()) {
            int displayCount = Math.min(dayEvents.size(), 3); // Limit to 3 visible events

            for (int i = 0; i < displayCount; i++) {
                Map<String, Object> event = dayEvents.get(i);
                JPanel eventIndicator = createEventIndicator(event);
                eventsPanel.add(eventIndicator);
                eventsPanel.add(Box.createRigidArea(new Dimension(0, 2)));
            }

            // Add "more" indicator if there are more events
            if (dayEvents.size() > 3) {
                JLabel moreLabel = new JLabel("+" + (dayEvents.size() - 3) + " more");
                moreLabel.setFont(SMALL_FONT);
                moreLabel.setForeground(PRIMARY_COLOR);
                eventsPanel.add(moreLabel);
            }
        }

        dayPanel.add(eventsPanel, BorderLayout.CENTER);

        // Add hover effect and click listener
        dayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!date.equals(selectedDate)) {
                    dayPanel.setBackground(HOVER_COLOR);
                    eventsPanel.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!date.equals(selectedDate)) {
                    dayPanel.setBackground(Color.WHITE);
                    eventsPanel.setBackground(Color.WHITE);
                } else {
                    dayPanel.setBackground(SELECTED_COLOR);
                    eventsPanel.setBackground(SELECTED_COLOR);
                }
            }

            @Override
            public void mouseClicked(MouseEvent evt) {
                selectedDate = date;
                if (evt.getClickCount() == 2) {
                    // Double click switches to day view
                    currentView = "DAY";
                    setActiveTab(dayViewBtn);
                    cardLayout.show(viewContainer, "DAY");
                }
                refreshViews();
            }
        });

        return dayPanel;
    }

    private JPanel createEventIndicator(Map<String, Object> event) {
        JPanel indicator = new JPanel(new BorderLayout());
        indicator.setPreferredSize(new Dimension(10, 18));
        indicator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        // Rounded rectangle with event type
        String eventType = event.get("type").toString();
        JLabel eventLabel = new JLabel("  " + eventType);
        eventLabel.setFont(SMALL_FONT);
        eventLabel.setForeground(Color.WHITE);

        // Create custom color based on event type for visual distinction
        Color eventColor = getEventColor(eventType);

        indicator.setBackground(eventColor);
        indicator.add(eventLabel, BorderLayout.WEST);

        // Add subtle rounded corners
        indicator.setBorder(new LineBorder(eventColor, 1, true));

        // Add tooltip
        indicator.setToolTipText(eventType + " | " + event.get("venue") + " | Room: " + event.get("room"));

        return indicator;
    }

    private Color getEventColor(String eventType) {
        // Create different colors based on event type
        switch (eventType.toLowerCase()) {
            case "meeting":
                return new Color(41, 128, 185); // Blue
            case "presentation":
                return new Color(142, 68, 173); // Purple
            case "workshop":
                return new Color(39, 174, 96); // Green
            case "conference":
                return new Color(211, 84, 0); // Orange
            case "seminar":
                return new Color(22, 160, 133); // Teal
            default:
                return ACCENT_COLOR; // Default green
        }
    }

    private JPanel createWeekViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Create day headers
        JPanel dayHeadersPanel = new JPanel(new GridLayout(1, 7, 1, 0));
        dayHeadersPanel.setBackground(BORDER_COLOR);
        LocalDate startOfWeek = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() % 7);

        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

            // Different background for weekends
            if (i == 0 || i == 6) {
                headerPanel.setBackground(new Color(243, 244, 246));
            } else {
                headerPanel.setBackground(new Color(250, 251, 252));
            }

            // Day of week
            JLabel dowLabel = new JLabel(day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            dowLabel.setHorizontalAlignment(SwingConstants.CENTER);
            dowLabel.setFont(SUBTITLE_FONT);
            dowLabel.setForeground(TEXT_COLOR);

            // Day of month in a circular indicator if it's today
            JPanel dateContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            dateContainer.setOpaque(false);

            JLabel domLabel;
            if (day.equals(LocalDate.now())) {
                domLabel = new JLabel(String.valueOf(day.getDayOfMonth()));
                domLabel.setOpaque(true);
                domLabel.setBackground(PRIMARY_COLOR);
                domLabel.setForeground(Color.WHITE);
                domLabel.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));
                domLabel.setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                domLabel = new JLabel(String.valueOf(day.getDayOfMonth()));
                domLabel.setForeground(TEXT_COLOR);
            }
            domLabel.setFont(REGULAR_FONT);

            dateContainer.add(domLabel);

            headerPanel.add(dowLabel, BorderLayout.NORTH);
            headerPanel.add(dateContainer, BorderLayout.CENTER);

            // Highlight today and selected day
            if (day.equals(LocalDate.now())) {
                headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, PRIMARY_COLOR));
            }
            if (day.equals(selectedDate)) {
                headerPanel.setBackground(SELECTED_COLOR);
            }

            // Make day headers clickable
            final LocalDate clickDate = day;
            headerPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!clickDate.equals(selectedDate)) {
                        headerPanel.setBackground(HOVER_COLOR);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!clickDate.equals(selectedDate)) {
                        if (clickDate.getDayOfWeek().getValue() % 7 == 0 || clickDate.getDayOfWeek().getValue() % 7 == 6) {
                            headerPanel.setBackground(new Color(243, 244, 246));
                        } else {
                            headerPanel.setBackground(new Color(250, 251, 252));
                        }
                    } else {
                        headerPanel.setBackground(SELECTED_COLOR);
                    }
                }

                @Override
                public void mouseClicked(MouseEvent evt) {
                    selectedDate = clickDate;
                    if (evt.getClickCount() == 2) {
                        // Double click switches to day view
                        currentView = "DAY";
                        setActiveTab(dayViewBtn);
                        cardLayout.show(viewContainer, "DAY");
                    }
                    refreshViews();
                }
            });

            dayHeadersPanel.add(headerPanel);
        }
        panel.add(dayHeadersPanel, BorderLayout.NORTH);

        // Create events grid
        JPanel eventsGrid = new JPanel(new GridLayout(1, 7, 1, 0));
        eventsGrid.setBackground(BORDER_COLOR);

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
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setBackground(Color.WHITE);

        // Highlight today's date
        if (date.equals(LocalDate.now())) {
            panel.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, PRIMARY_COLOR));
        }

        // Highlight selected date
        if (date.equals(selectedDate)) {
            panel.setBackground(SELECTED_COLOR);
        }

        // Highlight weekends with a subtle background
        if (date.getDayOfWeek().getValue() % 7 == 0 || date.getDayOfWeek().getValue() % 7 == 6) {
            panel.setBackground(new Color(248, 248, 250));
        }

        // Add events for this day
        List<Map<String, Object>> dayEvents = eventsByDate.get(date);
        if (dayEvents != null && !dayEvents.isEmpty()) {
            for (Map<String, Object> event : dayEvents) {
                JPanel eventCard = createEventCard(event);
                panel.add(eventCard);
                panel.add(Box.createRigidArea(new Dimension(0, 5))); // Space between events
            }
        }

        // Make day panel clickable
        final LocalDate clickDate = date;
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!clickDate.equals(selectedDate)) {
                    panel.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!clickDate.equals(selectedDate)) {
                    if (clickDate.getDayOfWeek().getValue() % 7 == 0 || clickDate.getDayOfWeek().getValue() % 7 == 6) {
                        panel.setBackground(new Color(248, 248, 250));
                    } else {
                        panel.setBackground(Color.WHITE);
                    }
                } else {
                    panel.setBackground(SELECTED_COLOR);
                }
            }

            @Override
            public void mouseClicked(MouseEvent evt) {
                selectedDate = clickDate;
                if (evt.getClickCount() == 2) {
                    // Double click switches to day view
                    currentView = "DAY";
                    setActiveTab(dayViewBtn);
                    cardLayout.show(viewContainer, "DAY");
                }
                refreshViews();
            }
        });

        return panel;
    }

    private JPanel createEventCard(Map<String, Object> event) {
        JPanel card = new JPanel(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.setBackground(Color.WHITE);

        String eventType = event.get("type").toString();
        Color eventColor = getEventColor(eventType);

        // Left color bar
        JPanel colorBar = new JPanel();
        colorBar.setPreferredSize(new Dimension(5, 0));
        colorBar.setBackground(eventColor);
        card.add(colorBar, BorderLayout.WEST);

        // Main content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Event title
        JLabel titleLabel = new JLabel(eventType);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Event location
        JLabel locationLabel = new JLabel(event.get("venue") + ", Room " + event.get("room"));
        locationLabel.setFont(SMALL_FONT);
        locationLabel.setForeground(new Color(108, 117, 125));
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Duration
        JLabel durationLabel = new JLabel(event.get("duration") + " min");
        durationLabel.setFont(SMALL_FONT);
        durationLabel.setForeground(new Color(108, 117, 125));
        durationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        contentPanel.add(locationLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        contentPanel.add(durationLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showEventDetails(event);
            }
        });

        return card;
    }

    private JPanel createDayViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Create header for the day
        JPanel dayHeaderPanel = new JPanel(new BorderLayout());
        dayHeaderPanel.setBackground(new Color(250, 251, 252));
        dayHeaderPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        dayHeaderPanel.add(new JLabel(selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d"))), BorderLayout.CENTER);

        // Create all-day events section
        JPanel allDayPanel = new JPanel(new BorderLayout());
        allDayPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        JLabel allDayLabel = new JLabel("ALL DAY");
        allDayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        allDayLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 0));
        allDayLabel.setForeground(new Color(108, 117, 125));
        allDayPanel.add(allDayLabel, BorderLayout.NORTH);

        // Create time slots panel (left side)
        JPanel timeSlotPanel = new JPanel(new GridLayout(24, 1));
        timeSlotPanel.setBackground(Color.WHITE);
        timeSlotPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        timeSlotPanel.setPreferredSize(new Dimension(70, 0));

        for (int hour = 0; hour < 24; hour++) {
            String timeText = String.format("%02d:00", hour);
            JLabel timeLabel = new JLabel(timeText);
            timeLabel.setFont(SMALL_FONT);
            timeLabel.setForeground(new Color(108, 117, 125));
            timeLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
            timeLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            timeSlotPanel.add(timeLabel);
        }

        // Create events panel (right side)
        JPanel eventsContainer = new JPanel();
        eventsContainer.setLayout(new BoxLayout(eventsContainer, BoxLayout.Y_AXIS));
        eventsContainer.setBackground(Color.WHITE);

        // Create hour grid for events
        JPanel hourGrid = new JPanel(new GridLayout(24, 1));
        hourGrid.setBackground(Color.WHITE);

        // Events mapped by hour (for simplicity)
        Map<Integer, List<Map<String, Object>>> eventsByHour = new HashMap<>();

        // Distribute events to hours
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
            JPanel hourPanel = new JPanel(new BorderLayout());
            hourPanel.setBackground(Color.WHITE);
            hourPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
            hourPanel.setPreferredSize(new Dimension(0, 60)); // Fixed height for hour slots

            // Highlight current hour
            LocalDate now = LocalDate.now();
            if (selectedDate.equals(now) && hour == java.time.LocalTime.now().getHour()) {
                hourPanel.setBackground(new Color(255, 252, 235));
            }

            // Add events for this hour
            JPanel eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            eventsPanel.setOpaque(false);
            eventsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            List<Map<String, Object>> hourEvents = eventsByHour.get(hour);
            if (hourEvents != null && !hourEvents.isEmpty()) {
                for (Map<String, Object> event : hourEvents) {
                    JPanel eventBlock = createDayViewEventBlock(event);
                    eventsPanel.add(eventBlock);
                    eventsPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Space between blocks
                }
            }

            hourPanel.add(eventsPanel, BorderLayout.CENTER);
            hourGrid.add(hourPanel);
        }

        eventsContainer.add(hourGrid);

        // Main panel with scroll capability
        JPanel dayViewContent = new JPanel(new BorderLayout());
        dayViewContent.add(timeSlotPanel, BorderLayout.WEST);
        dayViewContent.add(eventsContainer, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(dayViewContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // Smoother scrolling

        // Combine components
        JPanel headerSection = new JPanel(new BorderLayout());
        headerSection.add(dayHeaderPanel, BorderLayout.NORTH);
        headerSection.add(allDayPanel, BorderLayout.CENTER);

        panel.add(headerSection, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Scroll to 8 AM by default
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(8 * 60); // Scroll to 8 AM position
        });

        return panel;
    }

    private JPanel createDayViewEventBlock(Map<String, Object> event) {
        String eventType = event.get("type").toString();
        Color eventColor = getEventColor(eventType);

        JPanel block = new JPanel();
        block.setLayout(new BorderLayout());
        block.setBackground(Color.WHITE);
        block.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(eventColor, 2, false),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Event title
        JLabel titleLabel = new JLabel(eventType);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(TEXT_COLOR);

        // Event details
        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        detailsPanel.setOpaque(false);

        JLabel venueLabel = new JLabel(event.get("venue").toString());
        venueLabel.setFont(SMALL_FONT);
        venueLabel.setForeground(new Color(108, 117, 125));

        JLabel separatorLabel = new JLabel("•");
        separatorLabel.setFont(SMALL_FONT);
        separatorLabel.setForeground(new Color(108, 117, 125));

        JLabel roomLabel = new JLabel("Room " + event.get("room"));
        roomLabel.setFont(SMALL_FONT);
        roomLabel.setForeground(new Color(108, 117, 125));

        JLabel durationLabel = new JLabel(event.get("duration") + " min");
        durationLabel.setFont(SMALL_FONT);
        durationLabel.setForeground(new Color(108, 117, 125));

        detailsPanel.add(venueLabel);
        detailsPanel.add(separatorLabel);
        detailsPanel.add(roomLabel);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(detailsPanel);

        block.add(contentPanel, BorderLayout.CENTER);

        // Add duration label on the right
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(durationLabel, BorderLayout.NORTH);
        block.add(rightPanel, BorderLayout.EAST);

        // Add hover effect and click listener
        block.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                block.setBackground(new Color(245, 247, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                block.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showEventDetails(event);
            }
        });

        return block;
    }

    private void showEventDetails(Map<String, Object> event) {
        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Event Details");
        detailsDialog.setSize(450, 300);
        detailsDialog.setLocationRelativeTo(null);
        detailsDialog.setModal(true);
        detailsDialog.setLayout(new BorderLayout());

        // Header with event type
        String eventType = event.get("type").toString();
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(getEventColor(eventType));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel headerLabel = new JLabel(eventType);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(Color.WHITE);

        // Create detail rows
        addDetailRow(detailsPanel, "Venue:", event.get("venue").toString());
        addDetailRow(detailsPanel, "Room:", event.get("room").toString());
        addDetailRow(detailsPanel, "Duration:", event.get("duration") + " minutes");
        addDetailRow(detailsPanel, "Start Date:", formatTimestamp((Timestamp)event.get("startDate")));
        addDetailRow(detailsPanel, "End Date:", formatTimestamp((Timestamp)event.get("endDate")));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 20));

        JButton closeButton = new JButton("Close");
        closeButton.setFont(BUTTON_FONT);
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setPreferredSize(new Dimension(100, 36));
        closeButton.addActionListener(e -> detailsDialog.dispose());

        buttonPanel.add(closeButton);

        // Add components to dialog
        detailsDialog.add(headerPanel, BorderLayout.NORTH);
        detailsDialog.add(detailsPanel, BorderLayout.CENTER);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }

    private void addDetailRow(JPanel container, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setPreferredSize(new Dimension(100, 25));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(REGULAR_FONT);

        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);

        container.add(rowPanel);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return timestamp.toLocalDateTime().format(
                java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")
        );
    }

    private void loadMarketingEvents() {
        try (Connection connection = myJDBC.getConnection()) {
            if (connection != null) {
                String query = "SELECT * FROM marketing_events WHERE startDate BETWEEN ? AND ?";
                PreparedStatement pstmt = connection.prepareStatement(query);

                // Set date range for the current month
                LocalDate startDate = displayedYearMonth.atDay(1);
                LocalDate endDate = displayedYearMonth.atEndOfMonth();

                pstmt.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
                pstmt.setTimestamp(2, Timestamp.valueOf(endDate.atTime(23, 59, 59)));

                ResultSet rs = pstmt.executeQuery();
                eventsByDate.clear();

                while (rs.next()) {
                    LocalDate eventDate = rs.getTimestamp("startDate").toLocalDateTime().toLocalDate();
                    Map<String, Object> event = new HashMap<>();
                    event.put("type", rs.getString("type"));
                    event.put("room", rs.getInt("room"));
                    event.put("venue", rs.getString("venue"));
                    event.put("duration", rs.getInt("duration"));
                    event.put("startDate", rs.getTimestamp("startDate"));
                    event.put("endDate", rs.getTimestamp("endDate"));

                    eventsByDate.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshViews() {
        updateHeaderLabel();
        loadMarketingEvents();

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