package com.healthbuddy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:health_buddy.db";
    private Connection connection;

    public void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");

            File dbFile = new File("health_buddy.db");
            boolean needsCreation = !dbFile.exists();

            connection = DriverManager.getConnection(DB_URL);

            if (needsCreation) {
                createTables();
                createDefaultData();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            throw new SQLException("SQLite JDBC driver not found");
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw e;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTables() {
        String[] createTableSQL = {
                "CREATE TABLE IF NOT EXISTS users (" +
                        "username TEXT PRIMARY KEY," +
                        "password TEXT NOT NULL," +
                        "name TEXT," +
                        "age INTEGER," +
                        "gender TEXT," +
                        "height REAL," +
                        "target_weight REAL" +
                        ")",
                "CREATE TABLE IF NOT EXISTS health_data (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT," +
                        "date TEXT," +
                        "weight REAL," +
                        "bmi REAL," +
                        "steps INTEGER," +
                        "blood_pressure TEXT," +
                        "heart_rate INTEGER," +
                        "FOREIGN KEY(username) REFERENCES users(username)" +
                        ")",
                "CREATE TABLE IF NOT EXISTS daily_habits (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT," +
                        "date TEXT," +
                        "water_intake INTEGER," +
                        "diet TEXT," +
                        "sleep_hours INTEGER," +
                        "FOREIGN KEY(username) REFERENCES users(username)" +
                        ")",
                "CREATE TABLE IF NOT EXISTS health_goals (" +
                        "username TEXT PRIMARY KEY," +
                        "target_weight REAL," +
                        "target_steps INTEGER," +
                        "target_water_intake INTEGER," +
                        "target_sleep_hours INTEGER," +
                        "FOREIGN KEY(username) REFERENCES users(username)" +
                        ")"
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : createTableSQL) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createDefaultData() {
        try (Statement stmt = connection.createStatement()) {
            // Insert test user if not exists
            stmt.execute("INSERT OR IGNORE INTO users (username, password, name, age, gender, height, target_weight) " +
                    "VALUES ('test', 'test123', 'Test User', 25, 'Male', 175.0, 70.0)");

            // Insert sample health data
            String[] healthData = {
                    "INSERT OR IGNORE INTO health_data (username, date, weight, bmi, steps, blood_pressure, heart_rate) VALUES "
                            +
                            "('test', '03/15/2024', 75.5, 24.6, 8500, '120/80', 72)",
                    "INSERT OR IGNORE INTO health_data (username, date, weight, bmi, steps, blood_pressure, heart_rate) VALUES "
                            +
                            "('test', '03/16/2024', 75.2, 24.5, 9000, '119/79', 71)",
                    "INSERT OR IGNORE INTO health_data (username, date, weight, bmi, steps, blood_pressure, heart_rate) VALUES "
                            +
                            "('test', '03/17/2024', 74.8, 24.4, 9500, '118/78', 70)",
                    "INSERT OR IGNORE INTO health_data (username, date, weight, bmi, steps, blood_pressure, heart_rate) VALUES "
                            +
                            "('test', '03/18/2024', 74.5, 24.3, 10000, '118/77', 69)",
                    "INSERT OR IGNORE INTO health_data (username, date, weight, bmi, steps, blood_pressure, heart_rate) VALUES "
                            +
                            "('test', '03/19/2024', 74.2, 24.2, 10500, '117/77', 68)",
                    "INSERT OR IGNORE INTO health_data (username, date, weight, bmi, steps, blood_pressure, heart_rate) VALUES "
                            +
                            "('test', '03/20/2024', 73.8, 24.1, 11000, '117/76', 67)"
            };

            for (String sql : healthData) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.err.println("Error creating default data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public boolean validateLogin(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return password.equals(storedPassword); // In production, use password hashing
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Insert username and password
    public boolean insertUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            throw e;
        }
    }

    public ResultSet getHealthData(String username, String metric, String timeRange) throws SQLException {
        String columnName;
        switch (metric.toLowerCase()) {
            case "weight":
                columnName = "weight";
                break;
            case "bmi":
                columnName = "bmi";
                break;
            case "steps":
                columnName = "steps";
                break;
            case "blood pressure":
                columnName = "blood_pressure";
                break;
            case "heart rate":
                columnName = "heart_rate";
                break;
            default:
                columnName = "weight";
        }

        int limit;
        switch (timeRange) {
            case "Last Week":
                limit = 7;
                break;
            case "Last Month":
                limit = 30;
                break;
            case "Last 3 Months":
                limit = 90;
                break;
            case "Last Year":
                limit = 365;
                break;
            default:
                limit = 30;
        }

        String dateFilter;
        switch (timeRange) {
            case "Last Week":
                dateFilter = "date >= date('now', '-7 days') AND date <= date('now')";
                break;
            case "Last Month":
                dateFilter = "date >= date('now', '-1 month') AND date <= date('now')";
                break;
            case "Last 3 Months":
                dateFilter = "date >= date('now', '-3 months') AND date <= date('now')";
                break;
            case "Last Year":
                dateFilter = "date >= date('now', '-1 year') AND date <= date('now')";
                break;
            default:
                dateFilter = "date >= date('now', '-7 days') AND date <= date('now')";
                break;
        }

        String sql = "SELECT date, " + columnName + " FROM health_data " +
                "WHERE username = ? AND " + dateFilter + " " +
                "ORDER BY date ASC";

        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        return pstmt.executeQuery();
    }

    public ResultSet getComparisonData(String username, String metric, int daysOffset) throws SQLException {
        String columnName;
        switch (metric.toLowerCase()) {
            case "weight":
                columnName = "weight";
                break;
            case "bmi":
                columnName = "bmi";
                break;
            case "steps":
                columnName = "steps";
                break;
            case "blood pressure":
                columnName = "blood_pressure";
                break;
            case "heart rate":
                columnName = "heart_rate";
                break;
            default:
                columnName = "weight";
                break;
        }

        // Get data from previous period
        String sql = "SELECT date, " + columnName + " FROM health_data " +
                "WHERE username = ? AND " +
                "date >= date('now', '-" + (daysOffset * 2) + " days') AND " +
                "date < date('now', '-" + daysOffset + " days') " +
                "ORDER BY date ASC";

        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        return pstmt.executeQuery();
    }

    public double getTargetValue(String username, String metric) throws SQLException {
        String sql = "SELECT target_weight FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("target_weight");
            }
        }
        return 0.0;
    }

    // Check username uniqueness
    public boolean checkUsernameExists(String username) throws SQLException {
        String sql = "SELECT username FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User(username, "");
                ProfileData profile = new ProfileData();
                profile.setName(rs.getString("name"));
                profile.setAge(rs.getInt("age"));
                profile.setGender(rs.getString("gender"));
                profile.setHeight(rs.getDouble("height"));
                profile.setTargetWeight(rs.getDouble("target_weight"));
                user.setProfile(profile);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Inserts a new daily habit record into the database
    public boolean insertDailyHabit(String username, String date, int waterIntake, String diet, int sleepHours) throws SQLException {
        String sql = "INSERT INTO daily_habits (username, date, water_intake, diet, sleep_hours) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, date);
            pstmt.setInt(3, waterIntake);
            pstmt.setString(4, diet);
            pstmt.setInt(5, sleepHours);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting daily habit: " + e.getMessage());
            throw e;
        }
    }
    // Checks if a daily habit record exists for the given username and date
    public boolean checkDailyHabitExists(String username, String date) throws SQLException {
        String sql = "SELECT 1 FROM daily_habits WHERE username = ? AND date = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, date);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }
    // Retrieves the daily habit record for the given username and date
    public ResultSet getDailyHabit(String username, String date) throws SQLException {
        String sql = "SELECT * FROM daily_habits WHERE username = ? AND date = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, date);
        return pstmt.executeQuery();
    }
        // Updates an existing daily habit record in the database
public boolean updateDailyHabit(String username, String date, int waterIntake, String diet, int sleepHours) throws SQLException {
    String sql = "UPDATE daily_habits SET water_intake = ?, diet = ?, sleep_hours = ? WHERE username = ? AND date = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, waterIntake); 
        pstmt.setString(2, diet); 
        pstmt.setInt(3, sleepHours); 
        pstmt.setString(4, username);
        pstmt.setString(5, date); 

        int rowsUpdated = pstmt.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        System.err.println("Error updating daily habit: " + e.getMessage());
        throw e;
    }
}
 
    
}