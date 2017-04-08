package de.markusziller.alns.entities;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.*;

public abstract class Job extends Identifiable implements Serializable {

    private static final long serialVersionUID = 7414188193795448780L;
    private List<Timeslot> availabilty = new ArrayList<>();
    private Integer durationSlots;
    private Integer totalDuration;
    private Double schedulingPriority;
    private Integer pauseBefore;
    private Integer pauseAfter;
    private String name;
    private Set<Qualification> qualifications = new HashSet<>();
    private Integer qHash = 0;
    private String binary = "";

    public void addQualification(Collection<Qualification> qs) {

        for (Qualification q : qs) {
            addQualification(q);
        }
    }

    private void addQualification(Qualification q) {
        qHash = qHash + q.getQHash();
        qualifications.add(q);
        binary = Integer.toBinaryString(qHash);

    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("id", getId())
                .omitNullValues()
                .add("d", getDurationSlots())
                .add("t", availabilty)
                .toString();
    }

    public void addAvailabilty(Timeslot vacancySlot) {
        availabilty.add(vacancySlot);

    }

    public List<Timeslot> getAvailabilty() {
        return this.availabilty;
    }

    public void setAvailabilty(List<Timeslot> availabilty) {
        this.availabilty = availabilty;
    }

    public Integer getDurationSlots() {
        return this.durationSlots;
    }

    public void setDurationSlots(Integer durationSlots) {
        this.durationSlots = durationSlots;
    }

    public Integer getTotalDuration() {
        return this.totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Double getSchedulingPriority() {
        return this.schedulingPriority;
    }

    public void setSchedulingPriority(Double schedulingPriority) {
        this.schedulingPriority = schedulingPriority;
    }

    public Integer getPauseBefore() {
        return this.pauseBefore;
    }

    public void setPauseBefore(Integer pauseBefore) {
        this.pauseBefore = pauseBefore;
    }

    public Integer getPauseAfter() {
        return this.pauseAfter;
    }

    public void setPauseAfter(Integer pauseAfter) {
        this.pauseAfter = pauseAfter;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Qualification> getQualifications() {
        return this.qualifications;
    }

    public void setQualifications(Set<Qualification> qualifications) {
        this.qualifications = qualifications;
    }

    public Integer getQHash() {
        return this.qHash;
    }

    public void setQHash(Integer hash) {
        qHash = hash;
        binary = Integer.toBinaryString(hash);
    }

    public String getBinary() {
        return this.binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }
}
