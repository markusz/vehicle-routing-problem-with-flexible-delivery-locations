package de.tum.ziller.thesis.thrp.common.entities;

import com.google.common.base.Objects;
import de.tum.ziller.thesis.thrp.common.abstraction.Identifiable;

import java.io.Serializable;
import java.util.*;

public abstract class Job extends Identifiable implements Serializable{
	
	private static final long serialVersionUID = 7414188193795448780L;
	
	private Set<Qualification>	qualifications		= new HashSet<>();
	private Integer				qHash				= 0;
	private String		binary				= "";
	
	List<Timeslot>						availabilty			= new ArrayList<>();
	Integer								durationSlots;
	Integer								totalDuration;
	Double								schedulingPriority;
	Integer								pauseBefore;
	Integer								pauseAfter;
	String								name;
	
	public void addQualification(Collection<Qualification> qs){
		
		for (Qualification q : qs) {
			addQualification(q);
		}
	}
	
	public void addQualification(Qualification q){
		qHash = qHash + q.getQHash();
		qualifications.add(q);
		binary = Integer.toBinaryString(qHash);
		
	}
	
	public void setQHash(Integer hash){
		qHash = hash;
		binary = Integer.toBinaryString(hash);
	}
	
	@Override
	public String toString(){
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

    public Integer getDurationSlots() {
        return this.durationSlots;
    }

    public Integer getTotalDuration() {
        return this.totalDuration;
    }

    public Double getSchedulingPriority() {
        return this.schedulingPriority;
    }

    public Integer getPauseBefore() {
        return this.pauseBefore;
    }

    public Integer getPauseAfter() {
        return this.pauseAfter;
    }

    public String getName() {
        return this.name;
    }

    public void setQualifications(Set<Qualification> qualifications) {
        this.qualifications = qualifications;
    }

    public void setAvailabilty(List<Timeslot> availabilty) {
        this.availabilty = availabilty;
    }

    public void setDurationSlots(Integer durationSlots) {
        this.durationSlots = durationSlots;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public void setSchedulingPriority(Double schedulingPriority) {
        this.schedulingPriority = schedulingPriority;
    }

    public void setPauseBefore(Integer pauseBefore) {
        this.pauseBefore = pauseBefore;
    }

    public void setPauseAfter(Integer pauseAfter) {
        this.pauseAfter = pauseAfter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Qualification> getQualifications() {
        return this.qualifications;
    }

    public Integer getQHash() {
        return this.qHash;
    }

    public String getBinary() {
        return this.binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }
}
