package com.healthbuddy;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HealthDataEntryPage extends JFrame {
    private User user;
    private HomePage homePage;
    private Color primaryColor = new Color(100, 149, 237);
    private Color backgroundColor = new Color(240, 248, 255);

    // Input fields
    private JTextField weightField;
    private JTextField stepsField;
    private JTextField bloodPressureField;
    private JTextField heartRateField;
    private JTextField dateField; // 日期輸入框
    private JButton datePickerButton; // 日期選擇按鈕
    private JLabel bmiLabel;

    private boolean isDataExists = false; // 標記是否已存在數據

    public HealthDataEntryPage(User user, HomePage homePage) {
        this.user = user;
        this.homePage = homePage;
        setupUI();
        setDefaultDate(); // 設置預設日期為今天
        loadExistingData(); // 加載當天數據（如果存在）
    }

    private void setupUI() {
        setTitle("My Healthcare Buddy - Health Data Entry");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel with background
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title label
        JLabel titleLabel = new JLabel("Health Data Entry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(primaryColor);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        // Date
        gbc.gridwidth = 1;
        gbc.gridy++;
        mainPanel.add(new JLabel("Date (MM/DD/YYYY):"), gbc);
        dateField = new JTextField(20);
        dateField.setEditable(false); // 禁止用戶手動輸入
        gbc.gridx = 1;
        mainPanel.add(dateField, gbc);

        // Date picker button
        datePickerButton = new JButton("Pick Date");
        datePickerButton.addActionListener(e -> {
            showDatePicker();
            loadExistingData(); // 選擇日期後加載數據
        });
        gbc.gridx = 2;
        mainPanel.add(datePickerButton, gbc);

        // Weight
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Weight (kg):"), gbc);
        weightField = new JTextField(20);
        weightField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateBMI();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateBMI();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateBMI();
            }
        });
        gbc.gridx = 1;
        mainPanel.add(weightField, gbc);

        // Steps
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Steps:"), gbc);
        stepsField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(stepsField, gbc);

        // Blood Pressure
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Blood Pressure (e.g., 120/80):"), gbc);
        bloodPressureField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(bloodPressureField, gbc);

        // Heart Rate
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Heart Rate (bpm):"), gbc);
        heartRateField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(heartRateField, gbc);

        // BMI
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("BMI:"), gbc);
        bmiLabel = new JLabel("N/A");
        gbc.gridx = 1;
        mainPanel.add(bmiLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveHealthData());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            this.dispose();
            if (homePage != null) {
                homePage.setVisible(true);
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void setDefaultDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        dateField.setText(formatter.format(new Date())); // 設置為今天的日期
    }

    private void showDatePicker() {
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(editor);

        int result = JOptionPane.showOptionDialog(
                this,
                dateSpinner,
                "Select Date",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);

        if (result == JOptionPane.OK_OPTION) {
            Date selectedDate = (Date) dateSpinner.getValue();
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            dateField.setText(formatter.format(selectedDate));
        }
    }

    private void loadExistingData() {
        String date = dateField.getText().trim();
        if (date.isEmpty()) return;

        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.connect();

            String query = "SELECT * FROM health_data WHERE username = ? AND date = ?";
            try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, date);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    isDataExists = true;
                    weightField.setText(String.valueOf(rs.getDouble("weight")));
                    stepsField.setText(String.valueOf(rs.getInt("steps")));
                    bloodPressureField.setText(rs.getString("blood_pressure"));
                    heartRateField.setText(String.valueOf(rs.getInt("heart_rate")));
                    bmiLabel.setText(String.format("%.2f", rs.getDouble("bmi")));
                } else {
                    isDataExists = false;
                    weightField.setText("");
                    stepsField.setText("");
                    bloodPressureField.setText("");
                    heartRateField.setText("");
                    bmiLabel.setText("N/A");
                }
            }

            dbManager.closeConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveHealthData() {
        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            int steps = Integer.parseInt(stepsField.getText().trim());
            String bloodPressure = bloodPressureField.getText().trim();
            int heartRate = Integer.parseInt(heartRateField.getText().trim());
            String date = dateField.getText().trim();

            if (date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a date.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DatabaseManager dbManager = new DatabaseManager();
            dbManager.connect();

            double height = dbManager.getUser(user.getUsername()).getProfile().getHeight();
            double bmi = weight / Math.pow(height / 100, 2);

            String sql;
            if (isDataExists) {
                sql = "UPDATE health_data SET weight = ?, bmi = ?, steps = ?, blood_pressure = ?, heart_rate = ? WHERE username = ? AND date = ?";
            } else {
                sql = "INSERT INTO health_data (weight, bmi, steps, blood_pressure, heart_rate, username, date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            }

            try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
                pstmt.setDouble(1, weight);
                pstmt.setDouble(2, bmi);
                pstmt.setInt(3, steps);
                pstmt.setString(4, bloodPressure);
                pstmt.setInt(5, heartRate);
                pstmt.setString(6, user.getUsername());
                pstmt.setString(7, date);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Health data saved successfully!");
            }

            dbManager.closeConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please ensure all fields are filled correctly.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateBMI() {
        try {
            double weight = Double.parseDouble(weightField.getText().trim());

            DatabaseManager dbManager = new DatabaseManager();
            dbManager.connect();
            double height = dbManager.getUser(user.getUsername()).getProfile().getHeight();
            dbManager.closeConnection();

            if (height > 0) {
                double heightInMeters = height / 100;
                double bmi = weight / (heightInMeters * heightInMeters);
                bmiLabel.setText(String.format("%.2f", bmi));
            } else {
                bmiLabel.setText("N/A");
            }
        } catch (NumberFormatException e) {
            bmiLabel.setText("N/A");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}