package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Sets;

import de.tum.ziller.thesis.thrp.common.entities.Instance;
import de.tum.ziller.thesis.thrp.common.entities.Job;
import de.tum.ziller.thesis.thrp.common.entities.Qualification;
import de.tum.ziller.thesis.thrp.common.entities.Room;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.ALNSAbstractOperation;

/**
 * Zielt einen zuf�lligen Knoten. Entfernt anschlie�end q-1 Knoten die ihm am �hnlichsten sind, bzgl Raumeignung, Dauer und ben�tigten
 * Qualifikationen
 * 
 * @author Markus
 * 
 */
public class RelatedDestroy extends ALNSAbstractOperation implements IALNSDestroy {
	class RelatedNode implements Comparable<RelatedNode> {
		double	relatedess;
		// Node n;
		Removal	r;

		RelatedNode(double r, Removal rem) {
			relatedess = r;
			this.r = rem;
		}

		@Override
		public int compareTo(RelatedNode o) {
			if (o.relatedess < this.relatedess) {
				return -1;
			}
			return 1;
		}
	}

	@Override
	public Solution destroy(Solution from, int q) throws Exception {
		double w_r_t = 1. / 3., w_r_q = 1. / 3., w_r_ri = 1. / 3.;
		Random r = new Random();
		ArrayList<Removal> R = getRemovals(from);
		Removal seed = R.get(r.nextInt(R.size()));
		TreeSet<RelatedNode> ts = new TreeSet<>();
		R.remove(seed);
		for (Removal rem : R) {
			double r_t = computeTRelatedness(seed.n.getJob(), rem.n.getJob());
			double r_q = computeQRelatedness(seed.n.getJob(), rem.n.getJob());
			double r_ri = computeRIRelatedness(seed.n.getJob(), rem.n.getJob(), from.getInstance());
			double r_tot = w_r_t * r_t + w_r_q * r_q + w_r_ri * r_ri;
			RelatedNode rn = new RelatedNode(r_tot, rem);
			ts.add(rn);
		}
		from.remove(seed.n, seed.r);
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

	public double computeQRelatedness(Job i, Job j) {
		Set<Qualification> q_i = i.getQualifications();
		Set<Qualification> q_j = j.getQualifications();
		Set<Qualification> q_ij_un = Sets.union(q_i, q_j);
		Set<Qualification> q_ij_isec = Sets.intersection(q_i, q_j);
		if (q_ij_un.size() == q_ij_isec.size()) {
			return 1.;
		}
		return (double) q_ij_isec.size() / (double) q_ij_un.size();
	}

	public double computeTRelatedness(Job i, Job j) {
		return 1. - (double) Math.abs(i.getDurationSlots() - j.getDurationSlots()) / (double) Math.max(i.getDurationSlots(), j.getDurationSlots());
	}

	public double computeRIRelatedness(Job i, Job j, Instance is) {
		Set<Room> r_i = is.getEligibleRooms(i);
		Set<Room> r_j = is.getEligibleRooms(j);
		Set<Room> r_ij_un = Sets.union(r_i, r_j);
		Set<Room> r_ij_isec = Sets.intersection(r_i, r_j);
		if (r_ij_un.size() == r_ij_isec.size()) {
			return 1.;
		}
		return (double) r_ij_isec.size() / (double) r_ij_un.size();
	}
}
