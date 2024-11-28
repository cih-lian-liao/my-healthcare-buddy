package com.healthbuddy;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import java.sql.*;

public class DataAnalysisPage extends JFrame {
    private User user;
    private HomePage homePage;
    private Color primaryColor = new Color(100, 149, 237);
    private Color backgroundColor = new Color(240, 248, 255);
    private JPanel chartPanel;
    private JPanel compareChartPanel;
    private JComboBox<String> metricCombo;
    private JComboBox<String> timeRangeCombo;

    public DataAnalysisPage(User user, HomePage homePage) {
        this.user = user;
        this.homePage = homePage;
        setupUI();
    }

    private void setupUI() {
        setTitle("My Healthcare Buddy - Data Analysis");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Control Panel at top
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Charts Panel in center
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left Chart Panel
        chartPanel = new JPanel();
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor), "Health Metrics Chart"));

        // Right Compare Chart Panel
        compareChartPanel = new JPanel();
        compareChartPanel.setBackground(Color.WHITE);
        compareChartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor), "Comparison Chart"));

        chartsPanel.add(chartPanel);
        chartsPanel.add(compareChartPanel);
        mainPanel.add(chartsPanel, BorderLayout.CENTER);

        // Back button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> {
            this.dispose();
            if (homePage != null) {
                homePage.setVisible(true);
            }
        });
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        displayDefaultChart();
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Metric Selection
        JPanel metricPanel = new JPanel(new BorderLayout(5, 5));
        metricPanel.setOpaque(false);
        metricPanel.add(new JLabel("Select Metric:"), BorderLayout.NORTH);
        metricCombo = new JComboBox<>(new String[] {
                "Weight", "BMI", "Steps", "Blood Pressure", "Heart Rate"
        });
        metricPanel.add(metricCombo, BorderLayout.CENTER);

        // Time Range Selection
        JPanel timePanel = new JPanel(new BorderLayout(5, 5));
        timePanel.setOpaque(false);
        timePanel.add(new JLabel("Time Range:"), BorderLayout.NORTH);
        timeRangeCombo = new JComboBox<>(new String[] {
                "Last Week", "Last Month", "Last 3 Months", "Last Year"
        });
        timePanel.add(timeRangeCombo, BorderLayout.CENTER);

        // Buttons
        JButton updateButton = createStyledButton("Update Chart");
        JButton compareButton = createStyledButton("Compare");
        JButton exportButton = createStyledButton("Export Data");

        updateButton.addActionListener(e -> updateChart());
        compareButton.addActionListener(e -> compareData());
        exportButton.addActionListener(e -> exportData());

        // Add components to control panel
        controlPanel.add(metricPanel);
        controlPanel.add(timePanel);
        controlPanel.add(updateButton);
        controlPanel.add(compareButton);
        controlPanel.add(exportButton);

        return controlPanel;
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

        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 30));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        return button;
    }

    private void displayDefaultChart() {
        // Create a sample dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(70.5, "Weight", "Jan");
        dataset.addValue(70.2, "Weight", "Feb");
        dataset.addValue(69.8, "Weight", "Mar");
        dataset.addValue(69.5, "Weight", "Apr");

        JFreeChart chart = ChartFactory.createLineChart(
                "Weight Progress",
                "Month",
                "Weight (kg)",
                dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        this.chartPanel.setLayout(new BorderLayout());
        this.chartPanel.add(chartPanel, BorderLayout.CENTER);
    }

    private void updateChart() {
        String selectedMetric = (String) metricCombo.getSelectedItem();
        String timeRange = (String) timeRangeCombo.getSelectedItem();
        // TODO: Implement actual data fetching and chart updating
        JOptionPane.showMessageDialog(this,
                "Updating chart for " + selectedMetric + " over " + timeRange,
                "Update Chart",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void compareData() {
        // TODO: Implement comparison functionality
        JOptionPane.showMessageDialog(this,
                "Comparison feature coming soon!",
                "Compare Data",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportData() {
        // TODO: Implement export functionality
        JOptionPane.showMessageDialog(this,
                "Export feature coming soon!",
                "Export Data",
                JOptionPane.INFORMATION_MESSAGE);
    }
}