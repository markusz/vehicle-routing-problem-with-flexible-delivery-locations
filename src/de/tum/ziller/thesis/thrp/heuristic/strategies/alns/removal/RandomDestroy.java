package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal;

import java.util.ArrayList;
import java.util.Random;

import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.common.exceptions.RouteConstructionException;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.ALNSAbstractOperation;

/**
 * Entfernt q zuf�llige Knoten
 * 
 * @author Markus
 * 
 */
public class RandomDestroy extends ALNSAbstractOperation implements IALNSDestroy {
	@Override
	public Solution destroy(Solution from, int q) {
		Random random = new Random();
		ArrayList<Removal> R = getRemovals(from);
		for (; q > 0; q--) {
			int idx = random.nextInt(R.size());
			Removal r = R.get(idx);
			R.remove(r);
			try {
				from.remove(r.n, r.r);
			} catch (RouteConstructionException e) {
				e.printStackTrace();
			}
		}
		return from;
	}
}