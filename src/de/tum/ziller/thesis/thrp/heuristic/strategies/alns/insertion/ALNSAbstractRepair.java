package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.insertion;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import de.tum.ziller.thesis.thrp.common.entities.Insertion;
import de.tum.ziller.thesis.thrp.common.entities.Job;
import de.tum.ziller.thesis.thrp.common.entities.Room;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.common.entities.Therapist;
import de.tum.ziller.thesis.thrp.heuristic.concurrent.InsertionEvaluator;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.ALNSAbstractOperation;

public abstract class ALNSAbstractRepair extends ALNSAbstractOperation {
	
	private InsertionEvaluator ie = new InsertionEvaluator();
	private @Getter @Setter List<Insertion> tabus = new LinkedList<>();

	public double getNodeCosts(Solution s, Insertion is){
		ie.setSolution(s);
		return ie.getInsertionCosts(is);
		
	}
	
	protected boolean isTabu(Job j, Therapist t, Room r, Integer start) {
		for (Insertion pni : getTabus()) {
			if (pni.getTherapist() == t && pni.getNode().getJob() == j && pni.getNode().getRoom() == r && start == pni.getStart()) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean containsTabu(Job j, Therapist t) {
		for (Insertion pni : getTabus()) {
			if (pni.getTherapist() == t && pni.getNode().getJob() == j) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean containsTabu(Job j, Room r) {
		for (Insertion pni : getTabus()) {
			if (pni.getNode().getRoom() == r && pni.getNode().getJob() == j) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean containsTabu(Job j, Therapist t,  Room r) {
		for (Insertion pni : getTabus()) {
			if (pni.getTherapist() == t && pni.getNode().getRoom() == r && pni.getNode().getJob() == j) {
				return true;
			}
		}
		return false;
	}

}