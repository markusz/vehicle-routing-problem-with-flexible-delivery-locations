package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal;

import de.tum.ziller.thesis.thrp.common.entities.Node;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.ALNSAbstractOperation;

import java.util.ArrayList;

/**
 * Zieht einen zuf�lligen Startknoten und konstruiert von ihm ausgehend eine Route der L�nge q nach dem Greedy Prinzip. Diese Route wird
 * anschlie�end entfernt
 * 
 * @author Markus
 * 
 */
public class SubrouteDestroy extends ALNSAbstractOperation implements IALNSDestroy {
	class RelatedNode implements Comparable<RelatedNode> {
		int		d;
		Removal	r;

		RelatedNode(int r, Removal rem) {
			d = r;
			this.r = rem;
		}

		@Override
		public int compareTo(RelatedNode o) {
			if (o.d < this.d) {
				return 1;
			}
			return -1;
		}
	}

	@Override
	public Solution destroy(Solution from, int q) throws Exception {
		ArrayList<Removal> R = getRemovals(from);
		ArrayList<Removal> R_arr = new ArrayList<>();
		Removal r_s = null;
		r_s = random(R);
		Removal curr_seed = null;
		curr_seed = r_s;
		R.remove(r_s);
		R_arr.add(r_s);
		q--;
		while (q > 0) {
			int best = Integer.MAX_VALUE;
			Removal q_best = null;
			for (Removal rem : R) {
				if (!(R_arr.contains(rem))) {
					Node o = curr_seed.getN();
					Node n = rem.getN();
					int i = from.getInstance().getTravelTime(o.getRoom(), n.getRoom());
					if (i < best) {
						best = i;
						q_best = rem;
					}
				}
			}
			R_arr.add(q_best);
			q--;
			curr_seed = q_best;
		}
		for (Removal rn : R_arr) {
			if (q > 0) {
				from.remove(rn.n, rn.r);
				q--;
			} else {
				break;
			}
		}
		return from;
	}
}
