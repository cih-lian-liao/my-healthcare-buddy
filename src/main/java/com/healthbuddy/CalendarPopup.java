package com.healthbuddy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class CalendarPopup {
    private Calendar calendar;
    private JWindow calendarPopup;
    private JPanel calendarPanel;
    private Consumer<String> onDateSelected;

    
    public CalendarPopup(JTextField parentField, Consumer<String> onDateSelected) {
        this.calendar = Calendar.getInstance();
        this.onDateSelected = onDateSelected;
        initCalendarPopup(parentField);
    }

    private void initCalendarPopup(JTextField parentField) {
        calendarPopup = new JWindow();
        calendarPopup.setLayout(new BorderLayout());
        calendarPopup.setSize(parentField.getWidth(), 250);
        calendarPopup.setLocation(
                parentField.getLocationOnScreen().x,
                parentField.getLocationOnScreen().y + parentField.getHeight()
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
            updateCalendarPanel(parentField, monthLabel);
        });

        // Button to navigate to the next month
        JButton nextButton = new JButton(">");
        nextButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        nextButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendarPanel(parentField, monthLabel);
        });

        // Add components to the header panel
        headerPanel.add(prevButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        // Create the panel to hold the calendar grid
        calendarPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        calendarPanel.setBackground(new Color(240, 248, 255));
        updateCalendarPanel(parentField, monthLabel);

        // Add the header and calendar grid to the popup
        calendarPopup.add(headerPanel, BorderLayout.NORTH);
        calendarPopup.add(calendarPanel, BorderLayout.CENTER);
    }

    private void updateMonthLabel(JLabel monthLabel) {
        monthLabel.setText(String.format("%1$tB %1$tY", calendar));
    }

    private void updateCalendarPanel(JTextField parentField, JLabel monthLabel) {
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

        // Add the day cells
        for (int day = 1; day <= daysInMonth; day++) {
            int currentDay = day;
            JLabel dayLabel = new JLabel(String.valueOf(currentDay), JLabel.CENTER);
            dayLabel.setOpaque(true);
            dayLabel.setPreferredSize(new Dimension(parentField.getWidth() / 7, 30));

            // Format the date as MM/DD/YYYY
            String date = String.format("%1$tm/%2$02d/%1$tY", calendar, currentDay);

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
                    parentField.setText(date);

                    if (onDateSelected != null) {
                        onDateSelected.accept(date);
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

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    public void show() {
        calendarPopup.setVisible(true);
    }
}
