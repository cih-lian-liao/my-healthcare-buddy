package com.healthbuddy;

public class HealthGoals {
    private double targetWeight;
    private int targetSteps;
    private String targetBloodPressure;

    public boolean checkGoalProgress(HealthData data) {
        return data.compareWithGoals(this);
    }

    // Getters and setters
    public double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public int getTargetSteps() {
        return targetSteps;
    }

    public void setTargetSteps(int targetSteps) {
        this.targetSteps = targetSteps;
    }

    public String getTargetBloodPressure() {
        return targetBloodPressure;
    }

    public void setTargetBloodPressure(String targetBloodPressure) {
        this.targetBloodPressure = targetBloodPressure;
    }
}