package com.healthbuddy;

import java.io.Serializable;

public class ProfileData implements Serializable {
    private byte[] photo;
    private String name;
    private int age;
    private String gender;
    private double height;
    private double targetWeight;

    public ProfileData() {
        this.name = "";
        this.age = 0;
        this.gender = "";
        this.height = 0.0;
        this.targetWeight = 0.0;
    }

    // Getters and setters
    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }
}