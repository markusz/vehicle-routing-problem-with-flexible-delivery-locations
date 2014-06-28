package de.tum.ziller.thesis.thrp.common.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import com.google.common.base.Objects;

import de.tum.ziller.thesis.thrp.common.abstraction.Identifiable;

/**
 * Ein Therapeut
 * @author Markus
 *
 */
public class Therapist extends Identifiable implements Comparable<Therapist>,Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8951314154337865392L;

	/**
	 * List of Attributes
	 * **********************
	 */
	
	private @Getter @Setter String name = "";
	
	private @Getter Set<Qualification>	qualifications		= new HashSet<>();
	private List<Timeslot>				available;
	private @Getter @Setter Timeslot	firstPauseRange;
	private @Getter @Setter Timeslot	secondPauseRange;
	private @Getter @Setter Integer		latestFirstPauseStart;
	private @Getter @Setter Integer		earliestFirstPauseStart;
	private @Getter @Setter Integer		latestSecondPauseStart;
	private @Getter @Setter Integer		earliestSecondPauseStart;

	private @Setter @Getter Integer		qHash				= 0;
	private @Setter @Getter String		binary				= "";
	private @Setter @Getter Integer		shiftStart;
	private @Setter @Getter Integer		regularShiftEnd;
	
	public void addQualification(Qualification q){
		qualifications.add(q);
		qHash = qHash + q.getQHash();
		binary = Integer.toBinaryString(qHash);
	}
	
	public void addQualification(Collection<Qualification> qs) {
		for (Qualification q : qs) {
			addQualification(q);
		}
	}
	
	public Therapist(Integer id){
		setId(id);
	}
	
	public Therapist(Integer id, String name){
		setId(id);
		this.name = name;
	}

	
	@Override
	public String toString(){
		return Objects.toStringHelper(this.getClass()).add("id", getId())
	            .add("name", name)
	            .add("qHash", qHash)
	            .add("shiftSt.", getShiftStart())
	            .add("regShiftEnd", getRegularShiftEnd())
	            .toString();
	}

	@Override
	public int compareTo(Therapist t) {
		if(t.getQHash() > this.getQHash()){
			return 1;
		}
		if(t.getQHash() < this.getQHash()){
			return -1;
		}else{
			return 0;
		}
	}

}
