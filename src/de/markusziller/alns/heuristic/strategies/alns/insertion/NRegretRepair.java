package de.markusziller.alns.heuristic.strategies.alns.insertion;

import de.markusziller.alns.entities.*;
import de.markusziller.alns.exceptions.GeneralInfeasibilityException;
import de.markusziller.alns.exceptions.RouteConstructionException;
import de.markusziller.alns.utils.TimeUtil;

import java.util.*;

public class NRegretRepair extends ALNSAbstractRepair implements IALNSRepair {
    private final int n;

    public NRegretRepair(int n) {
        this.n = n;
    }

    @Override
    public Solution repair(Solution s) {
        List<Job> jj = new ArrayList<>(s.getUnscheduledJobs());
        Collections.shuffle(jj);
        for (Job j : jj) {
            try {
                s = planNextJob(s, j);
            } catch (GeneralInfeasibilityException | RouteConstructionException ignored) {
            }
        }
        return s;
    }

    private Solution planNextJob(Solution s, Job j) throws GeneralInfeasibilityException, RouteConstructionException {
        NavigableSet<NRegret> regr = new TreeSet<>();
        Set<Job> I_uns = s.getUnscheduledJobs();
        NRegret n_r = new NRegret(n);
        Collection<Therapist> p_j = s.getInstance().getProficientTherapists(j);
        Collection<Room> r_j = s.getInstance().getEligibleRooms(j);
        List<Timeslot> t_j = j.getAvailabilty();
        for (Therapist p : p_j) {
            Node[] n_p = s.getAllNodes(p).toArray(new Node[0]);
            for (int i = 0; i < n_p.length; i++) {
                Node n = n_p[i];
                if (n.isIdle()) {
                    Node m = n_p[i - 1];
                    Node o = n_p[i + 1];
                    for (Room r : r_j) {
                        int t_b = s.getInstance().getTravelTime(m.getRoom(), r);
                        int t_a = s.getInstance().getTravelTime(r, o.getRoom());
                        if (n.getTime().getLength() >= j.getDurationSlots() && (n.getStart() + t_b) <= (n.getEnd() - t_a)) {
                            int ss = n.getStart() + t_b;
                            int ee = n.getEnd() - t_a;
                            Timeslot ts_p = new Timeslot(ss, ee);
                            Set<Timeslot> tss = s.getVacanciesForAllRooms().get(r);
                            List<Timeslot> slots = TimeUtil.getIntersection(new ArrayList<>(tss), ts_p);
                            slots = TimeUtil.getIntersection(slots, t_j);
                            for (Timeslot ts : slots) {
                                if (ts.getLength() >= j.getDurationSlots()) {
                                    int i_e = ts.getStart();
                                    int i_l = (ts.getEnd() - j.getDurationSlots()) - 1;
                                    int ii = i_e;
                                    if (ts.getLength() > j.getDurationSlots() && Math.random() < 0.5) {
                                        ii = i_l;
                                    }
                                    int end = TimeUtil.getEnd(ii, j.getDurationSlots());
                                    Insertion is = new Insertion(new Node("", r, j, new Timeslot(ii, end)), p);
                                    is.setCosts(getNodeCosts(s, is));
                                    n_r.add(is);
                                }
                            }
                        }
                    }
                }
            }
        }
        regr.add(n_r);
        // }
        Insertion i;
        try {
            i = regr.first().s.first();
        } catch (Exception e) {
            throw new GeneralInfeasibilityException(I_uns.size() + " remaining", s.getUid());
        }
        if (i == null) {
            throw new GeneralInfeasibilityException(I_uns.size() + " remaining", s.getUid());
        }
        s.apply(i);
        s.getUnscheduledJobs().remove(i.getNode().getJob());
        asvm.onJobPlanned(this, i, s);
        return s;
    }

    class NRegret implements Comparable<NRegret> {
        private final NavigableSet<Insertion> s = new TreeSet<>();
        private final int n;

        public NRegret(int n) {
            this.n = n;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("{");
            for (Insertion i : s) {
                sb.append(i.getCosts()).append(",");
            }
            sb.append("-->").append(getRegret()).append("}");
            return sb.toString();
        }

        private double getRegret() {
            if (s.size() > 0) {
                return s.last().getCosts() - s.first().getCosts();
            }
            return 0.;
        }

        public void add(Insertion i) {
            if (s.size() < n) {
                s.add(i);
            } else {
                s.add(i);
                s.pollLast();
            }
        }

        @Override
        public int compareTo(NRegret o) {
            if (getRegret() < o.getRegret()) {
                return 1;
            }
            if (getRegret() > o.getRegret()) {
                return -1;
            }
            return 0;
        }
    }
}
