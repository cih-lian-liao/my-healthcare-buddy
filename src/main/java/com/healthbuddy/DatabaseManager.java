package com.healthbuddy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
                "DROP TABLE IF EXISTS users",
                "CREATE TABLE users (" +
                        "username TEXT PRIMARY KEY," +
                        "password TEXT NOT NULL," +
                        "salt TEXT NOT NULL," +
                        "name TEXT," +
                        "age INTEGER," +
                        "gender TEXT," +
                        "height REAL," +
                        "target_weight REAL" +
                        ")",
                "CREATE TABLE IF NOT EXISTS health_data (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT," +
                        "date DATE NOT NULL," +
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
                        "date DATE NOT NULL," +
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
            // Insert test user with hashed password
            String salt = PasswordSecurity.generateSalt();
            String hashedPassword = PasswordSecurity.hashPassword("test123", salt);
            stmt.execute("INSERT OR IGNORE INTO users (username, password, salt, name, age, gender, height, target_weight) " +
                    "VALUES ('test', '" + hashedPassword + "', '" + salt + "', 'Test User', 25, 'Male', 175.0, 70.0)");

            // Get current date for sample data
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // Create sample data for last 90 days
            for (int i = 0; i < 90; i++) {
                String date = sdf.format(cal.getTime());

                // Create realistic trends with some random variation
                double baseWeight = 75.5 - (i * 0.05); // Slow weight loss trend
                double randomWeight = baseWeight + (Math.random() * 0.4 - 0.2); // ±0.2 kg variation
                double weight = Math.round(randomWeight * 10.0) / 10.0;

                double bmi = weight / Math.pow(175.0 / 100, 2);

                // Steps with weekend variation and random noise
                int baseSteps = 8500 + (i * 20); // Gradually increasing base steps
                int weekendReduction = (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) ? 2000 : 0;
                int steps = baseSteps - weekendReduction + (int) (Math.random() * 1000 - 500);

                // Blood pressure with slight variation
                int baseSystolic = 120 - (i / 30); // Slight improvement over time
                int systolic = baseSystolic + (int) (Math.random() * 6 - 3); // ±3 variation
                int baseDiastolic = 80 - (i / 30);
                int diastolic = baseDiastolic + (int) (Math.random() * 4 - 2); // ±2 variation

                // Heart rate with activity correlation
                int baseHeartRate = 72 - (i / 30); // Slight improvement over time
                int stepEffect = (steps > 10000) ? 2 : 0; // Higher heart rate on active days
                int heartRate = baseHeartRate - stepEffect + (int) (Math.random() * 4 - 2);

                String sql = String.format(
                        "INSERT OR IGNORE INTO health_data (username, date, weight, bmi, steps, blood_pressure, heart_rate) "
                                +
                                "VALUES ('test', '%s', %.1f, %.1f, %d, '%d/%d', %d)",
                        date, weight, bmi, steps, systolic, diastolic, heartRate);
                stmt.execute(sql);

                // Move to previous day
                cal.add(Calendar.DAY_OF_MONTH, -1);
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
        String sql = "SELECT password, salt FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String salt = rs.getString("salt");
                return PasswordSecurity.verifyPassword(password, storedPassword, salt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Insert username and password with salt
    public boolean insertUser(String username, String password) throws SQLException {
        String salt = PasswordSecurity.generateSalt();
        String hashedPassword = PasswordSecurity.hashPassword(password, salt);
        String sql = "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, salt);
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
                break;
        }

        // Get current date in MM/dd/yyyy format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String endDate = outputFormat.format(cal.getTime());

        // Calculate start date
        switch (timeRange) {
            case "Last Week":
                cal.add(Calendar.DAY_OF_MONTH, -7);
                break;
            case "Last Month":
                cal.add(Calendar.MONTH, -1);
                break;
            case "Last 3 Months":
                cal.add(Calendar.MONTH, -3);
                break;
            case "Last Year":
                cal.add(Calendar.YEAR, -1);
                break;
            default:
                cal.add(Calendar.DAY_OF_MONTH, -7);
        }
        String startDate = outputFormat.format(cal.getTime());

        // Simple query since dates are stored in MM/dd/yyyy format
        String sql = "SELECT date, " + columnName + " FROM health_data " +
                "WHERE username = ? " +
                "AND date >= ? " +
                "AND date <= ? " +
                "ORDER BY date ASC";

        System.out.println("Query dates: Start=" + startDate + ", End=" + endDate);
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, startDate);
        pstmt.setString(3, endDate);

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
    public boolean insertDailyHabit(String username, String date, int waterIntake, String diet, int sleepHours)
            throws SQLException {
        try {
            // Convert MM/dd/yyyy to yyyy-MM-dd
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = inputFormat.parse(date);
            String formattedDate = outputFormat.format(parsedDate);

            String sql = "INSERT INTO daily_habits (username, date, water_intake, diet, sleep_hours) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, formattedDate);
                pstmt.setInt(3, waterIntake);
                pstmt.setString(4, diet);
                pstmt.setInt(5, sleepHours);
                pstmt.executeUpdate();
                return true;
            }
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            throw new SQLException("Invalid date format. Expected MM/dd/yyyy");
        } catch (SQLException e) {
            System.err.println("Error inserting daily habit: " + e.getMessage());
            throw e;
        }
    }

    // Checks if a daily habit record exists for the given username and date
    public boolean checkDailyHabitExists(String username, String date) throws SQLException, ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = inputFormat.parse(date);
        String formattedDate = outputFormat.format(parsedDate);
        String sql = "SELECT 1 FROM daily_habits WHERE username = ? AND date = date(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, formattedDate);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    public boolean checkHealthDataExists(String username, String date) throws SQLException, ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = inputFormat.parse(date);
        String formattedDate = outputFormat.format(parsedDate);
        String sql = "SELECT 1 FROM health_data WHERE username = ? AND date = date(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, formattedDate);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    // Retrieves the daily habit record for the given username and date
    public ResultSet getDailyHabit(String username, String date) throws SQLException, ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = inputFormat.parse(date);
        String formattedDate = outputFormat.format(parsedDate);
        String sql = "SELECT * FROM daily_habits WHERE username = ? AND date = date(?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, formattedDate);
        return pstmt.executeQuery();
    }

    // Updates an existing daily habit record in the database
    public boolean updateDailyHabit(String username, String date, int waterIntake, String diet, int sleepHours)
            throws SQLException, ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = inputFormat.parse(date);
        String formattedDate = outputFormat.format(parsedDate);
        String sql = "UPDATE daily_habits SET water_intake = ?, diet = ?, sleep_hours = ? WHERE username = ? AND date = date(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, waterIntake);
            pstmt.setString(2, diet);
            pstmt.setInt(3, sleepHours);
            pstmt.setString(4, username);
            pstmt.setString(5, formattedDate);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating daily habit: " + e.getMessage());
            throw e;
        }
    }

}