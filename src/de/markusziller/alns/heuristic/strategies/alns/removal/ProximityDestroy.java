package de.markusziller.alns.heuristic.strategies.alns.removal;

import de.markusziller.alns.entities.Node;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;


public class ProximityDestroy extends ALNSAbstractOperation implements IALNSDestroy {
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

    class RelatedNode implements Comparable<RelatedNode> {
        final int distance;
        final Removal r;

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
}
