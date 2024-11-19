package com.healthbuddy;

import java.time.LocalDate;

public class DailyHabit {
    private int waterIntake;
    private String diet;
    private int sleepHours;
    private LocalDate date;

    public DailyHabit() {
        this.date = LocalDate.now();
    }

    public void logHabitData() {
        // Database logging implementation will be added
    }

    // Getters and setters
    public int getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(int waterIntake) {
        this.waterIntake = waterIntake;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public int getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(int sleepHours) {
        this.sleepHours = sleepHours;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}