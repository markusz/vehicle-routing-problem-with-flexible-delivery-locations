package de.markusziller.alns.heuristic.strategies.alns.insertion;

import de.markusziller.alns.entities.*;
import de.markusziller.alns.heuristic.concurrent.InsertionEvaluator;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;

import java.util.LinkedList;
import java.util.List;

abstract class ALNSAbstractRepair extends ALNSAbstractOperation {

    private final InsertionEvaluator ie = new InsertionEvaluator();
    private List<Insertion> tabus = new LinkedList<>();

    double getNodeCosts(Solution s, Insertion is) {
        ie.setSolution(s);
        return ie.getInsertionCosts(is);

    }

    boolean containsTabu(Job j, Therapist t) {
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

    private List<Insertion> getTabus() {
        return this.tabus;
    }

    void setTabus(List<Insertion> tabus) {
        this.tabus = tabus;
    }
}
