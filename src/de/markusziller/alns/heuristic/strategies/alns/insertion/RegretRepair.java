package de.markusziller.alns.heuristic.strategies.alns.insertion;

import com.google.common.collect.TreeMultimap;
import de.markusziller.alns.entities.*;
import de.markusziller.alns.exceptions.GeneralInfeasibilityException;
import de.markusziller.alns.exceptions.RouteConstructionException;
import de.markusziller.alns.utils.TimeUtil;

import java.util.*;

public class RegretRepair extends ALNSAbstractRepair implements IALNSRepair {
    @Override
    public Solution repair(Solution s) {
        List<Job> jj = new ArrayList<>(s.getUnscheduledJobs());
        Collections.shuffle(jj);
        for (Job j : jj) {
            try {
                s = planNextJob(s, j);
                // System.out.println("planned job");
            } catch (GeneralInfeasibilityException | RouteConstructionException ignored) {
            }
        }
        return s;
    }

    private Solution planNextJob(Solution s, Job j) throws GeneralInfeasibilityException, RouteConstructionException {
        TreeMultimap<Room, Timeslot> T = s.getVacanciesForAllRooms();
        Set<Job> I_uns = s.getUnscheduledJobs();
        Insertion i_s = null;
        double i_s_delta = Double.NEGATIVE_INFINITY;
        List<Job> uns_l = new ArrayList<>(I_uns);
        Collections.shuffle(uns_l);
        Insertion[] i_arr = new Insertion[2];
        Collection<Therapist> p_j = s.getInstance().getProficientTherapists(j);
        Collection<Room> r_j = s.getInstance().getEligibleRooms(j);
        List<Timeslot> t_j = j.getAvailabilty();
        for (Therapist p : p_j) {
            // freie Zeiten des Therapeuten
            List<Timeslot> t_pj = TimeUtil.getIntersection(t_j, s.getIdleTimeForTherapist(p));
            for (Room r : r_j) {
                // Wann ist der Raum frei
                // Schnittmenge aus Therapeuten, Job und Raumverf�gbarkeit
                List<Timeslot> l = new LinkedList<>(T.get(r));
                List<Timeslot> t_pjr = TimeUtil.getIntersection(t_pj, l);
                for (Timeslot ts : t_pjr) {
                    // Slot nur relevant wenn Job reinpasst
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
                        // for (int i = timeslot.getStart(); i <= timeslot.getEnd() - j.getDurationSlots() + 1; i++) {
                        int end = TimeUtil.getEnd(i, j.getDurationSlots());
                        Insertion is = new Insertion(new Node("", r, j, new Timeslot(i, end)), p);
                        is.setCosts(getNodeCosts(s, is));
                        // kann in 1 eingef�gt werden -null AND is_c >= i_arr[0]_c
                        if (i_arr[0] == null || is.getCosts() < i_arr[0].getCosts()) {
                            i_arr[1] = i_arr[0];
                            i_arr[0] = is;
                        } else {
                            if (i_arr[1] == null || is.getCosts() < i_arr[1].getCosts()) {
                                i_arr[1] = is;
                            }
                        }
                        // i_.add(is);
                        // }
                    }
                }
            }
        }
        if (i_arr[1] == null) {
            if (i_arr[0] != null && i_s == null) {
                i_s = i_arr[0];
            }
        } else {
            double delta = i_arr[1].getCosts() - i_arr[0].getCosts();
            if (delta > i_s_delta) {
                i_s = i_arr[0];
                i_s_delta = delta;
            }
        }
        if (i_s == null) {
            throw new GeneralInfeasibilityException(I_uns.size() + " remaining", s.getUid());
        }
        s.apply(i_s);
        s.getUnscheduledJobs().remove(i_s.getNode().getJob());
        asvm.onJobPlanned(this, i_s, s);
        return s;
    }

    public Solution planNextJob(Solution s, List<Insertion> tabus) throws GeneralInfeasibilityException, RouteConstructionException {
        TreeMultimap<Room, Timeslot> T = s.getVacanciesForAllRooms();
        Set<Job> I_uns = s.getUnscheduledJobs();
        Insertion i_s = null;
        double i_s_delta = Double.NEGATIVE_INFINITY;
        List<Job> uns_l = new ArrayList<>(I_uns);
        Collections.shuffle(uns_l);
        for (Job j : uns_l) {
            // Set<Insertion> i_ = new TreeSet<>();
            Insertion[] i_arr = new Insertion[2];
            Collection<Therapist> p_j = s.getInstance().getProficientTherapists(j);
            Collection<Room> r_j = s.getInstance().getEligibleRooms(j);
            List<Timeslot> t_j = j.getAvailabilty();
            for (Therapist p : p_j) {
                // freie Zeiten des Therapeuten
                List<Timeslot> t_pj = TimeUtil.getIntersection(t_j, s.getIdleTimeForTherapist(p));
                for (Room r : r_j) {
                    // Wann ist der Raum frei
                    // Schnittmenge aus Therapeuten, Job und Raumverf�gbarkeit
                    List<Timeslot> l = new LinkedList<>(T.get(r));
                    List<Timeslot> t_pjr = TimeUtil.getIntersection(t_pj, l);
                    for (Timeslot ts : t_pjr) {
                        // Slot nur relevant wenn Job reinpasst
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
                            // for (int i = timeslot.getStart(); i <= timeslot.getEnd() - j.getDurationSlots() + 1; i++) {
                            int end = TimeUtil.getEnd(i, j.getDurationSlots());
                            Insertion is = new Insertion(new Node("", r, j, new Timeslot(i, end)), p);
                            is.setCosts(getNodeCosts(s, is));
                            // kann in 1 eingef�gt werden -null AND is_c >= i_arr[0]_c
                            if (i_arr[0] == null || is.getCosts() < i_arr[0].getCosts()) {
                                i_arr[1] = i_arr[0];
                                i_arr[0] = is;
                            } else {
                                if (i_arr[1] == null || is.getCosts() < i_arr[1].getCosts()) {
                                    i_arr[1] = is;
                                }
                            }
                            // i_.add(is);
                            // }
                        }
                    }
                }
            }
            if (i_arr[1] == null) {
                if (i_arr[0] != null && i_s == null) {
                    i_s = i_arr[0];
                }
            } else {
                double delta = i_arr[1].getCosts() - i_arr[0].getCosts();
                if (delta > i_s_delta) {
                    i_s = i_arr[0];
                    i_s_delta = delta;
                }
            }
        }
        if (i_s == null) {
            throw new GeneralInfeasibilityException(I_uns.size() + " remaining", s.getUid());
        }
        s.apply(i_s);
        s.getUnscheduledJobs().remove(i_s.getNode().getJob());
        asvm.onJobPlanned(this, i_s, s);
        return s;
    }
}
