package de.markusziller.alns.heuristic.strategies.alns.removal;

import com.google.common.collect.TreeMultimap;
import de.markusziller.alns.entities.Node;
import de.markusziller.alns.entities.Route;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.entities.Therapist;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;


public class RandomRouteDestroy extends ALNSAbstractOperation implements IALNSDestroy {
    @Override
    public Solution destroy(Solution s, int q) throws Exception {
        TreeMultimap<Therapist, Route> map = s.getRoutes();
        ArrayList<Route> routes = new ArrayList<>();
        ArrayList<Removal> removals = new ArrayList<>();
        for (Therapist t : map.keySet()) {
            Set<Route> R = map.get(t);
            for (Route r : R) {
                if (r.noOfTreatmentJobs() > 0) {
                    routes.add(r);
                }
            }
        }
        Random random = new Random();
        Route r = routes.get(random.nextInt(routes.size()));
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
        for (Removal w : removals) {
            s.remove(w.n, w.r);
        }
        return s;
    }
}
