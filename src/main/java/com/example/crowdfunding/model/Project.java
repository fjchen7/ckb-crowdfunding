package com.example.crowdfunding.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

@Entity
public class Project {
    @Id
    @GeneratedValue
    private Long id;
    private Status status;

    private String name;
    private String description;
    private String creatorAddress;
    private long targetCKB;
    private LocalDate startDate;
    private LocalDate endDate;
    @Column(columnDefinition = "BLOB NOT NULL")
    private Milestone[] milestones;
    @ElementCollection
    @Column(columnDefinition = "BLOB NOT NULL")
    private Map<Long, String> deliveries;

    private int nextMilestoneIndex;
    private long pledgedCKB;
    private int numberOfBacker;
    @ElementCollection
    private Map<Long, Integer> numberOfBackerInDeliveries;

    public Project() {
        startDate = LocalDate.now();
        status = Status.CREATED;
        milestones = new Milestone[0];
        deliveries = new TreeMap<>();
        numberOfBacker = 0;
        numberOfBackerInDeliveries = new TreeMap<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorAddress() {
        return creatorAddress;
    }

    public void setCreatorAddress(String creatorAddress) {
        this.creatorAddress = creatorAddress;
    }

    public long getTargetCKB() {
        return targetCKB;
    }

    public void setTargetCKB(long targetCKB) {
        this.targetCKB = targetCKB;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Milestone[] getMilestones() {
        return milestones;
    }

    public void setMilestones(Milestone[] milestones) {
        this.milestones = milestones;
    }

    public Map<Long, String> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(Map<Long, String> deliveries) {
        this.deliveries = deliveries;
        for (Long pledgeAmount: this.deliveries.keySet()) {
            this.setNumberOfBackerInDelivery(pledgeAmount, 0);
        }
    }

    public void setDelivery(long pledgeAmount, String delivery) {
        deliveries.put(pledgeAmount, delivery);
        this.setNumberOfBackerInDelivery(pledgeAmount, 0);
    }

    public int getNextMilestoneIndex() {
        return nextMilestoneIndex;
    }

    public void setNextMilestoneIndex(int nextMilestoneIndex) {
        this.nextMilestoneIndex = nextMilestoneIndex;
    }

    public Milestone getNextMilestone() {
        return milestones[nextMilestoneIndex];
    }

    public void moveNextMilestone() {
        this.nextMilestoneIndex++;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public long getPledgedCKB() {
        return pledgedCKB;
    }

    public void setPledgedCKB(long pledgedCKB) {
        this.pledgedCKB = pledgedCKB;
    }

    public int getNumberOfBacker() {
        return numberOfBacker;
    }

    public void setNumberOfBacker(int numberOfBacker) {
        this.numberOfBacker = numberOfBacker;
    }

    public void incrementNumberOfBacker() {
        numberOfBacker++;
    }

    public Map<Long, Integer> getNumberOfBackerInDeliveries() {
        return numberOfBackerInDeliveries;
    }

    public void setNumberOfBackerInDeliveries(Map<Long, Integer> numberOfBackerInDeliveries) {
        this.numberOfBackerInDeliveries = numberOfBackerInDeliveries;
    }

    public void setNumberOfBackerInDelivery(Long pledgeAmount, Integer numberOfBacker) {
        numberOfBackerInDeliveries.put(pledgeAmount, numberOfBacker);
    }

    public void incrementNumberOfBackerInDelivery(Long pledgeAmount) {
        numberOfBackerInDeliveries.put(pledgeAmount, numberOfBackerInDeliveries.get(pledgeAmount) + 1);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creatorAddress='" + creatorAddress + '\'' +
                ", targetCKB=" + targetCKB +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", milestones=" + Arrays.toString(milestones) +
                ", deliveries=" + deliveries +
                ", nextMilestoneIndex=" + nextMilestoneIndex +
                ", pledgedCKB=" + pledgedCKB +
                ", numberOfBacker=" + numberOfBacker +
                ", numberOfBackerInDeliveries=" + numberOfBackerInDeliveries +
                '}';
    }

    public enum Status {
        CREATED,
        PLEDGING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}
