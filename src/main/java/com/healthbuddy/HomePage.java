package com.healthbuddy;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {
    private User user;
    private Color primaryColor = new Color(100, 149, 237);
    private Color backgroundColor = new Color(240, 248, 255);

    public HomePage(User user) {
        this.user = user;
        setupUI();
    }

    private void setupUI() {
        setTitle("My Healthcare Buddy - Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel with background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Welcome panel
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        welcomePanel.setOpaque(false);

        String name = user.getUsername(); // Use username if name is not set
        if (user.getProfile() != null && user.getProfile().getName() != null
                && !user.getProfile().getName().isEmpty()) {
            name = user.getProfile().getName();
        }

        JLabel welcomeLabel = new JLabel("Welcome back,");
        welcomeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        welcomeLabel.setForeground(primaryColor);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        nameLabel.setForeground(new Color(70, 70, 70));

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(nameLabel);

        // Logout button
        JButton logoutButton = new JButton("Logout") {
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

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics metrics = g2.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        logoutButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(welcomePanel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Menu Panel
        JPanel menuPanel = createMenuPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[][] menuItems = {
                { "Health Data Entry", "Track your daily health metrics" },
                { "Daily Habits", "Monitor your daily activities" },
                { "Data Analysis", "View your health trends" },
                { "Profile Settings", "Update your profile information" }
        };

        for (String[] item : menuItems) {
            menuPanel.add(createMenuButton(item[0], item[1]));
        }

        return menuPanel;
    }

    private JButton createMenuButton(String title, String description) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(primaryColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.setColor(Color.WHITE);
                g2.setFont(UIConfig.BUTTON_FONT);

                FontMetrics metrics = g2.getFontMetrics();
                int titleX = (getWidth() - metrics.stringWidth(title)) / 2;
                int titleY = (getHeight() / 2) - 10;

                g2.drawString(title, titleX, titleY);

                g2.setFont(UIConfig.REGULAR_FONT);
                metrics = g2.getFontMetrics();
                int descX = (getWidth() - metrics.stringWidth(description)) / 2;
                int descY = (getHeight() / 2) + 20;

                g2.drawString(description, descX, descY);

                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(200, 100));
        button.addActionListener(e -> handleMenuClick(title));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        return button;
    }

    private void handleMenuClick(String menuTitle) {
        switch (menuTitle) {
            case "Health Data Entry":
                this.openHealthDataEntry(user);
                break;
            case "Daily Habits":
                this.openDailyHabitSettingPage(user);
                break;
            case "Data Analysis":
                DataAnalysisPage dataAnalysisPage = new DataAnalysisPage(user, this);
                this.setVisible(false);
                dataAnalysisPage.setVisible(true);
                break;
            case "Profile Settings":
                ProfileSettingsPage profilePage = new ProfileSettingsPage(user, this);
                this.setVisible(false);
                profilePage.setVisible(true);
                break;
        }
    }

    private void logout() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginPage().setVisible(true);
        }
    }

    private void openPofileSettingsPage(User user) {
        this.dispose();
        new ProfileSettingsPage(user, this).setVisible(true);
    }

    private void openDailyHabitSettingPage(User user) {
        this.dispose();
        new DailyHabitSetting(user).setVisible(true);

    private void openHealthDataEntry(User user) {
        this.dispose();
        new HealthDataEntryPage(user, this).setVisible(true);

    }
}
