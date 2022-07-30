package com.example.crowdfunding.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    private long target;  // in CKBytes
    private LocalDate endDate;
    @Column(columnDefinition = "BLOB NOT NULL")
    private Milestone[] milestones;
    @ElementCollection
    @Column(columnDefinition = "BLOB NOT NULL")
    private Map<Long, String> deliveries;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int nextMilestoneIndex;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate starDate;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long current;  // in CKBytes
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int numberOfBacker;
    @ElementCollection
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Map<Long, Integer> numberOfBackerInDeliveries;

    public Project() {
        starDate = LocalDate.now();
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

    public long getTarget() {
        return target;
    }

    public void setTarget(long target) {
        this.target = target;
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

    public LocalDate getStarDate() {
        return starDate;
    }

    public void setStarDate(LocalDate starDate) {
        this.starDate = starDate;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creatorAddress='" + creatorAddress + '\'' +
                ", target=" + target +
                ", endDate=" + endDate +
                ", milestones=" + Arrays.toString(milestones) +
                ", deliveries=" + deliveries +
                ", starDate=" + starDate +
                ", current=" + current +
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
