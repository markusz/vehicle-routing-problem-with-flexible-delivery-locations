package de.markusziller.alns.heuristic.strategies.alns.insertion;

import de.markusziller.alns.common.entities.*;
import de.markusziller.alns.heuristic.concurrent.InsertionEvaluator;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;

import java.util.LinkedList;
import java.util.List;

public abstract class ALNSAbstractRepair extends ALNSAbstractOperation {

    private InsertionEvaluator ie = new InsertionEvaluator();
    private List<Insertion> tabus = new LinkedList<>();

    public double getNodeCosts(Solution s, Insertion is) {
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

    protected boolean containsTabu(Job j, Therapist t, Room r) {
        for (Insertion pni : getTabus()) {
            if (pni.getTherapist() == t && pni.getNode().getRoom() == r && pni.getNode().getJob() == j) {
                return true;
            }
        }
        return false;
    }

    public List<Insertion> getTabus() {
        return this.tabus;
    }

    public void setTabus(List<Insertion> tabus) {
        this.tabus = tabus;
    }
}
