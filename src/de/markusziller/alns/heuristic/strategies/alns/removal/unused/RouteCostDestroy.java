package de.markusziller.alns.heuristic.strategies.alns.removal.unused;

import com.google.common.collect.TreeMultimap;
import de.markusziller.alns.entities.Node;
import de.markusziller.alns.entities.Route;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.entities.Therapist;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;
import de.markusziller.alns.heuristic.strategies.alns.removal.IALNSDestroy;
import de.markusziller.alns.heuristic.strategies.alns.removal.Removal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class RouteCostDestroy extends ALNSAbstractOperation implements IALNSDestroy {

    //default:
    private int cost_greater = 1;
    private int cost_lower = -1;

    public RouteCostDestroy(boolean highestCostFirst) {
        int modifier = highestCostFirst ? -1 : 1;
        cost_greater *= modifier;
        cost_lower *= modifier;
    }

    @Override
    public Solution destroy(final Solution from, int q) throws Exception {


        TreeMultimap<Therapist, Route> map = from.getRoutes();

        TreeSet<Route> routes = new TreeSet<>(new Comparator<Route>() {

            //Compares its two arguments for order. Returns a negative integer, zero,
            //or a positive integer as the first argument is less than, equal to, or greater than the second.
            @Override
            public int compare(Route o1, Route o2) {
                if (from.getRouteCosts(o1) > from.getRouteCosts(o2)) {
                    return cost_greater;
                }

                if (from.getRouteCosts(o1) < from.getRouteCosts(o2)) {
                    return cost_lower;
                }
                return 0;
            }
        });


        ArrayList<Removal> removals = new ArrayList<>();
//		int sum = 0;

        for (Therapist t : map.keySet()) {
            Set<Route> R = map.get(t);
            for (Route r : R) {

                if (r.noOfTreatmentJobs() > 0) {
                    routes.add(r);
//					sum += r.noOfTreatmentJobs();
                }
            }
        }


        for (Route r : routes) {
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

//		for (Removal w : removals) {
//				from.remove(w.n, w.r);
//		}

        return from;
    }
}
