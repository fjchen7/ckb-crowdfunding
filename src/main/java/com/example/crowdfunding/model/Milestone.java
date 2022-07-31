package com.example.crowdfunding.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;

public class Milestone implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private long requiredCKB;
    private String description;
    private int numberOfVoteNo;

    public Milestone() {
    }

    public Milestone(LocalDate dueDate, int requiredCKB, String description) {
        this.dueDate = dueDate;
        this.requiredCKB = requiredCKB;
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public long getRequiredCKB() {
        return requiredCKB;
    }

    public void setRequiredCKB(long requiredCKB) {
        this.requiredCKB = requiredCKB;
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
                ", requiredCKB=" + requiredCKB +
                ", description='" + description + '\'' +
                ", numberOfVoteNo=" + numberOfVoteNo +
                '}';
    }
}
