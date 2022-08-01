package com.example.crowdfunding.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;

public class Milestone implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private Integer targetCKBPercentage;
    private String description;
    private Integer approvalRatioThreshold;
    private Integer numberOfVotedNo;

    public Milestone() {
    }

    public Milestone(LocalDate dueDate, Integer targetCKBPercentage, String description, Integer approvalRatioThreshold) {
        super();
        this.dueDate = dueDate;
        this.targetCKBPercentage = targetCKBPercentage;
        this.description = description;
        this.numberOfVotedNo = 0;
        this.approvalRatioThreshold = approvalRatioThreshold;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getTargetCKBPercentage() {
        return targetCKBPercentage;
    }

    public void setTargetCKBPercentage(Integer targetCKBPercentage) {
        this.targetCKBPercentage = targetCKBPercentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getApprovalRatioThreshold() {
        return approvalRatioThreshold;
    }

    public void setApprovalRatioThreshold(Integer approvalRatioThreshold) {
        this.approvalRatioThreshold = approvalRatioThreshold;
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
                ", targetCKBPercentage=" + targetCKBPercentage +
                ", description='" + description + '\'' +
                ", approvalRatioThreshold=" + approvalRatioThreshold +
                ", numberOfVotedNo=" + numberOfVotedNo +
                '}';
    }
}
