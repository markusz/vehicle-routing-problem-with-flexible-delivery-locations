package de.markusziller.alns.heuristic.strategies.alns.insertion;

import com.google.common.collect.TreeMultimap;
import de.markusziller.alns.entities.*;
import de.markusziller.alns.exceptions.RouteConstructionException;
import de.markusziller.alns.utils.TimeUtil;

import java.util.*;


public class GreedyRepair extends ALNSAbstractRepair implements IALNSRepair {
    @Override
    public Solution repair(Solution s) {
        List<Job> jj = new ArrayList<>(s.getUnscheduledJobs());
        Collections.shuffle(jj);
        for (Job j : jj) {
            try {
                s = planNextJob(s, j);
            } catch (RouteConstructionException ignored) {
            }
        }
        return s;
    }

    private Solution planNextJob(Solution s, Job j) throws RouteConstructionException {
        Insertion i_best = null;
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
                                    // kann in 1 eingef�gt werden -null AND is_c >= i_arr[0]_c
                                    if (i_best == null || is.getCosts() < i_best.getCosts()) {
                                        i_best = is;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (i_best == null) {
            throw new RouteConstructionException("");
        }
        s.apply(i_best);
        s.getUnscheduledJobs().remove(i_best.getNode().getJob());
        asvm.onJobPlanned(this, i_best, s);
        return s;
    }

    public Solution planNextJobOLD(Solution s, Job j) throws RouteConstructionException {
        TreeMultimap<Room, Timeslot> T = s.getVacanciesForAllRooms();
        Insertion i_best = null;
        Collection<Therapist> p_j = s.getInstance().getProficientTherapists(j);
        Collection<Room> r_j = s.getInstance().getEligibleRooms(j);
        List<Timeslot> t_j = j.getAvailabilty();
        for (Therapist p : p_j) {
            // freie Zeiten des Therapeuten
            List<Timeslot> t_p = s.getIdleTimeForTherapist(p);
            List<Timeslot> t_pj = TimeUtil.getIntersection(t_j, t_p);
            for (Room r : r_j) {
                // Wann ist der Raum frei
                // Schnittmenge aus Therapeuten, Job und Raumverf�gbarkeit
                List<Timeslot> l = new LinkedList<>(T.get(r));
                List<Timeslot> t_pjr = TimeUtil.getIntersection(t_pj, l);
                for (Timeslot ts : t_pjr) {
                    Room before = s.getLocation(ts.getStart() - 1, p);
                    Room after = s.getLocation(ts.getEnd() + 1, p);
                    int t_b = s.getInstance().getTravelTime(before, r);
                    int t_a = s.getInstance().getTravelTime(r, after);
                    // Timeslot avail = new Timeslot(ts.getStart()+t_b, ts.getEnd()-t_a);
                    ts.setStart(ts.getStart() + t_b);
                    ts.setEnd(ts.getEnd() - t_a);
                    if (ts.getLength() >= j.getDurationSlots()) {
                        Random ran = new Random();
                        int i_e = ts.getStart();
                        int i_l = ts.getEnd() - j.getDurationSlots();
                        int i = ts.getStart();
                        if (i_e < i_l) {
                            i = ran.nextInt(i_l - i_e) + i_e;
                        }
                        int end = TimeUtil.getEnd(i, j.getDurationSlots());
                        Insertion is = new Insertion(new Node("", r, j, new Timeslot(i, end)), p);
                        is.setCosts(getNodeCosts(s, is));
                        // kann in 1 eingef�gt werden -null AND is_c >= i_arr[0]_c
                        if (i_best == null || is.getCosts() < i_best.getCosts()) {
                            i_best = is;
                        }
                    }
                }
            }
        }
        if (i_best == null) {
            throw new RouteConstructionException("");
        }
        s.apply(i_best);
        s.getUnscheduledJobs().remove(i_best.getNode().getJob());
        asvm.onJobPlanned(this, i_best, s);
        return s;
    }
}
