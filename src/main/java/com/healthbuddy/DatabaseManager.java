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
        String insertTestUser = "INSERT INTO users (username, password, name) VALUES " +
                "('test', 'test123', 'Test User')";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(insertTestUser);
        } catch (SQLException e) {
            System.err.println("Error creating default data: " + e.getMessage());
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
}