package com.example.crowdfunding.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;

public class Milestone implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private int targetPercentage;
    private String description;
    private int numberOfVoteNo;

    public Milestone() {
    }

    public Milestone(LocalDate dueDate, int targetPercentage, String description) {
        this.dueDate = dueDate;
        this.targetPercentage = targetPercentage;
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public int getTargetPercentage() {
        return targetPercentage;
    }

    public void setTargetPercentage(int targetPercentage) {
        this.targetPercentage = targetPercentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfVoteNo() {
        return numberOfVoteNo;
    }

    public void setNumberOfVoteNo(int numberOfVoteNo) {
        this.numberOfVoteNo = numberOfVoteNo;
    }

    public void incrementNumberOfVoteNo() {
        this.numberOfVoteNo++;
    }

    @Override
    public String toString() {
        return "Milestone{" +
                "dueDate=" + dueDate +
                ", targetPercentage=" + targetPercentage +
                ", description='" + description + '\'' +
                '}';
    }
}
