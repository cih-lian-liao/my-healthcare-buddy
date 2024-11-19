package com.healthbuddy;

import java.time.LocalDate;

public class HealthData {
    private double weight;
    private double bmi;
    private int steps;
    private String bloodPressure;
    private int heartRate;
    private LocalDate date;

    public HealthData() {
        this.date = LocalDate.now();
    }

    public double calculateBMI(double height) {
        return weight / (height * height);
    }

    public boolean compareWithGoals(HealthGoals goals) {
        return weight <= goals.getTargetWeight() &&
                steps >= goals.getTargetSteps();
    }

    // Getters and setters
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}