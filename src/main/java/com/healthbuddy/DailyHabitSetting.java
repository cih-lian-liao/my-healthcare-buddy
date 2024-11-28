package com.healthbuddy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate; // 使用 java.time.LocalDate
import java.time.format.DateTimeFormatter;
import java.sql.ResultSet;

public class DailyHabitSetting extends JFrame {
    private User user;
    private JTextField dateField;
    private JTextField waterIntakeField;
    private JTextField exerciseField;
    private JTextField sleepHoursField;

    public DailyHabitSetting(User user) {
        this.user = user;
        setupUI();
        setDefaultDate();
    }

    private void setupUI() {
        setTitle("Daily Habit Setting Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Daily Habit Setting Page", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(dateLabel, gbc);

        dateField = new JTextField(20);
        dateField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        dateField.setEditable(false);
        dateField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CalendarPopup popup = new CalendarPopup(dateField, selectedDate -> {
                    loadDailyHabitData(selectedDate);
                });
                popup.show();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(dateField, gbc);

        waterIntakeField = addRow(mainPanel, gbc, 2, "Water Intake (Integer)*:");
        exerciseField = addRow(mainPanel, gbc, 3, "Exercise (Integer)*:");
        sleepHoursField = addRow(mainPanel, gbc, 4, "Sleep Hours (Integer)*:");

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> handleBack());
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> handleSave());

        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private JTextField addRow(JPanel panel, GridBagConstraints gbc, int row, String label) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(fieldLabel, gbc);

        JTextField textField = new JTextField(20);
        textField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(textField, gbc);

        return textField;
    }

    private void setDefaultDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        dateField.setText(LocalDate.now().format(formatter));
        loadDailyHabitData(dateField.getText());
    }

    public void loadDailyHabitData(String date) {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.connect();

            ResultSet rs = dbManager.getDailyHabit(user.getUsername(), date);
            if (rs != null && rs.next()) {
                waterIntakeField.setText(String.valueOf(rs.getInt("water_intake")));
                exerciseField.setText(rs.getString("diet"));
                sleepHoursField.setText(String.valueOf(rs.getInt("sleep_hours")));
            } else {
                waterIntakeField.setText("");
                exerciseField.setText("");
                sleepHoursField.setText("");
            }

            dbManager.closeConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleBack() {
        this.dispose();
        new HomePage(user).setVisible(true);
    }

    private void handleSave() {
        String date = dateField.getText();
        String waterIntake = waterIntakeField.getText();
        String exercise = exerciseField.getText();
        String sleepHours = sleepHoursField.getText();

        if (date.isEmpty() || waterIntake.isEmpty() || exercise.isEmpty() || sleepHours.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.connect();
            boolean success = dbManager.insertDailyHabit(
                    user.getUsername(),
                    date,
                    Integer.parseInt(waterIntake),
                    exercise,
                    Integer.parseInt(sleepHours)
            );
            dbManager.closeConnection();

            if (success) {
                JOptionPane.showMessageDialog(this, "Data saved successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save data!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
