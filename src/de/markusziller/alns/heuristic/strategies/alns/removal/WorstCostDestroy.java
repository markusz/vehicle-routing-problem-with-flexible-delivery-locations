package de.markusziller.alns.heuristic.strategies.alns.removal;

import com.google.common.collect.TreeMultimap;
import de.markusziller.alns.entities.*;
import de.markusziller.alns.entities.jobs.BreakJob;
import de.markusziller.alns.entities.jobs.TreatmentJob;
import de.markusziller.alns.entities.jobs.WardJob;
import de.markusziller.alns.entities.rooms.TherapyCenter;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;

import java.util.Set;
import java.util.TreeSet;


public class WorstCostDestroy extends ALNSAbstractOperation implements IALNSDestroy {
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

    class WorstCostNode implements Comparable<WorstCostNode> {
        final double c;
        final Removal n;

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
}
