// This class represents a popup dropdown calendar for date selection
package com.healthbuddy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class CalendarPopup {
    private Calendar calendar; 
    private JWindow calendarPopup; 
    private JPanel calendarPanel; 

     //Constructor to initialize the CalendarPopup.
    public CalendarPopup(JTextField dateField, User user) {
        calendar = Calendar.getInstance(); 
        initCalendarPopup(dateField, user); 
    }


    private void initCalendarPopup(JTextField dateField, User user) {
        calendarPopup = new JWindow(); 
        calendarPopup.setLayout(new BorderLayout());
        calendarPopup.setSize(dateField.getWidth(), 250);
        calendarPopup.setLocation(
            dateField.getLocationOnScreen().x,
            dateField.getLocationOnScreen().y + dateField.getHeight()
        );

        // Create the header with month navigation
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel monthLabel = new JLabel();
        monthLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        updateMonthLabel(monthLabel);

        // Button to navigate to the previous month
        JButton prevButton = new JButton("<");
        prevButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        prevButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendarPanel(dateField, user, monthLabel);
        });

        // Button to navigate to the next month
        JButton nextButton = new JButton(">");
        nextButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        nextButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendarPanel(dateField, user, monthLabel);
        });

        // Add components to the header panel
        headerPanel.add(prevButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        // Create the panel to hold the calendar grid
        calendarPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        calendarPanel.setBackground(new Color(240, 248, 255));
        updateCalendarPanel(dateField, user, monthLabel);

        // Add the header and calendar grid to the popup
        calendarPopup.add(headerPanel, BorderLayout.NORTH);
        calendarPopup.add(calendarPanel, BorderLayout.CENTER);
    }


    private void updateMonthLabel(JLabel monthLabel) {
        monthLabel.setText(String.format("%1$tB %1$tY", calendar));
    }

    // Updates the calendar panel to display the days of the current month.
    private void updateCalendarPanel(JTextField dateField, User user, JLabel monthLabel) {
        calendarPanel.removeAll();
        updateMonthLabel(monthLabel);

        // Add day of week headers
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, JLabel.CENTER);
            dayLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            calendarPanel.add(dayLabel);
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1); 
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar today = Calendar.getInstance();

        // Add empty cells for the days before the first day of the month
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel());
        }

        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.connect();

            // Add the day cells
            for (int day = 1; day <= daysInMonth; day++) {
                int currentDay = day;
                JLabel dayLabel = new JLabel(String.valueOf(currentDay), JLabel.CENTER);
                dayLabel.setOpaque(true);
                dayLabel.setPreferredSize(new Dimension(dateField.getWidth() / 7, 30));

                // Format the date as MM/DD/YYYY
                String date = String.format("%1$tm/%2$02d/%1$tY", calendar, currentDay);
                boolean hasData = dbManager.checkDailyHabitExists(user.getUsername(), date);

                // Highlight days with data
                if (hasData) {
                    dayLabel.setBackground(Color.LIGHT_GRAY);
                } else {
                    dayLabel.setBackground(Color.WHITE);
                }

                // Highlight today's date
                if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    currentDay == today.get(Calendar.DAY_OF_MONTH)) {
                    dayLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 0), 2));
                } else {
                    dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                }

                // Add a click listener to select the date
                dayLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        dayLabel.setBackground(new Color(100, 149, 237));
                        dayLabel.setForeground(Color.WHITE);
                        dateField.setText(String.format("%1$tm/%2$02d/%1$tY", calendar, currentDay));

                        // Load existing habit data into the main window
                        DailyHabitSetting settingWindow = (DailyHabitSetting) SwingUtilities.getWindowAncestor(dateField);
                        if (settingWindow != null) {
                            settingWindow.loadDailyHabitData(dateField.getText());
                        }

                        // Close the popup after a short delay
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                calendarPopup.dispose();
                            }
                        }, 500);
                    }
                });

                calendarPanel.add(dayLabel);
            }

            dbManager.closeConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error checking habit data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        calendarPanel.revalidate(); 
        calendarPanel.repaint();
    }

    public void show() {
        calendarPopup.setVisible(true);
    }
}
