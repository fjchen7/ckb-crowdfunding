package com.example.crowdfunding.model;

import java.io.Serializable;

public class Delivery implements Serializable {
    private String reward;
    private int numberOfBacker;

    public Delivery() {
    }

    public Delivery(String reward) {
        super();
        this.reward = reward;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public Integer getNumberOfBacker() {
        return numberOfBacker;
    }

    public void setNumberOfBacker(Integer numberOfBacker) {
        this.numberOfBacker = numberOfBacker;
    }

    public void incrementNumber() {
        this.numberOfBacker += 1;
    }

    public void incrementNumber(int number) {
        this.numberOfBacker += number;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "reward='" + reward + '\'' +
                ", numberOfBacker=" + numberOfBacker +
                '}';
    }
}
