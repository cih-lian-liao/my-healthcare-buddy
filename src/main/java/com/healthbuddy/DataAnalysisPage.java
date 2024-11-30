package com.healthbuddy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.DefaultCategoryDataset;

public class DataAnalysisPage extends JFrame {
    private User user;
    private HomePage homePage;
    private Color primaryColor = new Color(100, 149, 237);
    private Color backgroundColor = new Color(240, 248, 255);
    private JPanel chartPanel;
    private JPanel compareChartPanel;
    private JComboBox<String> metricCombo;
    private JComboBox<String> timeRangeCombo;
    private DatabaseManager dbManager;

    public DataAnalysisPage(User user, HomePage homePage) {
        this.user = user;
        this.homePage = homePage;
        this.dbManager = new DatabaseManager();
        try {
            dbManager.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setupUI();
        // Set default selections and show chart
        metricCombo.setSelectedItem("Weight");
        timeRangeCombo.setSelectedItem("Last Week");
        updateChart();
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
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor), "Health Metrics Chart"));

        // Right Compare Chart Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor), "Comparison Chart"));

        // Create a separate panel for comparison controls
        JPanel comparisonControlsPanel = createComparisonPanel();
        rightPanel.add(comparisonControlsPanel, BorderLayout.NORTH);

        // Create a panel for the comparison chart
        compareChartPanel = new JPanel(new BorderLayout());
        compareChartPanel.setBackground(Color.WHITE);
        rightPanel.add(compareChartPanel, BorderLayout.CENTER);

        chartsPanel.add(chartPanel);
        chartsPanel.add(rightPanel);
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
        setupSelectionListeners();
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

        // Export Button
        JButton exportButton = createStyledButton("Export Data");
        exportButton.addActionListener(e -> exportData());

        // Add components to control panel
        controlPanel.add(metricPanel);
        controlPanel.add(timePanel);
        controlPanel.add(exportButton);

        return controlPanel;
    }

    private JPanel createComparisonPanel() {
        JPanel comparisonPanel = new JPanel(new BorderLayout(5, 5));
        comparisonPanel.setOpaque(false);
        comparisonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Top controls panel
        JPanel controlsPanel = new JPanel(new BorderLayout(5, 5));
        controlsPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Compare with:");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        // Comparison time selector
        JComboBox<String> compareTimeCombo = new JComboBox<>(new String[] {
                "Last Week",
                "Last Month",
                "Last 3 Months",
                "Target Value"
        });

        controlsPanel.add(titleLabel, BorderLayout.WEST);
        controlsPanel.add(compareTimeCombo, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton compareButton = createStyledButton("Compare");
        JButton clearButton = createStyledButton("Clear");

        compareButton.addActionListener(e -> {
            String selected = (String) compareTimeCombo.getSelectedItem();
            if (selected != null) {
                updateComparisonChart(selected);
            }
        });

        clearButton.addActionListener(e -> clearComparisonChart());

        buttonPanel.add(compareButton);
        buttonPanel.add(clearButton);
        controlsPanel.add(buttonPanel, BorderLayout.EAST);

        comparisonPanel.add(controlsPanel, BorderLayout.NORTH);

        // Chart panel
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);
        compareChartPanel = chartPanel;
        comparisonPanel.add(chartPanel, BorderLayout.CENTER);

        return comparisonPanel;
    }

    private void clearComparisonChart() {
        compareChartPanel.removeAll();
        compareChartPanel.add(new JLabel("No comparison data selected", SwingConstants.CENTER));
        compareChartPanel.revalidate();
        compareChartPanel.repaint();
    }

    private void setupSelectionListeners() {
        metricCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateChart();
            }
        });

        timeRangeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateChart();
            }
        });
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

    private void updateChart() {
        String selectedMetric = (String) metricCombo.getSelectedItem();
        String timeRange = (String) timeRangeCombo.getSelectedItem();

        ResultSet rs = null;
        try {
            rs = dbManager.getHealthData(user.getUsername(), selectedMetric, timeRange);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            while (rs.next()) {
                String date = rs.getString("date");
                // Format date to show only MM/DD
                String formattedDate = date.substring(0, 5);

                double value;
                if (selectedMetric.equalsIgnoreCase("Blood Pressure")) {
                    value = parseBloodPressure(rs.getString(2));
                } else {
                    value = rs.getDouble(2);
                }
                dataset.addValue(value, selectedMetric, formattedDate);
            }

            JFreeChart chart = ChartFactory.createLineChart(
                    selectedMetric + " Progress",
                    "Date",
                    getYAxisLabel(selectedMetric),
                    dataset);

            // Customize chart appearance
            chart.setBackgroundPaint(Color.WHITE);

            CategoryPlot plot = chart.getCategoryPlot();
            LineAndShapeRenderer renderer = new LineAndShapeRenderer();

            // Show points on the line
            renderer.setSeriesShapesVisible(0, true);
            renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
            renderer.setSeriesPaint(0, primaryColor);
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));

            plot.setRenderer(renderer);
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            plot.setDomainGridlinePaint(Color.LIGHT_GRAY);

            // Customize the domain axis
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // Rotate labels
            domainAxis.setTickLabelFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

            // Customize the range axis
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setTickLabelFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(400, 300));

            this.chartPanel.removeAll();
            this.chartPanel.add(chartPanel, BorderLayout.CENTER);
            this.chartPanel.revalidate();
            this.chartPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error fetching data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateComparisonChart(String comparisonType) {
        String selectedMetric = (String) metricCombo.getSelectedItem();
        ResultSet rs = null;
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Current period data
            rs = dbManager.getHealthData(user.getUsername(), selectedMetric, "Last Week");
            while (rs.next()) {
                String date = rs.getString("date");
                String formattedDate = date.substring(0, 5);
                double value = rs.getDouble(2);
                dataset.addValue(value, "Current", formattedDate);
            }

            // Comparison data
            switch (comparisonType) {
                case "Previous Week":
                    rs = dbManager.getComparisonData(user.getUsername(), selectedMetric, 7);
                    while (rs.next()) {
                        String date = rs.getString("date");
                        String formattedDate = date.substring(0, 5);
                        double value = rs.getDouble(2);
                        dataset.addValue(value, "Previous", formattedDate);
                    }
                    break;

                case "Target Value":
                    // Add target line based on user's goals
                    double targetValue = dbManager.getTargetValue(user.getUsername(), selectedMetric);
                    rs = dbManager.getHealthData(user.getUsername(), selectedMetric, "Last Week");
                    while (rs.next()) {
                        String date = rs.getString("date");
                        String formattedDate = date.substring(0, 5);
                        dataset.addValue(targetValue, "Target", formattedDate);
                    }
                    break;
            }

            JFreeChart chart = ChartFactory.createLineChart(
                    selectedMetric + " Comparison",
                    "Date",
                    getYAxisLabel(selectedMetric),
                    dataset);

            customizeComparisonChart(chart);

            ChartPanel chartPanel = new ChartPanel(chart);
            compareChartPanel.removeAll();
            compareChartPanel.add(chartPanel, BorderLayout.CENTER);
            compareChartPanel.revalidate();
            compareChartPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error creating comparison chart: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void customizeComparisonChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();

        // Current period line (blue)
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
        renderer.setSeriesPaint(0, primaryColor);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        // Comparison line (red)
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShape(1, new Ellipse2D.Double(-4, -4, 8, 8));
        renderer.setSeriesPaint(1, new Color(220, 20, 60));
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    }

    private double parseBloodPressure(String bp) {
        String[] parts = bp.split("/");
        return Double.parseDouble(parts[0]); // Use systolic pressure
    }

    private String getYAxisLabel(String metric) {
        switch (metric.toLowerCase()) {
            case "weight":
                return "Weight (kg)";
            case "bmi":
                return "BMI";
            case "steps":
                return "Steps";
            case "blood pressure":
                return "Blood Pressure (mmHg)";
            case "heart rate":
                return "Heart Rate (bpm)";
            default:
                return metric;
        }
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Data");
        fileChooser.setSelectedFile(new File("health_data.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write header
                writer.println("Date,Metric,Value");

                String selectedMetric = (String) metricCombo.getSelectedItem();
                String timeRange = (String) timeRangeCombo.getSelectedItem();

                ResultSet rs = dbManager.getHealthData(user.getUsername(), selectedMetric, timeRange);
                while (rs.next()) {
                    String date = rs.getString("date");
                    double value = rs.getDouble(2);
                    writer.printf("%s,%s,%.2f%n", date, selectedMetric, value);
                }

                JOptionPane.showMessageDialog(this,
                        "Data exported successfully to " + file.getName(),
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException | SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error exporting data: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}