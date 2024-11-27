package com.healthbuddy;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfileSettingsPage extends JFrame {
    private User user;
    private HomePage homePage;
    private Color primaryColor = new Color(100, 149, 237);
    private Color backgroundColor = new Color(240, 248, 255);

    // Input fields
    private JTextField nameField;
    private JTextField ageField;
    private JComboBox<String> genderComboBox;
    private JTextField heightField;
    private JTextField targetWeightField;

    public ProfileSettingsPage(User user, HomePage homePage) {
        this.user = user;
        this.homePage = homePage;
        setupUI();
    }

    private void setupUI() {
        setTitle("My Healthcare Buddy - Profile Settings");
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
        JLabel titleLabel = new JLabel("Profile Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(primaryColor);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        // Name
        gbc.gridwidth = 1;
        gbc.gridy++;
        mainPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Age
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Age:"), gbc);
        ageField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(ageField, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Gender:"), gbc);
        genderComboBox = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        gbc.gridx = 1;
        mainPanel.add(genderComboBox, gbc);

        // Height
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Height (cm):"), gbc);
        heightField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(heightField, gbc);

        // Target Weight
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Target Weight (kg):"), gbc);
        targetWeightField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(targetWeightField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveProfileData());
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
        loadProfileData();
    }

    private void loadProfileData() {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.connect();

            User loadedUser = dbManager.getUser(user.getUsername());
            if (loadedUser != null) {
                ProfileData profile = loadedUser.getProfile();
                nameField.setText(profile.getName());
                ageField.setText(String.valueOf(profile.getAge()));
                genderComboBox.setSelectedItem(profile.getGender());
                heightField.setText(String.valueOf(profile.getHeight()));
                targetWeightField.setText(String.valueOf(profile.getTargetWeight()));
            }

            dbManager.closeConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveProfileData() {
        try {
            // Retrieve input data
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String gender = (String) genderComboBox.getSelectedItem();
            double height = Double.parseDouble(heightField.getText().trim());
            double targetWeight = Double.parseDouble(targetWeightField.getText().trim());

            // Connect to the database
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.connect();

            // Update the profile data
            String updateSql = "UPDATE users SET name = ?, age = ?, gender = ?, height = ?, target_weight = ? WHERE username = ?";
            try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(updateSql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, age);
                pstmt.setString(3, gender);
                pstmt.setDouble(4, height);
                pstmt.setDouble(5, targetWeight);
                pstmt.setString(6, user.getUsername());
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update profile. Please try again.");
                }
            }
            dbManager.closeConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please ensure all fields are filled correctly.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
