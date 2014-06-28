package de.tum.ziller.thesis.thrp.common.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import de.tum.ziller.thesis.thrp.common.entities.jobs.WardJob;
import de.tum.ziller.thesis.thrp.common.entities.rooms.TherapyCenter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
/**
 * 
 * Eine hypothetische Einf�gesituation f�r einen zu planenden Job. Job, Raum und Zeit sind im Node gespeichert. Zus�tzlich wird der vorgesehene Therapeut gespeichert.
 * @author Markus
 *
 *
 */
public class Insertion implements Comparable<Insertion>, Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6687473937777930786L;

	private Double costs;
	
	@NonNull
	private Node node;
	
	private Node previousNode;
	private Node nextNode;
	
	@NonNull
	private Therapist therapist;

	public Integer getStart(){
		return node.getTime().getStart();
	}
	
	public Integer getEnd(){
		return node.getTime().getEnd();
	}
	
	@Override
	public String toString(){
		return therapist.getName()+" -> "+node;
	}
	
	//aufsteigend sortiert
	@Override
	public int compareTo(Insertion o) {
		if(o.getCosts() < costs){
			return 1;
		}
		if(o.getCosts() > costs){
			return -1;
		}
		return 0;
	}
	
	public boolean isTransfer(){
		return (node.getJob().getClass() == WardJob.class && node.getRoom().getClass() == TherapyCenter.class);
	}
}
