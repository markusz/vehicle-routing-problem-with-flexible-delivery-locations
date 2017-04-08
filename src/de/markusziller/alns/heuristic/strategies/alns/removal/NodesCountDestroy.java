package de.markusziller.alns.heuristic.strategies.alns.removal;

import com.google.common.collect.TreeMultimap;
import de.markusziller.alns.entities.Node;
import de.markusziller.alns.entities.Route;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.entities.Therapist;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;


public class NodesCountDestroy extends ALNSAbstractOperation implements IALNSDestroy {
    private int c_g = 1;
    private int c_l = -1;

    public NodesCountDestroy(boolean highestFirst) {
        int modifier = highestFirst ? -1 : 1;
        c_g *= modifier;
        c_l *= modifier;
    }

    @Override
    public Solution destroy(final Solution from, int q) throws Exception {
        TreeMultimap<Therapist, Route> map = from.getRoutes();
        TreeSet<Route> routes = new TreeSet<>(new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                if (o1.noOfTreatmentJobs() > o2.noOfTreatmentJobs()) {
                    return c_g;
                }
                if (o1.noOfTreatmentJobs() < o2.noOfTreatmentJobs()) {
                    return c_l;
                }
                return 0;
            }
        });
        ArrayList<Removal> removals = new ArrayList<>();
        for (Therapist t : map.keySet()) {
            Set<Route> R = map.get(t);
            for (Route r : R) {
                if (r.noOfTreatmentJobs() > 0) {
                    routes.add(r);
                }
            }
        }
        for (Route r : routes) {
            if (r.noOfTreatmentJobs() <= q) {
                if (q < 1) {
                    break;
                }
                for (Node m : r.getN()) {
                    if (q > 0) {
                        if (m.isTreatment()) {
                            removals.add(new Removal(m, r));
                            q--;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        for (Removal w : removals) {
            from.remove(w.n, w.r);
        }
        return from;
    }
}
