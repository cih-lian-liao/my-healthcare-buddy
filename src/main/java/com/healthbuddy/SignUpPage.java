package com.healthbuddy;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SignUpPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public SignUpPage() {
        UIConfig.setupUIDefaults();

        setTitle("Sign Up - My Healthcare Buddy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false); 

        // Set main panel layout
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(UIConfig.BACKGROUND_COLOR); 
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set window title
        JLabel titleLabel = new JLabel("Sign Up", JLabel.CENTER);
        titleLabel.setFont(UIConfig.TITLE_FONT);
        titleLabel.setForeground(UIConfig.PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainPanel.add(titleLabel, gbc);

        // Username label and input textbox
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(usernameField, gbc);

        // Password label and input textbox
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(passwordField, gbc);

        // Confirm password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(confirmPasswordField, gbc);

        // Sign Up button
        JButton signUpButton = createStyledButton("Sign Up");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        signUpButton.addActionListener(e -> handleSignUp());
        mainPanel.add(signUpButton, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(UIConfig.PRIMARY_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(UIConfig.PRIMARY_COLOR.brighter());
                } else {
                    g2.setColor(UIConfig.PRIMARY_COLOR);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                FontMetrics metrics = g2.getFontMetrics(getFont());
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setFont(UIConfig.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(100, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        return button;
    }

    private void handleSignUp() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.connect();

            if (dbManager.checkUsernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please try a new name.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean success = dbManager.insertUser(username, password);
                if (success) {
                    User user = dbManager.getUser(username);
                    if (user != null) {
                        dispose();
                        new HomePage(user).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to retrieve user data.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            dbManager.closeConnection();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Sign Up Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
