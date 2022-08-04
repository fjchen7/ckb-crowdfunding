package com.example.crowdfunding.model;

import org.nervos.ckb.type.OutPoint;

import javax.persistence.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
    @Column(columnDefinition = "BLOB")
    private Map<Long, Delivery> deliveries;
    private Long startupCKB;

    private Integer nextMilestoneIndex;
    private Long pledgedCKB;
    private Integer numberOfBacker;

    //    @Column(columnDefinition = "BLOB")
    @Embedded
    private OutPoint crowdfundingCell;

    public Project() {
    }

    public static void init(Project project) {
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
        if (project.pledgedCKB == null) {
            project.pledgedCKB = 0L;
        }
        if (project.numberOfBacker == null) {
            project.numberOfBacker = 0;
        }
        if (project.deliveries == null) {
            project.deliveries = new TreeMap<>();
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

    public Map<Long, Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(Map<Long, Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public Long getStartupCKB() {
        return startupCKB;
    }

    public void setStartupCKB(Long startupCKB) {
        this.startupCKB = startupCKB;
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
        if (nextMilestoneIndex == null) {
            nextMilestoneIndex = 0;
        } else if (nextMilestoneIndex < milestones.length - 1) {
            nextMilestoneIndex++;
        } else {
            nextMilestoneIndex = null;
        }
    }

    public long[] allowedPledgeAmounts() {
        long[] amounts = new long[deliveries.size()];
        int i = 0;
        for (long amount: deliveries.keySet()) {
            amounts[i++] = amount;
        }
        Arrays.sort(amounts);
        return amounts;
    }

    public boolean inAllowedPledgeAmounts(long amount) {
        return deliveries.containsKey(amount);
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

    public void incrementPledgedCKB(long pledgedCKB) {
        this.pledgedCKB += pledgedCKB;
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

    public void incrementNumberOfBackerInDelivery(Long pledgeAmount) {
        if (deliveries.containsKey(pledgeAmount)) {
            deliveries.get(pledgeAmount).incrementNumber();
        }
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
                ", startupCKB=" + startupCKB +
                ", nextMilestoneIndex=" + nextMilestoneIndex +
                ", pledgedCKB=" + pledgedCKB +
                ", numberOfBacker=" + numberOfBacker +
                ", crowdfundingCell=" + crowdfundingCell +
                '}';
    }

    public byte[] toData() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ByteBuffer bb = ByteBuffer.allocate(8);
            bb.putLong(startDate.atTime(0, 0, 0).toEpochSecond(ZoneOffset.UTC));
            out.write(bb.array());

            bb = ByteBuffer.allocate(4);
            bb.putInt(targetCKB.intValue());
            out.write(bb.array());

            bb = ByteBuffer.allocate(4);
            bb.putInt(startupCKB.intValue());
            out.write(bb.array());

            Integer calculatedPercentage = 0;
            for (int i = 0; i < milestones.length; i++) {
                Milestone m = milestones[i];
                bb = ByteBuffer.allocate(13);
                bb.putLong(m.getDueDate().atTime(0, 0, 0).toEpochSecond(ZoneOffset.UTC));
                Long amount = (targetCKB - startupCKB) * (m.getTargetCKBPercentage() - calculatedPercentage) / 100;
                calculatedPercentage += m.getTargetCKBPercentage();
                bb.putInt(amount.intValue());
                bb.put(m.getApprovalRatioThreshold().byteValue());
                out.write(bb.array());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    public enum Status {
        CREATED,
        PLEDGING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}
