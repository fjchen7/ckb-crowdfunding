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
    private Long targetCKB;
    private LocalDate startDate;
    private LocalDate endDate;
    @Column(columnDefinition = "BLOB NOT NULL")
    private Milestone[] milestones;
    @ElementCollection
    @Column(columnDefinition = "BLOB NOT NULL")
    private Map<Long, String> deliveries;

    private Integer nextMilestoneIndex;
    private Long pledgedCKB;
    private Integer numberOfBacker;
    @ElementCollection
    private Map<Long, Integer> numberOfBackerInDeliveries;

    @Column(columnDefinition = "BLOB")
    private OutPoint crowdfundingCell;

    public Project() {
    }

    public static void init(Project project) {
        project.startDate = LocalDate.now();
        if (project.status == null) {
            project.status = Status.CREATED;
        }
        if (project.milestones == null) {
            project.milestones = new Milestone[0];
        }
        for (Milestone milestone: project.milestones) {
            milestone.setNumberOfVotedNo(0);
        }
        if (project.deliveries == null) {
            project.deliveries = new TreeMap<>();
        }
        if (project.nextMilestoneIndex == null) {
            project.nextMilestoneIndex = 0;
        }
        if (project.pledgedCKB == null) {
            project.pledgedCKB = 0L;
        }
        if (project.numberOfBacker == null) {
            project.numberOfBacker = 0;
        }
        if (project.numberOfBackerInDeliveries == null) {
            project.numberOfBackerInDeliveries = new TreeMap<>();
        }
        if (project.deliveries == null) {
            project.numberOfBackerInDeliveries = new TreeMap<>();
            for (Long pledgeAmount: project.numberOfBackerInDeliveries.keySet()) {
                project.numberOfBackerInDeliveries.put(pledgeAmount, 0);
            }
        }
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

    public Long getTargetCKB() {
        return targetCKB;
    }

    public void setTargetCKB(Long targetCKB) {
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
        if (this.deliveries == null) {
            this.deliveries = new TreeMap<>();
        }
        deliveries.put(pledgeAmount, delivery);
    }

    public Integer getNextMilestoneIndex() {
        return nextMilestoneIndex;
    }

    public void setNextMilestoneIndex(Integer nextMilestoneIndex) {
        this.nextMilestoneIndex = nextMilestoneIndex;
    }

    public Milestone getNextMilestone() {
        if (nextMilestoneIndex == null || nextMilestoneIndex >= milestones.length) {
            return null;
        }
        return milestones[nextMilestoneIndex];
    }

    public void moveToNextMilestone() {
        if (nextMilestoneIndex < milestones.length - 1) {
            nextMilestoneIndex++;
        } else {
            nextMilestoneIndex = null;
            status = Status.COMPLETED;
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Long getPledgedCKB() {
        return pledgedCKB;
    }

    public void setPledgedCKB(Long pledgedCKB) {
        this.pledgedCKB = pledgedCKB;
    }

    public Integer getNumberOfBacker() {
        return numberOfBacker;
    }

    public void setNumberOfBacker(Integer numberOfBacker) {
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
        if (this.numberOfBackerInDeliveries == null) {
            this.numberOfBackerInDeliveries = new TreeMap<>();
        }
        numberOfBackerInDeliveries.put(pledgeAmount, numberOfBacker);
    }

    public void incrementNumberOfBackerInDelivery(Long pledgeAmount) {
        setNumberOfBackerInDelivery(pledgeAmount, numberOfBackerInDeliveries.get(pledgeAmount) + 1);
    }

    public OutPoint getCrowdfundingCell() {
        return crowdfundingCell;
    }

    public void setCrowdfundingCell(OutPoint crowdfundingCell) {
        this.crowdfundingCell = crowdfundingCell;
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
                ", crowdfundingCell=" + crowdfundingCell +
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
