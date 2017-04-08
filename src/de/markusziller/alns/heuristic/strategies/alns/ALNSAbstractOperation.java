package de.markusziller.alns.heuristic.strategies.alns;

import com.google.common.base.Objects;
import de.markusziller.alns.entities.*;
import de.markusziller.alns.entities.jobs.TreatmentJob;
import de.markusziller.alns.heuristic.strategies.alns.removal.Removal;
import de.markusziller.alns.heuristic.visualization.ALNSStrategieVisualizationManager;

import java.util.*;

public abstract class ALNSAbstractOperation implements IALNSOperation {
    protected final ALNSStrategieVisualizationManager asvm = new ALNSStrategieVisualizationManager();
    private final Random r = new Random();
    private int pi;
    private double p;
    private int draws;
    private double w;

    @Override
    public ALNSStrategieVisualizationManager getVisualizationManager() {
        return asvm;
    }

    @Override
    public void drawn() {
        draws++;
    }

    @Override
    public void addToPi(int pi) {
        this.pi += pi;
    }

    public ArrayList<Node> treatmentNodesToArrayList(Solution s) {
        ArrayList<Node> N = new ArrayList<>();
        Set<Node> N_all = s.getAllNodes();
        for (Node n : N_all) {
            if (n.getJob() instanceof TreatmentJob) {
                N.add(n);
            }
        }
        return N;
    }

    protected ArrayList<Removal> getRemovals(Solution s) {
        ArrayList<Removal> n = new ArrayList<>();
        for (Therapist t : s.getRoutes().keySet()) {
            for (Route r : s.getRoutes().get(t)) {
                for (Node node : r.getN()) {
                    if (node.getJob() instanceof TreatmentJob) {
                        n.add(new Removal(node, r));
                    }
                }
            }
        }
        return n;
    }

    protected Removal random(ArrayList<Removal> list) {
        return list.get(r.nextInt(list.size()));
    }

    public Removal[] closestPair(ArrayList<Removal> R, Instance i) {
        Removal[] l = new Removal[2];
        int min = Integer.MAX_VALUE;
        for (Removal r : R) {
            for (Removal r2 : R) {
                Node o = r2.getN();
                Node n = r.getN();
                if (r2.getR() != r.getR()) {
                    int d = i.getTravelTime(n.getRoom(), o.getRoom());
                    if (d < min) {
                        min = d;
                        l[0] = r2;
                        l[1] = r;
                    }
                }
            }
        }
        return l;
    }

    protected Removal[] closestPairRandomSelected(ArrayList<Removal> R, final Instance i, int top_n) {
        NavigableSet<Removal[]> set = new TreeSet<>(new Comparator<Removal[]>() {
            @Override
            public int compare(Removal[] o1, Removal[] o2) {
                Removal r1_a = o1[0];
                Removal r1_b = o1[1];
                Removal r2_a = o2[0];
                Removal r2_b = o2[1];
                Room[] r1_roomids = new Room[]{r1_a.getN().getRoom(), r1_b.getN().getRoom()};
                Room[] r2_roomids = new Room[]{r2_a.getN().getRoom(), r2_b.getN().getRoom()};
                if (r1_roomids[0] == r2_roomids[1] && r1_roomids[1] == r2_roomids[0]) {
                    return 0;
                }
                if (i.getTravelTime(r1_a.getN().getRoom(), r1_b.getN().getRoom()) < i.getTravelTime(r2_a.getN().getRoom(), r2_b.getN().getRoom())) {
                    return -1;
                }
                return 1;
            }
        });
        for (Removal r : R) {
            for (Removal r2 : R) {
                if (r2.getR() != r.getR()) {
                    Removal[] t_a = new Removal[]{r, r2};
                    set.add(t_a);
                    if (set.size() > top_n) {
                        set.pollLast();
                    }
                }
            }
        }
        Removal[][] arr = set.toArray(new Removal[0][0]);
        return arr[r.nextInt(arr[0].length)];
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("pi", pi).add("p", p).toString();
    }

    public int getPi() {
        return this.pi;
    }

    public void setPi(int pi) {
        this.pi = pi;
    }

    public double getP() {
        return this.p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public int getDraws() {
        return this.draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public double getW() {
        return this.w;
    }

    public void setW(double w) {
        this.w = w;
    }
}
