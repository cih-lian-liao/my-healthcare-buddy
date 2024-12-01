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
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.DefaultCategoryDataset;

public class DataAnalysisPage extends JFrame {
    private User user;
    private HomePage homePage;
    private Color primaryColor = new Color(100, 149, 237);
    private Color backgroundColor = new Color(240, 248, 255);
    private JPanel chartPanel;
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
        JPanel chartsPanel = new JPanel(new BorderLayout());
        chartsPanel.setOpaque(false);
        chartsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Chart Panel
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor), "Health Metrics Chart"));

        chartsPanel.add(chartPanel, BorderLayout.CENTER);
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
                "Last Week", "Last Month", "Last 3 Months"
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

            // Count total data points for date label handling
            int dataPoints = 0;
            while (rs.next()) {
                dataPoints++;
                String date = rs.getString("date");
                double value = rs.getDouble(2);
                dataset.addValue(value, selectedMetric, date);

                // Add target weight line if metric is weight
                if (selectedMetric.equals("Weight")) {
                    double targetWeight = dbManager.getTargetValue(user.getUsername(), "weight");
                    if (targetWeight > 0) {
                        dataset.addValue(targetWeight, "Target Weight", date);
                    }
                }
            }

            if (dataset.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No data available for the selected period",
                        "No Data",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
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

            // Main metric line
            renderer.setSeriesShapesVisible(0, true);
            renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
            renderer.setSeriesPaint(0, primaryColor);
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));

            // Target weight line if present
            if (selectedMetric.equals("Weight")) {
                renderer.setSeriesShapesVisible(1, false); // No points on target line
                renderer.setSeriesPaint(1, new Color(220, 20, 60)); // Red color
                renderer.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f)); // Dashed line
            }

            plot.setRenderer(renderer);
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            plot.setDomainGridlinePaint(Color.LIGHT_GRAY);

            // Handle date labels based on number of data points
            // Handle date labels based on number of data points
            CategoryAxis domainAxis = plot.getDomainAxis();
            if (dataPoints > 15) {
                int skipFactor = dataPoints / 10; // Show approximately 10 labels
                domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

                // Ensure we show at least first, last, and some intermediate dates
                for (int i = 0; i < dataset.getColumnCount(); i++) {
                    if (i != 0 && i != dataset.getColumnCount() - 1 && i % skipFactor != 0) {
                        // Hide labels except first, last, and every skipFactor-th label
                        domainAxis.setTickLabelPaint(dataset.getColumnKey(i), new Color(0, 0, 0, 0));
                    }
                }
            } else {
                domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            }

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setTickLabelFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 500));

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

                // Add data to list first to avoid ResultSet issues
                while (rs.next()) {
                    String date = rs.getString("date");
                    String value;
                    if (selectedMetric.equals("Blood Pressure")) {
                        value = rs.getString(2); // Get blood pressure as string
                    } else {
                        value = String.format("%.2f", rs.getDouble(2));
                    }
                    writer.printf("%s,%s,%s%n", date, selectedMetric, value);
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