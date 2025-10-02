package com.healthbuddy;

import javax.swing.*;
import java.awt.*;

import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private DatabaseManager dbManager;
    private Color primaryColor = new Color(100, 149, 237); // Cornflower blue
    private Color backgroundColor = new Color(240, 248, 255); // Alice blue

    public LoginPage() {
        dbManager = new DatabaseManager();
        setupUI();
        try {
            dbManager.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!");
        }
    }

    private void setupUI() {
        setTitle("My Healthcare Buddy - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with background color
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Logo/Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("My Healthcare Buddy");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(primaryColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Your Personal Health Companion");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titlePanel, gbc);

        // Login Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formPanel.add(userLabel, formGbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formPanel.add(usernameField, formGbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        formPanel.add(passLabel, formGbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        formGbc.gridx = 0;
        formGbc.gridy = 3;
        formPanel.add(passwordField, formGbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> authenticateUser());

        JButton signUpButton = createStyledButton("Sign Up");
        signUpButton.addActionListener(e -> redirectToSignUp());

        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        formGbc.gridx = 0;
        formGbc.gridy = 4;
        formGbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(buttonPanel, formGbc);

        // Add form panel to main panel
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 10, 10, 10);
        mainPanel.add(formPanel, gbc);

        // Add main panel to frame
        add(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(primaryColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(primaryColor.brighter());
                } else {
                    g2.setColor(primaryColor);
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

        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(100, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        return button;
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (validateCredentials(username, password)) {
                User user = new User(username, password);
                openHomePage(user);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password. Please sign up if you don't have an account.",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error occurred",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateCredentials(String username, String password) throws SQLException {
        String sql = "SELECT password, salt FROM users WHERE username = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String salt = rs.getString("salt");
                return PasswordSecurity.verifyPassword(password, storedPassword, salt);
            }
        }
        return false;
    }

    private void redirectToSignUp() {
        this.dispose();
        new SignUpPage();
        
    }

    private void openHomePage(User user) {
        this.dispose();
        new HomePage(user).setVisible(true);
    }
}