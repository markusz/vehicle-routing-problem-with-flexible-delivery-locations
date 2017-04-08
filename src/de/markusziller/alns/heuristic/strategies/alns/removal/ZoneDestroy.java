package de.markusziller.alns.heuristic.strategies.alns.removal;

import de.markusziller.alns.entities.Node;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;

import java.util.ArrayList;
import java.util.TreeSet;


public class ZoneDestroy extends ALNSAbstractOperation implements IALNSDestroy {
    @Override
    public Solution destroy(Solution s, int q) throws Exception {
        ArrayList<Removal> R = getRemovals(s);
        Removal r_s = closestPairRandomSelected(R, s.getInstance(), 10)[0];
        TreeSet<RelatedNode> ts = new TreeSet<>();
        R.remove(r_s);
        for (Removal rem : R) {
            Node o = r_s.getN();
            Node n = rem.getN();
            int i = s.getInstance().getTravelTime(o.getRoom(), n.getRoom());
            RelatedNode rn = new RelatedNode(i, rem);
            ts.add(rn);
        }
        s.remove(r_s.n, r_s.r);
        q--;
        for (RelatedNode rn : ts) {
            if (q > 0) {
                s.remove(rn.r.n, rn.r.r);
                q--;
            } else {
                break;
            }
        }
        return s;
    }

    class RelatedNode implements Comparable<RelatedNode> {
        final int d;
        final Removal r;

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
}
