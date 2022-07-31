package com.example.crowdfunding.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;

public class Milestone implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private Long requiredCKBPercentage;
    private String description;
    private Integer numberOfVotedNo;

    public Milestone() {
        numberOfVotedNo = 0;
        requiredCKBPercentage = 0L;
    }

    public Milestone(LocalDate dueDate, long requiredCKBPercentage, String description) {
        super();
        this.dueDate = dueDate;
        this.requiredCKBPercentage = requiredCKBPercentage;
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getRequiredCKBPercentage() {
        return requiredCKBPercentage;
    }

    public void setRequiredCKBPercentage(Long requiredCKBPercentage) {
        this.requiredCKBPercentage = requiredCKBPercentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfVotedNo() {
        return numberOfVotedNo;
    }

    public void setNumberOfVotedNo(Integer numberOfVotedNo) {
        this.numberOfVotedNo = numberOfVotedNo;
    }

    public void incrementNumberOfVoteNo() {
        this.numberOfVotedNo++;
    }

    @Override
    public String toString() {
        return "Milestone{" +
                "dueDate=" + dueDate +
                ", requiredCKB=" + requiredCKBPercentage +
                ", description='" + description + '\'' +
                ", numberOfVoteNo=" + numberOfVotedNo +
                '}';
    }
}
