package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal.unused;

import com.google.common.collect.TreeMultimap;
import de.tum.ziller.thesis.thrp.common.entities.Node;
import de.tum.ziller.thesis.thrp.common.entities.Route;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.common.entities.Therapist;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.ALNSAbstractOperation;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal.IALNSDestroy;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal.Removal;

import java.util.ArrayList;
import java.util.Set;

public class NeighborhoodDestroy extends ALNSAbstractOperation implements IALNSDestroy {

	
	
	@Override
	public Solution destroy(final Solution from, int q) throws Exception {


		TreeMultimap<Therapist, Route> map = from.getRoutes();

		ArrayList<Removal> removals = new ArrayList<>();
		// int sum = 0;
		
		for (Therapist t : map.keySet()) {
			Set<Route> R = map.get(t);
			for (Route r : R) {
				Node[] nodes = r.getMostExpensiveArcs(from.getInstance());
				removals.add(new Removal(nodes[1], r));
			}
		}

//		for (Route r : routes) {
//			if (q < 1) {
//				break;
//			}
//			for (Node m : r.getN()) {
//
//				if (q > 0) {
//					if (m.isTreatment()) {
//						removals.add(new Removal(m, r));
//						q--;
//					}
//				} else {
//					break;
//				}
//			}
//		}
//
//		for (Removal w : removals) {
//			from.remove(w.n, w.r);
//		}

		return from;
	}
}
