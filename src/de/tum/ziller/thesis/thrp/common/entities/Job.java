package de.tum.ziller.thesis.thrp.common.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import com.google.common.base.Objects;

import de.tum.ziller.thesis.thrp.common.abstraction.Identifiable;

public abstract @Getter @Setter class Job extends Identifiable implements Serializable{
	
	private static final long serialVersionUID = 7414188193795448780L;
	
	private @Getter Set<Qualification>	qualifications		= new HashSet<>();
	private @Getter Integer				qHash				= 0;
	private @Setter @Getter String		binary				= "";
	
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

}
