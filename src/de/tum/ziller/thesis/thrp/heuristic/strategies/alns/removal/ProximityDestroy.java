package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

import de.tum.ziller.thesis.thrp.common.entities.Node;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.ALNSAbstractOperation;

/**
 * Zieht einen zuf�lligen Startknoten und entfernt die q am n�chsten gelegenen Knoten
 * 
 * @author Markus
 * 
 */
public class ProximityDestroy extends ALNSAbstractOperation implements IALNSDestroy {
	class RelatedNode implements Comparable<RelatedNode> {
		int		distance;
		Removal	r;

		RelatedNode(int r, Removal rem) {
			distance = r;
			this.r = rem;
		}

		@Override
		public int compareTo(RelatedNode o) {
			if (o.distance < this.distance) {
				return 1;
			}
			return -1;
		}
	}

	@Override
	public Solution destroy(Solution from, int q) throws Exception {
		Random random = new Random();
		ArrayList<Removal> R = getRemovals(from);
		Removal r_s = R.get(random.nextInt(R.size()));
		TreeSet<RelatedNode> ts = new TreeSet<>();
		R.remove(r_s);
		for (Removal r : R) {
			Node o = r_s.getN();
			Node n = r.getN();
			int i = from.getInstance().getTravelTime(o.getRoom(), n.getRoom());
			RelatedNode rn = new RelatedNode(i, r);
			ts.add(rn);
		}
		from.remove(r_s.n, r_s.r);
		q--;
		for (RelatedNode rn : ts) {
			if (q > 0) {
				from.remove(rn.r.n, rn.r.r);
				q--;
			} else {
				break;
			}
		}
		return from;
	}
}