package de.markusziller.alns.heuristic.strategies.alns.removal;

import de.markusziller.alns.entities.Node;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;

import java.util.ArrayList;


public class ProximityZoneDestroy extends ALNSAbstractOperation implements IALNSDestroy {
    @Override
    public Solution destroy(Solution from, int q) throws Exception {
        Removal r_s = null;
        ArrayList<Removal> R = getRemovals(from);
        ArrayList<Removal> R_sel = new ArrayList<>();
        r_s = random(R);
        R.remove(r_s);
        int A_all = (from.getInstance().getMaxX() - from.getInstance().getMinX()) * from.getInstance().getMaxY() - from.getInstance().getMinY();
        double A_zone = Math.sqrt((double) A_all / (double) from.getInstance().getJobs().size());
        Area a = new Area(r_s.n.getRoom().getX(), r_s.n.getRoom().getY(), A_zone);
        R_sel.add(r_s);
        q--;
        while (q > 0) {
            for (Removal r : R) {
                if (q > 0) {
                    if (a.isInside(r.n)) {
                        if (!R_sel.contains(r)) {
                            R_sel.add(r);
                            q--;
                        }
                    }
                } else {
                    break;
                }
            }
            if (q > 0) {
                a.increaseSurface(4.);
            }
        }
        for (Removal rn : R_sel) {
            from.remove(rn.n, rn.r);
        }
        return from;
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

    class Area {
        final int x_c;
        final int y_c;
        double d;

        @java.beans.ConstructorProperties({"x_c", "y_c", "d"})
        public Area(int x_c, int y_c, double d) {
            this.x_c = x_c;
            this.y_c = y_c;
            this.d = d;
        }

        public boolean isInside(Node n) {
            int x = n.getRoom().getX();
            int y = n.getRoom().getY();
            return x <= x_c + d / 2 && x >= x_c - d / 2 && y <= y_c + d / 2 && y >= y_c - d / 2;
        }

        public double surfaceSize() {
            return d * d;
        }

        public void increaseSurface(double factor) {
            double f = Math.sqrt(d * d * factor) / d;
            d *= f;
        }
    }
}
