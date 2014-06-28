package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal;

import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.TreeMultimap;

import de.tum.ziller.thesis.thrp.common.entities.Instance;
import de.tum.ziller.thesis.thrp.common.entities.Node;
import de.tum.ziller.thesis.thrp.common.entities.Route;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.common.entities.Therapist;
import de.tum.ziller.thesis.thrp.common.entities.jobs.BreakJob;
import de.tum.ziller.thesis.thrp.common.entities.jobs.TreatmentJob;
import de.tum.ziller.thesis.thrp.common.entities.jobs.WardJob;
import de.tum.ziller.thesis.thrp.common.entities.rooms.TherapyCenter;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.ALNSAbstractOperation;

/**
 * Entfernt die Knoten, deren Existenz die Kosten der Routen am meisten erh�ht. Es werden zuerst alle Knoten ausgew�hlt und dann sukzessive
 * entfernt
 * 
 * @author Markus
 * 
 */
public class WorstCostDestroy extends ALNSAbstractOperation implements IALNSDestroy {
	class WorstCostNode implements Comparable<WorstCostNode> {
		double	c;
		Removal	n;

		WorstCostNode(double r, Removal n) {
			c = r;
			this.n = n;
		}

		@Override
		public String toString() {
			return n + ": " + c;
		}

		@Override
		public int compareTo(WorstCostNode o) {
			if (o.c < this.c) {
				return -1;
			}
			return 1;
		}
	}

	@Override
	public Solution destroy(Solution s, int q) throws Exception {
		Instance ii = s.getInstance();
		TreeSet<WorstCostNode> ts = new TreeSet<>();
		TreeMultimap<Therapist, Route> map = s.getRoutes();
		for (Therapist t : map.keySet()) {
			Set<Route> R = map.get(t);
			for (Route r : R) {
				Node[] nodes = r.getN().toArray(new Node[0]);
				if (nodes.length < 3) {
				} else {
					for (int i = 1; i < nodes.length; i++) {
						Node l = null;
						final Node m = nodes[i];
						Node n = null;
						if (!(m.getJob() instanceof TreatmentJob)) {
							continue;
						}
						for (int j = i - 1; j >= 0; j--) {
							if (nodes[j].getJob() instanceof TreatmentJob || nodes[j].getJob() instanceof BreakJob) {
								l = nodes[j];
								break;
							}
						}
						for (int j = i + 1; j < nodes.length; j++) {
							if (nodes[j].getJob() instanceof TreatmentJob || nodes[j].getJob() instanceof BreakJob) {
								n = nodes[j];
								break;
							}
						}
						if (n == null || l == null) {
							continue;
						}
						double c_lmn = 0.;
						double c_ln = 0.;
						c_lmn += ii.getRouteCosts(l.getRoom(), m.getRoom());
						c_lmn += ii.getRouteCosts(m.getRoom(), n.getRoom());
						if (m.getJob() instanceof WardJob && m.getRoom() instanceof TherapyCenter) {
							WardJob j_m = (WardJob) m.getJob();
							 c_lmn += ii.getTransportCosts(j_m.getRoom(), m.getRoom());
							 c_lmn += ii.getTransportCosts(j_m.getRoom(), m.getRoom());
						}
						// Berechne die kosten ohne m
						c_ln += ii.getRouteCosts(l.getRoom(), n.getRoom());
						// Zusatzkosten durch m
						double c_d = c_lmn - c_ln;
						ts.add(new WorstCostNode(c_d, new Removal(m, r)));
					}
				}
			}
		}
		for (WorstCostNode w : ts) {
			if (q > 0) {
				s.remove(w.n.n, w.n.r);
				q--;
			} else {
				break;
			}
		}
		return s;
	}
}
