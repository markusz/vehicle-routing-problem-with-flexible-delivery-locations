package de.markusziller.alns.heuristic.concurrent;

import de.markusziller.alns.entities.*;
import de.markusziller.alns.entities.jobs.OutpatientJob;
import de.markusziller.alns.entities.jobs.WardJob;
import de.markusziller.alns.entities.rooms.TherapyCenter;
import lombok.NonNull;

import java.util.List;
import java.util.concurrent.Callable;

public class InsertionEvaluator implements Callable<Insertion> {


    private Insertion bestNode = null;
    @NonNull
    private List<Insertion> list;
    @NonNull
    private Solution solution;

    public InsertionEvaluator(List<Insertion> list, Solution s) {
        this.list = list;
        solution = s;
    }

    public InsertionEvaluator(Solution s) {
        solution = s;
    }

    public InsertionEvaluator() {
    }

    @Override
    public Insertion call() throws Exception {


        for (Insertion i : list) {

            if (bestNode == null) {
                bestNode = i;
                bestNode.setCosts(getInsertionCosts(i));
            }

            Double iC = getInsertionCosts(i);

            if (getInsertionCosts(i) < 1) {
                getInsertionCosts(i);
            }

            if (iC < bestNode.getCosts()) {
                i.setCosts(iC);
                bestNode = i;

            }
        }
        return bestNode;
    }

    public Double getInsertionCosts(Insertion i) {
        Therapist t = i.getTherapist();
        Job j = i.getNode().getJob();

        //zusatzkosten durch einf�gung des jobs
        Double c_delta = 0.;

        Room r_h = solution.getLocation(i.getNode().getTime().getStart() - 1, t);
        Room r_i = i.getNode().getRoom();
        Room r_j = solution.getLocation(i.getNode().getTime().getEnd() + 1, t);


        Double c_hj = solution.getInstance().getRouteCosts(r_h.getId(), r_j.getId());

        Double c_hij = solution.getInstance().getRouteCosts(r_h.getId(), r_i.getId());
        c_hij += solution.getInstance().getRouteCosts(r_i.getId(), r_j.getId());


//			addcosts = solution.getInstance().getRouteCosts(previousStation.getId(), planedStation.getId());
//			addcosts += solution.getInstance().getRouteCosts(previousStation.getId(), planedStation.getId());
        Double c_t_i = 0.;

        if (j instanceof OutpatientJob) {
            c_t_i += solution.getInstance().getTransportCosts(solution.getInstance().getBreakroom().getId(), r_i.getId());
            c_t_i += solution.getInstance().getTransportCosts(r_i.getId(), solution.getInstance().getBreakroom().getId());
        }

        if (j instanceof WardJob && r_i instanceof TherapyCenter) {

            WardJob wj = (WardJob) j;
            c_t_i += solution.getInstance().getTransportCosts(wj.getRoom().getId(), r_i.getId());
            c_t_i += solution.getInstance().getTransportCosts(r_i.getId(), wj.getRoom().getId());
        }
        c_delta = (c_hij + c_t_i) - c_hj;

        return c_delta;
    }

    @NonNull
    public Solution getSolution() {
        return this.solution;
    }

    public void setSolution(@NonNull Solution solution) {
        this.solution = solution;
    }
}
