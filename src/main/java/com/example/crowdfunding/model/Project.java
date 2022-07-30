package com.example.crowdfunding.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Arrays;

@Entity
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String privateKey;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long current;  // in CKBytes
    private long target;  // in CKBytes

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private LocalDate starDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate[] milestoneDates;
    private short[] milestoneTargets;  // percentage of target

    public Project() {
        milestoneDates = new LocalDate[0];
        milestoneTargets = new short[0];
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStarDate() {
        return starDate;
    }

    public void setStarDate(LocalDate starDate) {
        this.starDate = starDate;
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

    public long getTarget() {
        return target;
    }

    public void setTarget(long target) {
        this.target = target;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public LocalDate[] getMilestoneDates() {
        return milestoneDates;
    }

    public void setMilestoneDates(LocalDate[] milestoneDates) {
        this.milestoneDates = milestoneDates;
    }

    public short[] getMilestoneTargets() {
        return milestoneTargets;
    }

    public void setMilestoneTargets(short[] milestoneTargets) {
        this.milestoneTargets = milestoneTargets;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", current=" + current +
                ", target=" + target +
                ", starDate=" + starDate +
                ", milestoneDates=" + Arrays.toString(milestoneDates) +
                ", milestoneTargets=" + Arrays.toString(milestoneTargets) +
                '}';
    }
}
