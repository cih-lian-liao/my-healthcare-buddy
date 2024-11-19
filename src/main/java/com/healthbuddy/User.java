package com.healthbuddy;

import java.time.LocalDate;
import java.util.List;

public class User {
    private String username;
    private String password;
    private ProfileData profile;
    private HealthGoals goals;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.profile = new ProfileData();
        this.goals = new HealthGoals();
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ProfileData getProfile() {
        return profile;
    }

    public void setProfile(ProfileData profile) {
        this.profile = profile;
    }

    public HealthGoals getGoals() {
        return goals;
    }

    public void setGoals(HealthGoals goals) {
        this.goals = goals;
    }
}