package de.markusziller.alns.entities;

import com.google.common.collect.TreeMultimap;
import com.rits.cloning.Cloner;
import de.markusziller.alns.utils.Comparators;
import de.markusziller.alns.entities.jobs.*;
import de.markusziller.alns.entities.rooms.TherapyCenter;
import de.markusziller.alns.exceptions.RouteConstructionException;
import de.markusziller.alns.utils.OutputUtil;
import de.markusziller.alns.utils.TimeUtil;
import de.markusziller.alns.heuristic.SolverConfiguration;

import java.io.Serializable;
import java.util.*;

public class Solution implements Cloneable, Serializable {

    private static final long serialVersionUID = 153274005962954988L;
    private transient Long t = 0L;
    private Double costs = 0.;
    private transient Cloner cloner = new Cloner();
    private transient Solution predecessor = null;
    private TreeMultimap<Therapist, Route> routes = TreeMultimap.create(Comparators.THERAPIST_ID_ASCENDING, Comparators.ROUTE_START_ASCENDING);
    private Long t_s = System.currentTimeMillis();
    private Long t_S_init;
    private Long t_S_compl;
    private transient String uid = UUID.randomUUID().toString();
    private transient SolutionStatusWrapper ssw = SolutionStatusWrapper.createNew();
    private Integer minutesPerTimeslot;
    private Set<Job> unscheduledJobs;
    private Instance instance;
    private SolverConfiguration config;
    private Insertion i_last;
    private transient TreeMultimap<Therapist, Node> P_routes = TreeMultimap.create(Comparators.THERAPIST_ID_ASCENDING, Comparators.NODE_START_ASCENDING);
    private transient Set<Node> N_all = new TreeSet<>(Comparators.NODE_START_ASCENDING);

    public Solution(Instance is, SolverConfiguration sc) {
        instance = is;
        unscheduledJobs = is.getJobs();
        config = sc;
        minutesPerTimeslot = is.getI_conf().getMinutesPerTimeslot();
    }

    public Long getT() {
        return this.t;
    }

    public void setT(Long t) {
        this.t = t;
    }

    public Double getCosts() {
        return this.costs;
    }

    public void setCosts(Double costs) {
        this.costs = costs;
    }

    public Cloner getCloner() {
        return this.cloner;
    }

    public void setCloner(Cloner cloner) {
        this.cloner = cloner;
    }

    public Solution getPredecessor() {
        return this.predecessor;
    }

    public void setPredecessor(Solution predecessor) {
        this.predecessor = predecessor;
    }

    public TreeMultimap<Therapist, Route> getRoutes() {
        return this.routes;
    }

    public void setRoutes(TreeMultimap<Therapist, Route> routes) {
        this.routes = routes;
    }

    public Long getT_s() {
        return this.t_s;
    }

    public void setT_s(Long t_s) {
        this.t_s = t_s;
    }

    public Long getT_S_init() {
        return this.t_S_init;
    }

    public void setT_S_init(Long t_S_init) {
        this.t_S_init = t_S_init;
    }

    public Long getT_S_compl() {
        return this.t_S_compl;
    }

    public void setT_S_compl(Long t_S_compl) {
        this.t_S_compl = t_S_compl;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public SolutionStatusWrapper getSsw() {
        return this.ssw;
    }

    public void setSsw(SolutionStatusWrapper ssw) {
        this.ssw = ssw;
    }

    public Integer getMinutesPerTimeslot() {
        return this.minutesPerTimeslot;
    }

    public void setMinutesPerTimeslot(Integer minutesPerTimeslot) {
        this.minutesPerTimeslot = minutesPerTimeslot;
    }

    public Set<Job> getUnscheduledJobs() {
        return this.unscheduledJobs;
    }

    public void setUnscheduledJobs(Set<Job> unscheduledJobs) {
        this.unscheduledJobs = unscheduledJobs;
    }

    public Instance getInstance() {
        return this.instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public SolverConfiguration getConfig() {
        return this.config;
    }

    public void setConfig(SolverConfiguration config) {
        this.config = config;
    }

    public Insertion getI_last() {
        return this.i_last;
    }

    public void setI_last(Insertion i_last) {
        this.i_last = i_last;
    }

    public TreeMultimap<Therapist, Node> getP_routes() {
        return this.P_routes;
    }

    public void setP_routes(TreeMultimap<Therapist, Node> P_routes) {
        this.P_routes = P_routes;
    }

    public Set<Node> getN_all() {
        return this.N_all;
    }

    public void setN_all(Set<Node> N_all) {
        this.N_all = N_all;
    }

    public void nullifyWrapper() {
        ssw = null;
    }


    public Route apply(Insertion i) throws RouteConstructionException {

        Set<Route> R = routes.get(i.getTherapist());
        for (Route r : R) {
            if (r.getStartTime() <= i.getStart() && r.getEndTime() >= i.getEnd()) {
                r.insert(i.getNode());
                r.cleanup();
                update();

                costs = getSolutionCosts();
                i_last = i;
                return r;
            }
        }
        throw new RouteConstructionException("No fitting Pathway of Therapist " + i.getTherapist().getName() + " has been found for the PNI");
    }


    public void remove(Node n) throws RouteConstructionException {
        for (Therapist t : routes.keySet()) {
            Set<Route> R = routes.get(t);
            for (Route r : R) {
                if (r.contains(n)) {
                    r.remove(n);
                    unscheduledJobs.add(n.getJob());
                    update();

                    costs = getSolutionCosts();
                    return;
                }
            }
        }

    }


    public void remove(Node n, Route r) throws RouteConstructionException {

        if (!r.contains(n)) {
            System.err.println("Node not on Route");
        }

        if (r.contains(n)) {
            r.remove(n);
            unscheduledJobs.add(n.getJob());
            update();
            costs = getSolutionCosts();
        }

    }

    public Set<Node> getAllNodes() {
        return N_all;
    }

    public Set<Node> getAllNodes(Therapist t) {
        return P_routes.get(t);
    }

    public List<Timeslot> getIdleTimeForTherapist(Therapist t) {
        return new ArrayList<>(ssw.P_availabilities.get(t));
    }

    public Room getLocation(Integer t, Therapist tp) {
        return ssw.P_locations.get(tp)[t];
    }

    public TreeMultimap<Room, Timeslot> getVacanciesForAllRooms() {
        return ssw.R_availabilities;
    }


    public void update() {

        updateAllNodes();
        updateTherapistLocations();
        updateTherapistAvailabilities();
        updateRoomAvailabilities();
    }

    private void updateAllNodes() {
        P_routes.clear();
        N_all.clear();

        for (Therapist t : routes.keySet()) {
            for (Route r : routes.get(t)) {
                Set<Node> nodes = r.getN();
                P_routes.putAll(t, nodes);
                N_all.addAll(r.getN());
            }
        }
    }

    private void updateTherapistLocations() {
        ssw.P_locations.clear();
        Set<Therapist> t = instance.getTherapists();
        int r_size = instance.getI_conf().getNumberOfTimeSlots() + 1;

        for (Therapist th : t) {
            Room[] rr = new Room[r_size];
            for (Route r : routes.get(th)) {
                for (Node n : r.getN()) {
                    for (int i = n.getStart(); i <= n.getEnd(); i++) {
                        rr[i] = n.getRoom();
                    }
                }
            }
            ssw.P_locations.put(th, rr);
        }

    }

    private void updateTherapistAvailabilities() {
        ssw.P_availabilities.clear();
        Set<Therapist> t = instance.getTherapists();

        for (Therapist th : t) {
            for (Route r : routes.get(th)) {
                for (Node n : r.getN()) {
                    if (n.getJob() instanceof IdleJob) {
                        ssw.P_availabilities.put(th, n.getTime());
                    }
                }

            }
        }

    }

    private void updateRoomAvailabilities() {
        Set<Node> nodes = N_all;
        ssw.R_availabilities.clear();
        Comparator<Room> roomComp = new Comparator<Room>() {

            @Override
            public int compare(Room o1, Room o2) {
                if (o1.getId() < o2.getId()) {
                    return 1;
                }
                if (o1.getId() == o2.getId()) {
                    return 0;
                }
                return -1;
            }
        };

        // TreeMultimap<Room, Timeslot> vac = TreeMultimap.create(roomComp, Comparators.TIMESLOTS_ASCENDING_BY_START);
        TreeMultimap<Room, Timeslot> occ = TreeMultimap.create(roomComp, Comparators.TIMESLOTS_ASCENDING_BY_START);

        for (Node n : nodes) {
            if (n.isTreatment()) {
                occ.put(n.getRoom(), n.getTime());
                // TODO Kapazit�t!!
            }
        }

        for (Room r_ : instance.getRooms()) {
            Timeslot full = new Timeslot(0, instance.getI_conf().getNumberOfTimeSlots());
            // NavigableSet<Room> sss = occ.keySet();
            if (occ.containsKey(r_)) {

                List<Timeslot> vacant = TimeUtil.subtract(full, new LinkedList<>(occ.get(r_)));
                ssw.R_availabilities.removeAll(r_);
                ssw.R_availabilities.putAll(r_, vacant);
            } else {
                ssw.R_availabilities.put(r_, full);
            }
        }
    }

    public void renewUID() {
        uid = UUID.randomUUID().toString();
    }

    public void refreshWrapper() {
        ssw = SolutionStatusWrapper.createNew();
    }

    public TreeMultimap<Therapist, Route> addRoute(Therapist t, Route p) {
        routes.put(t, p);
        return routes;
    }

    public Integer[] planningBounds() {

        Integer[] bounds = new Integer[]{Integer.MAX_VALUE, Integer.MIN_VALUE};

        for (Therapist t : routes.keySet()) {
            Timeslot ts = getShiftBoundsForTherapist(t);
            if (ts.getStart() < bounds[0]) {
                bounds[0] = ts.getStart();
            }

            if (ts.getEnd() > bounds[1]) {
                bounds[1] = ts.getEnd();
            }

        }

        return bounds;
    }

    public Timeslot getShiftBoundsForTherapist(Therapist t) {
        Integer start = routes.get(t).first().getStartTime();
        Integer end = routes.get(t).last().getEndTime();

        return new Timeslot(start, end);
    }

    @Override
    public String toString() {
        return routes.toString();
    }

    public Double unscheduledJobsAverageLength() {
        Integer sum = 0;
        for (Job j : unscheduledJobs) {
            sum += j.getDurationSlots();
        }

        return new Double(sum) / (double) unscheduledJobs.size();
    }


    @Override
    public Solution clone() {
        if (cloner == null) {
            cloner = new Cloner();
        }

        cloner.setDumpClonedClasses(false);
        cloner.dontCloneInstanceOf(Job.class, Therapist.class, Room.class, Qualification.class, Instance.class, SolverConfiguration.class, SolutionStatusWrapper.class);

        Solution clone = cloner.deepClone(this);

        return clone;
    }

    public Solution cloneIncludingWrapper() {
        if (cloner == null) {
            cloner = new Cloner();
        }

        cloner.setDumpClonedClasses(false);
        cloner.dontCloneInstanceOf(Job.class, Therapist.class, Room.class, Qualification.class, Instance.class, SolverConfiguration.class);

        Solution clone = cloner.deepClone(this);

        return clone;
    }

    public boolean isComplete() {
        return unscheduledJobs.size() == 0;
    }

    public boolean isActive(Therapist t) {
        Set<Route> R = routes.get(t);
        for (Route r : R) {
            for (Node n : r.getN()) {
                if (n.getJob() instanceof TreatmentJob) {
                    return true;
                }
            }
        }
        return false;
    }


    public int getNoOfScheduledJobs() {
        int cnt = 0;

        for (Therapist t : instance.getTherapists()) {
            for (Node n : P_routes.get(t)) {
                if (!(n.getJob() instanceof BreakJob || n.getJob() instanceof IdleJob)) {
                    cnt++;
                }
            }
        }

        return cnt;
    }


    private Double getSolutionCosts() {
        double c = 0.;
        for (Therapist t : instance.getTherapists()) {
            Set<Route> R = routes.get(t);
            for (Route r : R) {
                Double d = getRouteCosts(r);
                c += d;
            }
        }

        return c;
    }

    public double getRouteCosts(Route r) {
        Node last = null;
        double c_t = 0.;
        double c_r = 0.;
        for (Node n : r.getN()) {
            Room s = n.getRoom();
            Job j = n.getJob();

            if (j instanceof WardJob && s instanceof TherapyCenter) {

                WardJob wj = (WardJob) j;
                c_t += instance.getTransportCosts(wj.getRoom(), s);
                c_t += instance.getTransportCosts(wj.getRoom(), s);
            }

            if (j instanceof OutpatientJob) {

                c_t += instance.getTransportCosts(instance.getBreakroom(), s);
                c_t += instance.getTransportCosts(s, instance.getBreakroom());
            }

            if (last != null) {

                Room ro = last.getRoom();
                Double d = instance.getRouteCosts(ro, s);
                c_r += d;
            }
            last = n;
        }
        return c_r + c_t;
    }

    public void removeHistory() {
        predecessor = null;

    }

    public double getFitness() {
        double a = 0.9999;
        double b = 0.0001;

        double active_v = activeVehicles();
        double c_uns = instance.getC_max() * 2 + 1;
        double m = active_v + (getUnscheduledJobs().size() * instance.getTherapists().size() + 1);
        double d = getCosts() + getUnscheduledJobs().size() * c_uns;

        return a * m + b * d;
    }

    public double getCostFitness() {
        double c_uns = instance.getC_max() * 2 + 1;
        return getCosts() + getUnscheduledJobs().size() * c_uns;
    }

    public int getVehicleFitness() {
        int active_v = activeVehicles();
        return active_v + (getUnscheduledJobs().size() * instance.getTherapists().size() + 1);
    }

    public void complete() {
        t_S_compl = System.currentTimeMillis() - t_s;
    }

    public int activeVehicles() {
        int i = 0;
        for (Therapist t : instance.getTherapists()) {
            if (isActive(t)) {
                i++;
            }
        }
        return i;
    }


    public String getGraphicalOutput() {
        return OutputUtil.graphicalOutput(this);
    }

    public void evaluateConsitency() {
        Set<Job> jobs = new HashSet<>(instance.getJobs());
        Set<Job> in_s = new HashSet<>();

        Set<Job> unscheduled = new HashSet<>(unscheduledJobs);

        for (Therapist t : instance.getTherapists()) {
            Set<Route> R = routes.get(t);
            for (Route r : R) {
                Node[] N = r.getN().toArray(new Node[0]);

                for (int i = 0; i < N.length - 1; i++) {

                    Node n = N[i];

                    if (in_s.contains(n.getJob())) {
                        System.err.println("Job " + n.getJob() + " doppelt geplant");
                    }
                    if (n.getJob() instanceof TreatmentJob) {
                        in_s.add(n.getJob());
                    }

                    if (!n.isIdle()) {

                        for (int j = i; j < N.length; j++) {

                            Node m = N[j];

                            if (n.getRoom() != m.getRoom()) {

                                int t_nm = instance.getTravelTime(n.getRoom(), m.getRoom());
                                if (n.getEnd() + t_nm > m.getStart()) {
                                    System.err.println("traveltime: " + t_nm + " but " + n + " ends at " + n.getEnd() + " and " + m + " starts at " + m.getStart());
                                }
                                break;
                            }
                        }
                    }
                }

                if (N[N.length - 1].getJob() instanceof TreatmentJob) {
                    in_s.add(N[N.length - 1].getJob());
                }

            }
        }

        jobs.removeAll(unscheduled);
        jobs.removeAll(in_s);
        if (jobs.size() > 0) {
            System.out.println("Scheduled Nodes in solution: " + in_s.size());
            System.out.println("Unscheduled Nodes in solution: " + unscheduled.size());
            System.out.println("Instance Jobs \\scheduled \\uns: " + jobs.size());
        }
    }

    static class SolutionStatusWrapper implements Serializable {


        private static final long serialVersionUID = 2973784589420087042L;
        private transient TreeMultimap<Therapist, Timeslot> P_availabilities = TreeMultimap.create(Comparators.THERAPIST_ID_ASCENDING, Comparators.TIMESLOTS_ASCENDING_BY_START);
        private transient TreeMap<Therapist, Room[]> P_locations = new TreeMap<>(Comparators.THERAPIST_ID_ASCENDING);
        private transient TreeMultimap<Room, Timeslot> R_availabilities = TreeMultimap.create(Comparators.ROOM_ID_ASCENDING, Comparators.TIMESLOTS_ASCENDING_BY_START);

        @java.beans.ConstructorProperties({"P_availabilities", "P_locations", "R_availabilities"})
        public SolutionStatusWrapper(TreeMultimap<Therapist, Timeslot> P_availabilities, TreeMap<Therapist, Room[]> P_locations, TreeMultimap<Room, Timeslot> R_availabilities) {
            this.P_availabilities = P_availabilities;
            this.P_locations = P_locations;
            this.R_availabilities = R_availabilities;
        }

        public static SolutionStatusWrapper createNew() {
            return new SolutionStatusWrapper(TreeMultimap.create(Comparators.THERAPIST_ID_ASCENDING, Comparators.TIMESLOTS_ASCENDING_BY_START), new TreeMap<Therapist, Room[]>(
                    Comparators.THERAPIST_ID_ASCENDING), TreeMultimap.create(Comparators.ROOM_ID_ASCENDING, Comparators.TIMESLOTS_ASCENDING_BY_START)
            );
        }

    }
}
