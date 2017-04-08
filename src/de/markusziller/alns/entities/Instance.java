package de.markusziller.alns.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Sets;
import de.markusziller.alns.entities.jobs.ICUJob;
import de.markusziller.alns.entities.jobs.JobWithFixedRoom;
import de.markusziller.alns.entities.jobs.OutpatientJob;
import de.markusziller.alns.entities.jobs.WardJob;
import de.markusziller.alns.entities.rooms.ICU;
import de.markusziller.alns.entities.rooms.TherapyCenter;
import de.markusziller.alns.entities.rooms.Ward;
import de.markusziller.alns.exceptions.GeneralInfeasibilityException;
import de.markusziller.alns.exceptions.InstanceConfigurationException;
import de.markusziller.alns.utils.HeuristicUtil;
import de.markusziller.alns.instancegenerator.EntityPool;
import de.markusziller.alns.instancegenerator.InstanceConfiguration;
import de.markusziller.alns.instancegenerator.imports.SolomonInstance;
import de.markusziller.alns.instancegenerator.random.GaussDistribution;
import de.markusziller.alns.instancegenerator.random.IProbabilityDistribution;
import org.apache.commons.math.util.MathUtils;
import org.ejml.simple.SimpleMatrix;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

public class Instance implements Serializable {

    private static final long serialVersionUID = -28317760558644109L;
    private final static Logger log = Logger.getLogger(Instance.class.getName());
    private final boolean isInstanceModifiable = true;
    private final long timestamp = System.currentTimeMillis();
    private transient Long DBPrimaryKey = null;
    private transient String uid = UUID.randomUUID().toString();
    private SolomonInstance solomonInstance;
    private Integer numberOfTimeSlots;
    private double c_max;
    private Set<Job> jobs;
    private Set<Room> rooms;
    private Set<Qualification> qualifications;
    private Room breakroom;
    private Set<Therapist> therapists;
    private InstanceConfiguration i_conf;
    private LinkedHashMultimap<Job, Room> roomsForJob;
    private LinkedHashMultimap<Room, Job> jobsForRoom;
    private LinkedHashMultimap<Therapist, Job> eligibleJobsForTherapist;
    private LinkedHashMultimap<Job, Therapist> proficientTherapistsForJob;
    private List<Ward> wards;
    private List<ICU> icus;
    private List<TherapyCenter> tcs;
    private List<WardJob> wardjobs;
    private List<ICUJob> icujobs;
    private List<OutpatientJob> outjobs;
    private int maxX;
    private int minX;
    private int maxY;
    private int minY;
    private SimpleMatrix distances;
    private SimpleMatrix hospitalLayout;

    public Instance(SolomonInstance si, InstanceConfiguration ic) throws GeneralInfeasibilityException {

        int[] s_meta = si.getMeta();
        int[][] s_raw = si.getRaw();
        int[][] roomsRaw = extractRawRoomData(s_raw);
        int[][] jobsRaw = extractRawJobData(s_raw);
        solomonInstance = si;

        int t_min = jobsRaw[2][0];
        int t_max = jobsRaw[3][0];
        ic.setNumberOfTimeSlots(t_max);

        minX = HeuristicUtil.min(roomsRaw[1]);
        maxX = HeuristicUtil.max(roomsRaw[1]);
        minY = HeuristicUtil.min(roomsRaw[2]);
        maxY = HeuristicUtil.max(roomsRaw[2]);

        i_conf = ic;
        numberOfTimeSlots = ic.getNumberOfTimeSlots();

        breakroom = EntityPool.getBreakroom();

        breakroom.setX(roomsRaw[1][0]);
        breakroom.setY(roomsRaw[2][0]);

        List<Qualification> Q_l = EntityPool.getNQualifications(5);
        List<Therapist> P_l = EntityPool.getNTherapists(s_meta[0]);

        List<Job> I_l = new ArrayList<>();
        List<Room> R_l = new ArrayList<>();

        //R�ume pro Job
        double gamma = si.getGamma();
        double rho = si.getRho();

        for (Therapist t : P_l) {
            t.setShiftStart(0);
            t.setRegularShiftEnd(t_max);
        }

        //Anzahl an R�umen und Jobs wird gesetzt.
        int n_i = s_raw[0].length - 1;
        int n_r = (int) Math.ceil((double) n_i * gamma);


        int n_i_icu = (int) ((double) n_r * (1. - rho));
        int n_r_icu = n_i_icu;
        //verh�ltniss |Ward| /|TC|
        double ward_tc_ratio = ic.getWardRoomsRatio();

        int n_r_ward = (int) Math.ceil((ward_tc_ratio * ((double) (n_r - n_r_icu))) / (ward_tc_ratio + 1));
        int n_i_ward = n_r_ward;

        int n_i_out = n_i - n_i_icu - n_i_ward;
        int n_r_tc = n_r - n_r_icu - n_r_ward;

        int tc_capacity = (int) Math.ceil((double) n_i_out / (double) n_r_tc);

        boolean ICUonly = (n_i_ward + n_i_out == 0);

//		R_l.add(breakroom);


        List<ICU> r_icu = EntityPool.getNICURooms(n_r_icu);
        ICU[] R_icu_arr = r_icu.toArray(new ICU[0]);

        List<Ward> r_ward = EntityPool.getNWardRooms(n_r_ward);
        Ward[] R_ward_arr = r_ward.toArray(new Ward[0]);

        List<TherapyCenter> r_tc = EntityPool.getNTCRooms(n_r_tc);
        TherapyCenter[] R_tc_arr = r_tc.toArray(new TherapyCenter[0]);

        List<ICUJob> I_icu = EntityPool.getNICUJobs(n_i_icu);
        ICUJob[] I_icu_arr = I_icu.toArray(new ICUJob[0]);

        List<WardJob> I_ward = EntityPool.getNWardJobs(n_i_ward);
        WardJob[] I_ward_arr = I_ward.toArray(new WardJob[0]);

        List<OutpatientJob> I_out = EntityPool.getNOutpatientJobs(n_i_out);
        OutpatientJob[] I_out_arr = I_out.toArray(new OutpatientJob[0]);

        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i < n_i + 1; i++) {
            numbers.add(i);
        }
        Random random = new Random();

        ArrayList<Integer> ICU_ids = new ArrayList<>();
        for (int i = 0; i < I_icu.size(); i++) {
            Integer id = numbers.get(random.nextInt(numbers.size()));
            ICU_ids.add(id);
            numbers.remove(id);
        }
        ArrayList<Integer> ward_ids = new ArrayList<>();
        for (int i = 0; i < I_ward.size(); i++) {
            Integer id = numbers.get(random.nextInt(numbers.size()));
            ward_ids.add(id);
            numbers.remove(id);
        }
        ArrayList<Integer> tc_ids = new ArrayList<>();
        for (int i = 0; i < I_out.size(); i++) {
            Integer id = numbers.get(random.nextInt(numbers.size()));
            tc_ids.add(id);
            numbers.remove(id);
        }

        for (int i = 0; i < R_icu_arr.length; i++) {
            Integer id = ICU_ids.get(random.nextInt(ICU_ids.size()));
            ICU_ids.remove(id);
            R_icu_arr[i].setId(id);
            R_icu_arr[i].setName("r_" + id);
            I_icu_arr[i].setId(id);

            R_icu_arr[i].setX(roomsRaw[1][id]);
            R_icu_arr[i].setY(roomsRaw[2][id]);
            R_icu_arr[i].setK(1);
            I_icu_arr[i].addAvailabilty(new Timeslot(jobsRaw[2][id], jobsRaw[3][id] + jobsRaw[4][id]));
            I_icu_arr[i].setDurationSlots(jobsRaw[4][id]);
            I_icu_arr[i].setName("i_" + id);
            I_icu_arr[i].setRoom(R_icu_arr[i]);


        }


        for (int i = 0; i < R_ward_arr.length; i++) {
            Integer id = ward_ids.get(random.nextInt(ward_ids.size()));
            ward_ids.remove(id);
            R_ward_arr[i].setId(id);
            R_ward_arr[i].setName("r_" + id);
            I_ward_arr[i].setId(id);

            R_ward_arr[i].setX(roomsRaw[1][id]);
            R_ward_arr[i].setY(roomsRaw[2][id]);
            R_ward_arr[i].setK(i);
            I_ward_arr[i].addAvailabilty(new Timeslot(jobsRaw[2][id], jobsRaw[3][id] + jobsRaw[4][id]));
            I_ward_arr[i].setDurationSlots(jobsRaw[4][id]);

            I_ward_arr[i].setRoom(R_ward_arr[i]);
            I_ward_arr[i].setName("i_" + id);

        }

        for (int i = 0; i < R_tc_arr.length; i++) {
            Integer id = tc_ids.get(random.nextInt(tc_ids.size()));
            tc_ids.remove(id);
            R_tc_arr[i].setId(id);
            R_tc_arr[i].setK(tc_capacity);
            R_tc_arr[i].setX(roomsRaw[1][id]);
            R_tc_arr[i].setY(roomsRaw[2][id]);
            R_tc_arr[i].setName("r_" + id);
            I_out_arr[i].setId(id);
            I_out_arr[i].addAvailabilty(new Timeslot(jobsRaw[2][id], jobsRaw[3][id] + jobsRaw[4][id]));
            I_out_arr[i].setDurationSlots(jobsRaw[4][id]);
            I_out_arr[i].setName("i_" + id);

        }

        for (int i = R_tc_arr.length; i < I_out_arr.length; i++) {
            Integer id = tc_ids.get(random.nextInt(tc_ids.size()));
            tc_ids.remove(id);
            I_out_arr[i].setId(id);
            I_out_arr[i].setName("i_" + id);
            I_out_arr[i].addAvailabilty(new Timeslot(jobsRaw[2][id], jobsRaw[3][id] + jobsRaw[4][id]));
            I_out_arr[i].setDurationSlots(jobsRaw[4][id]);
        }


        R_l.add(breakroom);
        R_l.addAll(r_icu);
        R_l.addAll(r_ward);
        R_l.addAll(r_tc);
        I_l.addAll(I_icu);
        I_l.addAll(I_out);
        I_l.addAll(I_ward);

        Collections.sort(R_l, new Comparator<Room>() {

            @Override
            public int compare(Room r, Room r2) {
                if (r.getId() < r2.getId()) {
                    return -1;
                }
                return 1;
            }
        });

        Collections.sort(I_l, new Comparator<Job>() {

            @Override
            public int compare(Job r, Job r2) {
                if (r.getId() < r2.getId()) {
                    return -1;
                }
                return 1;
            }
        });

        for (Job i : I_l) {
            if (i instanceof JobWithFixedRoom) {
//				System.out.println(i+" in "+((JobWithFixedRoom) i).getRoom());
            } else {
//				System.out.println(i);
            }

        }


        icujobs = I_icu;
        wardjobs = I_ward;
        outjobs = I_out;
        wards = r_ward;
        icus = r_icu;
        tcs = r_tc;

        therapists = new HashSet<>(P_l);
        rooms = new HashSet<>(R_l);
        qualifications = new HashSet<>(Q_l);
        jobs = new HashSet<>(I_l);


        if (ic.isUseQ()) {
            distributeQualificationsToTherapists(ic.getQualificationToTherapistAssignment());
            distributeQualificationsToJobs(ic.getQualificationToJobAssignment());
        }

        generateRoomDistancesAndHospitalLayout(R_l, ic, roomsRaw);
        linkRoomsAndJobsForImportedInstances();
        linkJobsAndTherapistsByQualification();


    }

    public Instance(InstanceConfiguration ic) throws InterruptedException, GeneralInfeasibilityException {

        try {
            ic.validateConfiguration();
        } catch (InstanceConfigurationException e) {

            log.severe("Instance parameters are inconsistent. Problem: " + e.getMessage() + "\n\n Execution is continued, but invalid results are to be expected");

            System.err.print("\n");

            for (int i = 0; i < 80; i++) {
                Thread.sleep(100);
                System.err.print("!");
            }
            System.err.print("\n");

        }

        numberOfTimeSlots = ic.getNumberOfTimeSlots();
        i_conf = ic;

        List<Therapist> lll = EntityPool.getNTherapists(ic.getTherapistsQuantity());
        List<Qualification> qqq = EntityPool.getNQualifications(ic.getQualificationsQuantity());
        List<Room> rrr = EntityPool.getNAllRooms(ic.getTotalRoomsQuantity(), ic.getWardRoomsRatio(), ic.getICURoomsRatio(), ic.getTCRoomsRatio());
        breakroom = EntityPool.getBreakroom();
        List<Job> jjj = EntityPool.getNAllJobs(ic.getTotalJobsQuantity(), ic.getWardJobsRatio(), ic.getICUJobsRatio(), ic.getOutpatientJobsRatio());

        therapists = new HashSet<>(lll);
        rooms = new HashSet<>(rrr);

        qualifications = new HashSet<>(qqq);

        jobs = new HashSet<>(jjj);

        assignPossibleJobTimes(jjj);
        assignDurations(jjj);


        distributeQualificationsToTherapists(ic.getQualificationToTherapistAssignment());
        distributeQualificationsToJobs(ic.getQualificationToJobAssignment());


        // List<Room> all = new LinkedList<>(rrr);
        // all.add(breakroom);
        generateRoomDistancesAndHospitalLayout(rrr, ic);
        linkRoomsAndJobs();
        linkJobsAndTherapistsByQualification();

        // therapistsQSorted = Utils.sort(therapists, Comparators.QUALIFICATIONS_ASCENDING);

    }

    public String toString() {
        return "de.tum.ziller.thesis.thrp.common.entities.Instance(isInstanceModifiable=" + this.isInstanceModifiable + ", DBPrimaryKey=" + this.DBPrimaryKey + ", uid=" + this.uid + ", solomonInstance=" + this.solomonInstance + ", numberOfTimeSlots=" + this.numberOfTimeSlots + ", timestamp=" + this.timestamp + ", c_max=" + this.c_max + ", jobs=" + this.jobs + ", rooms=" + this.rooms + ", qualifications=" + this.qualifications + ", breakroom=" + this.breakroom + ", therapists=" + this.therapists + ", i_conf=" + this.i_conf + ", roomsForJob=" + this.roomsForJob + ", jobsForRoom=" + this.jobsForRoom + ", eligibleJobsForTherapist=" + this.eligibleJobsForTherapist + ", proficientTherapistsForJob=" + this.proficientTherapistsForJob + ", wards=" + this.wards + ", icus=" + this.icus + ", tcs=" + this.tcs + ", wardjobs=" + this.wardjobs + ", icujobs=" + this.icujobs + ", outjobs=" + this.outjobs + ", maxX=" + this.maxX + ", minX=" + this.minX + ", maxY=" + this.maxY + ", minY=" + this.minY + ", distances=" + this.distances + ", hospitalLayout=" + this.hospitalLayout + ")";
    }

    public Long getDBPrimaryKey() {
        return this.DBPrimaryKey;
    }

    public void setDBPrimaryKey(Long DBPrimaryKey) {
        this.DBPrimaryKey = DBPrimaryKey;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public SolomonInstance getSolomonInstance() {
        return this.solomonInstance;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public double getC_max() {
        return this.c_max;
    }

    public Set<Job> getJobs() {
        return this.jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Set<Room> getRooms() {
        return this.rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public Set<Qualification> getQualifications() {
        return this.qualifications;
    }

    public void setQualifications(Set<Qualification> qualifications) {
        this.qualifications = qualifications;
    }

    public Room getBreakroom() {
        return this.breakroom;
    }

    public void setBreakroom(Room breakroom) {
        this.breakroom = breakroom;
    }

    public Set<Therapist> getTherapists() {
        return this.therapists;
    }

    public void setTherapists(Set<Therapist> therapists) {
        this.therapists = therapists;
    }

    public InstanceConfiguration getI_conf() {
        return this.i_conf;
    }

    public void setI_conf(InstanceConfiguration i_conf) {
        this.i_conf = i_conf;
    }

    public List<Ward> getWards() {
        return this.wards;
    }

    public List<ICU> getIcus() {
        return this.icus;
    }

    public List<TherapyCenter> getTcs() {
        return this.tcs;
    }

    public List<WardJob> getWardjobs() {
        return this.wardjobs;
    }

    public List<ICUJob> getIcujobs() {
        return this.icujobs;
    }

    public List<OutpatientJob> getOutjobs() {
        return this.outjobs;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMaxY() {
        return this.maxY;
    }

    public int getMinY() {
        return this.minY;
    }

    private int[][] extractRawJobData(int[][] raw) {

        int[][] arr = new int[5][raw[0].length];

        arr[0] = raw[0];
        arr[1] = raw[3];
        arr[2] = raw[4];
        arr[3] = raw[5];
        arr[4] = raw[6];

        return arr;
    }

    private int[][] extractRawRoomData(int[][] raw) {
        int[][] arr = new int[3][raw[0].length];

        arr[0] = raw[0];
        arr[1] = raw[1];
        arr[2] = raw[2];

        return arr;
    }

    private int max(int[] arr) {
        int max = Integer.MIN_VALUE;
        for (int anArr : arr) {
            if (anArr > max) {
                max = anArr;
            }
        }
        return max;
    }

    private void generateRoomDistancesAndHospitalLayout(List<Room> rrr, InstanceConfiguration ic, int[][] roomsData) {

        int[] x_coords = roomsData[1];
        int[] y_coords = roomsData[2];

        // Integer d = new Double(Math.ceil(Math.sqrt(rrr.size() * 1)) * 1.5).intValue();
        hospitalLayout = new SimpleMatrix(max(y_coords) + 1, max(x_coords) + 1);
        SimpleMatrix sm = new SimpleMatrix(y_coords.length, y_coords.length);
        Map<Integer, int[]> tempPosMap = new TreeMap<>();

        for (int i = 0; i < x_coords.length; i++) {
            hospitalLayout.set(y_coords[i], x_coords[i], roomsData[0][i]);
            tempPosMap.put(roomsData[0][i], new int[]{y_coords[i], x_coords[i]});
        }

        Double costPerCell = ic.getCostPerCell();

        for (int i = 0; i < roomsData[0].length; i++) {
            int r1 = roomsData[0][i];
            for (int j = 0; j < roomsData[0][i]; j++) {
                int r2 = roomsData[0][j];

                if (r1 == r2) {
                    sm.set(r2, r1, 0.0);
                } else {

                    Integer room1y = tempPosMap.get(r1)[0];
                    Integer room1x = tempPosMap.get(r1)[1];

                    Integer room2y = tempPosMap.get(r2)[0];
                    Integer room2x = tempPosMap.get(r2)[1];

                    //Rechtwinkliger abstand
                    Double distance = (Math.abs(room1y.doubleValue() - room2y.doubleValue()) + Math.abs(room1x.doubleValue() - room2x.doubleValue())) * costPerCell;


                    //Euklidischer abstand
                    Double d = MathUtils.distance(new int[]{room1x, room1y}, new int[]{room2x, room2y}) * costPerCell;

                    sm.set(r2, r1, d);
                }
            }
        }
        distances = sm;
        c_max = Math.max(getMaxRoutingCosts(), getMaxTransportCosts());

    }


    private void generateRoomDistancesAndHospitalLayout(List<Room> rrr, InstanceConfiguration ic) {
        SimpleMatrix sm = new SimpleMatrix(rrr.size(), rrr.size());
        Collections.sort(rrr, new Comparator<Room>() {

            @Override
            public int compare(Room r1, Room r2) {
                if (r1.getId() < r2.getId()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        // Randomize room locations
        Integer d = new Double(Math.ceil(Math.sqrt(rrr.size() * 1)) * 1.5).intValue();
        hospitalLayout = new SimpleMatrix(d, d);

        List<Room> tempRoom = new ArrayList<>(rrr);

        Map<Integer, Integer[]> tempPosMap = new HashMap<>();

        while (!tempRoom.isEmpty()) {
            Random randomGenerator = new Random();
            Integer y_pos = randomGenerator.nextInt(d);
            Integer x_pos = randomGenerator.nextInt(d);

            while (hospitalLayout.get(y_pos, x_pos) > 0) {
                y_pos = randomGenerator.nextInt(d);
                x_pos = randomGenerator.nextInt(d);
            }

            Room r = tempRoom.get(0);
            hospitalLayout.set(y_pos, x_pos, r.getId());
            tempPosMap.put(r.getId(), new Integer[]{y_pos, x_pos});
            tempRoom.remove(0);

        }

        Double costPerCell = ic.getCostPerCell();

        for (Room r1 : rrr) {
            if (r1.getId() == 0) {
                // System.out.println();
            }
            for (int j = 0; j < r1.getId(); j++) {
                Room r2 = rrr.get(j);

                if (r1 == r2) {
                    sm.set(r2.getId(), r1.getId(), 0.0);
                } else {

                    Integer room1y = tempPosMap.get(r1.getId())[0];
                    Integer room1x = tempPosMap.get(r1.getId())[1];

                    Integer room2y = tempPosMap.get(r2.getId())[0];
                    Integer room2x = tempPosMap.get(r2.getId())[1];

                    //Rechtwinkliger abstand
                    Double distance = (Math.abs(room1y.doubleValue() - room2y.doubleValue()) + Math.abs(room1x.doubleValue() - room2x.doubleValue())) * costPerCell;

                    //Euklidischer abstand
                    Double di = MathUtils.distance(new int[]{room1x, room1y}, new int[]{room2x, room2y}) * costPerCell;
                    sm.set(r2.getId(), r1.getId(), di);
                }
            }
        }
        distances = sm;
        c_max = Math.max(getMaxRoutingCosts(), getMaxTransportCosts());
    }

    private double getMaxRoutingCosts() {
        double max = Double.NEGATIVE_INFINITY;
        double d = max;
        for (Room r : rooms) {
            for (Room l : rooms) {
                if ((d = getDistance(r, l)) > max) {
                    max = d;
                }
            }
        }

        return max * i_conf.getRoutingCostFactor();
    }

    private double getMaxTransportCosts() {
        double max = Double.NEGATIVE_INFINITY;
        double d = max;
        for (Room r : rooms) {
            for (Room l : rooms) {
                if ((d = getDistance(r, l)) > max) {
                    max = d;
                }
            }
        }

        return max * i_conf.getPatientTransportCostFactor();
    }

    private void linkJobsAndTherapistsByQualification() throws GeneralInfeasibilityException {

        eligibleJobsForTherapist = LinkedHashMultimap.create();
        proficientTherapistsForJob = LinkedHashMultimap.create();

        for (Job j : Preconditions.checkNotNull(jobs)) {
            for (Therapist t : Preconditions.checkNotNull(therapists)) {


                if (t.getQHash() == j.getQHash()) {
                    eligibleJobsForTherapist.put(t, j);
                    proficientTherapistsForJob.put(j, t);
                }
                if (t.getQHash() > j.getQHash()) {
                    if (t.getBinary().endsWith(j.getBinary())) {
                        eligibleJobsForTherapist.put(t, j);
                        proficientTherapistsForJob.put(j, t);
                    }
                }
            }

            if (proficientTherapistsForJob.get(j).isEmpty()) {

                if (isInstanceModifiable) {
                    log.warning("No therapist is proficient for Job " + j.getId() + ". Removing all qualifications of Job " + j + " to avoid infeasibility");

                    j.setQHash(0);
                    proficientTherapistsForJob.putAll(j, therapists);

                    for (Therapist t2 : therapists) {
                        eligibleJobsForTherapist.put(t2, j);
                    }

                } else {
                    throw new GeneralInfeasibilityException("No therapist is proficient to perform Job " + j.getId() + " and altering the instance is disallowed. The instance is infeasible", uid);
                }

            }
        }

    }


    private void linkRoomsAndJobs() {

        Double avgWard = i_conf.getAvgJobsPerWard();
        Double avgICU = i_conf.getAvgJobsPerICU();
        Double avg = null;
        roomsForJob = LinkedHashMultimap.create();
        jobsForRoom = LinkedHashMultimap.create();

        // Collections.shuffle(null);

        List<Job> tempJob = new LinkedList<>(Preconditions.checkNotNull(jobs));
        List<Room> tempRoom = new LinkedList<>(Preconditions.checkNotNull(rooms));

        for (Job j : jobs) {
            for (Room r : rooms) {
                // passt Raum theoretisch?
                if (isRoomFittingForJob(r, j)) {

                    // Ein WardJob wurde gefunden
                    if (HeuristicUtil.isWardJob(j) || HeuristicUtil.isICUJob(j)) {

                        JobWithFixedRoom job = (JobWithFixedRoom) j;

                        avg = HeuristicUtil.isWardJob(j) ? avgWard : avgICU;

                        if (r.getClass() == TherapyCenter.class && HeuristicUtil.isWardJob(j)) {
                            roomsForJob.put(j, r);
                            jobsForRoom.put(r, j);
                        } else {
                            if (job.getRoom() == null) {

                                // zwischen ab und aufgerundetem Wert
                                if (jobsForRoom.get(r).size() < Math.floor(avg)) {
                                    roomsForJob.put(j, r);
                                    jobsForRoom.put(r, j);
                                    job.setRoom(r);

                                } else {
                                    Double rd = Math.random();
                                    // zuf�llig verteilen, besierend auf nachkommawert
                                    if (jobsForRoom.get(r).size() + 1 <= Math.ceil(avg) && rd < avg - Math.floor(avg)) {

                                        roomsForJob.put(j, r);
                                        jobsForRoom.put(r, j);
                                        job.setRoom(r);
                                    } else {
                                        // Raum wurde �berbelegt und braucht in sp�teren zuordnungen nicht mehr be�rcksichtigt werden
                                        if (!(r.getClass() == TherapyCenter.class)) {
                                            tempRoom.remove(r);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (j.getClass() == OutpatientJob.class) {
                        roomsForJob.put(j, r);
                        jobsForRoom.put(r, j);
                    }
                }
            }
        }

        for (Job j : jobs) {
            if (HeuristicUtil.isWardJob(j) || HeuristicUtil.isICUJob(j)) {
                JobWithFixedRoom temp = (JobWithFixedRoom) j;

                if (temp.getRoom() == null) {
                    log.warning("Forcing a room for job " + j.getName() + " because stochastic distribution lead to an infeasibility");
                    for (Room r : rooms) {
                        if (r.getClass() == Ward.class && HeuristicUtil.isWardJob(j) && jobsForRoom.get(r).size() < Math.ceil(avgWard)) {
                            roomsForJob.put(j, r);
                            jobsForRoom.put(r, j);
                            temp.setRoom(r);
                        }

                        if (HeuristicUtil.isICU(r) && HeuristicUtil.isICUJob(j) && jobsForRoom.get(r).size() < Math.ceil(avgICU)) {
                            roomsForJob.put(j, r);
                            jobsForRoom.put(r, j);
                            temp.setRoom(r);
                        }

                    }
                }
            }
        }

        for (Job j : tempJob) {
            for (Room r : tempRoom) {
                if (isRoomFittingForJob(r, j)) {
                    if (r.getClass() != Ward.class && r.getClass() != ICU.class) {
                        roomsForJob.put(j, r);
                        jobsForRoom.put(r, j);
                    }
                }
            }
        }
    }

    private void linkRoomsAndJobsForImportedInstances() {
        roomsForJob = LinkedHashMultimap.create();
        jobsForRoom = LinkedHashMultimap.create();

        for (Job j : jobs) {

            if (j instanceof JobWithFixedRoom) {
                roomsForJob.put(j, ((JobWithFixedRoom) j).getRoom());
                jobsForRoom.put(((JobWithFixedRoom) j).getRoom(), j);
            }

            for (Room r : rooms) {
                if ((j instanceof WardJob || j instanceof OutpatientJob) && r instanceof TherapyCenter) {
                    roomsForJob.put(j, r);
                    jobsForRoom.put(r, j);
                }
            }
        }

    }

    private boolean isRoomFittingForJob(Room r, Job j) {
        return i_conf.getRoomToJobAssignment().get(j.getClass()).contains(r.getClass());
    }

    private void distributeQualificationsToJobs(IProbabilityDistribution iq) {
        for (Job j : jobs) {
            List<Qualification> toAdd = iq.getSubset(new LinkedList<>(qualifications));
            j.addQualification(toAdd);
        }
    }

    private void distributeQualificationsToTherapists(IProbabilityDistribution iq) {
        for (Therapist t : therapists) {
            List<Qualification> toAdd = iq.getSubset(new LinkedList<>(qualifications));
            t.addQualification(toAdd);
        }

    }

    private <T extends Job> void assignPossibleJobTimes(List<T> jobs) {
        for (Job job : jobs) {
            job.addAvailabilty(new Timeslot(0, i_conf.getIndexOfLastTimeSlot()));
        }
    }

    private <T extends Job> void assignDurations(List<T> jobs) {

        Double mean_ICU = i_conf.getJobAvgLengths_TS()[0].doubleValue();
        Double mean_Ward = i_conf.getJobAvgLengths_TS()[1].doubleValue();
        Double mean_Outp = i_conf.getJobAvgLengths_TS()[2].doubleValue();

        Double dev_ICU = Math.max(i_conf.getJobMaxLengths_TS()[0].doubleValue() - mean_ICU, mean_ICU - i_conf.getJobMinLengths_TS()[0].doubleValue());
        Double dev_Ward = Math.max(i_conf.getJobMaxLengths_TS()[1].doubleValue() - mean_Ward, mean_Ward - i_conf.getJobMinLengths_TS()[1].doubleValue());
        Double dev_Outp = Math.max(i_conf.getJobMaxLengths_TS()[2].doubleValue() - mean_Outp, mean_Outp - i_conf.getJobMinLengths_TS()[2].doubleValue());

        IProbabilityDistribution distr_ICU = new GaussDistribution(mean_ICU, dev_ICU);
        IProbabilityDistribution distr_Ward = new GaussDistribution(mean_Ward, dev_Ward);
        IProbabilityDistribution distr_Outp = new GaussDistribution(mean_Outp, dev_Outp);

        for (Job job : jobs) {
            if (HeuristicUtil.isICUJob(job)) {
                job.setDurationSlots(distr_ICU.getQuantity(i_conf.getJobMinLengths_TS()[0], i_conf.getJobMaxLengths_TS()[0]));
            }
            if (HeuristicUtil.isWardJob(job)) {
                job.setDurationSlots(distr_Ward.getQuantity(i_conf.getJobMinLengths_TS()[1], i_conf.getJobMaxLengths_TS()[1]));
            }
            if (job.getClass() == OutpatientJob.class) {
                job.setDurationSlots(distr_Outp.getQuantity(i_conf.getJobMinLengths_TS()[2], i_conf.getJobMaxLengths_TS()[2]));
            }
        }
    }

    public Double getRouteCosts(Integer roomId1, Integer roomId2) {
        return distances.get(Math.min(roomId2, roomId1), Math.max(roomId2, roomId1)) * i_conf.getRoutingCostFactor();
    }

    private Double getRouteCosts(int... ids) {
        if (ids.length == 1) {
            return 0.;
        }
        if (ids.length == 2) {
            return distances.get(Math.min(ids[0], ids[1]), Math.max(ids[0], ids[1])) * i_conf.getRoutingCostFactor();
        }

        Double s = distances.get(Math.min(ids[0], ids[1]), Math.max(ids[0], ids[1])) * i_conf.getRoutingCostFactor();
        int[] temp = new int[ids.length - 1];
        System.arraycopy(ids, 1, temp, 0, ids.length - 1);

        return s + getRouteCosts(temp);
    }

    public Double getRouteCosts(Room r1, Room r2) {
        return distances.get(Math.min(r2.getId(), r1.getId()), Math.max(r2.getId(), r1.getId())) * i_conf.getRoutingCostFactor();
    }

    public Double getTransportCosts(Integer roomId1, Integer roomId2) {
        return distances.get(Math.min(roomId2, roomId1), Math.max(roomId2, roomId1)) * i_conf.getPatientTransportCostFactor();
    }

    public Double getTransportCosts(Room r1, Room r2) {
        return distances.get(Math.min(r2.getId(), r1.getId()), Math.max(r2.getId(), r1.getId())) * i_conf.getPatientTransportCostFactor();
    }

    private Double getDistance(Integer roomId1, Integer roomId2) {
        return distances.get(Math.min(roomId2, roomId1), Math.max(roomId2, roomId1));
    }

    private Double getDistance(Room room1, Room room2) {
        return getDistance(room1.getId(), room2.getId());
    }

    public int getTravelTime(Room r, Room r2) {
        return (int) Math.ceil(getRouteCosts(r, r2));
    }

    private Set<Job> getEligibleJobs(Therapist t) {
        return eligibleJobsForTherapist.get(t);
    }

    public Set<Job> getEligibleJobs(Therapist t, Room r) {
        return Sets.intersection(getEligibleJobs(t), getEligibleJobs(r));
    }

    public Set<Therapist> getProficientTherapists(Job j) {
        return proficientTherapistsForJob.get(j);
    }

    public Set<Room> getEligibleRooms(Job j) {
        return roomsForJob.get(j);
    }

    private Set<Job> getEligibleJobs(Room r) {
        return jobsForRoom.get(r);
    }

    class SolomonMeta {
        private final String name;
        private final double lamba;
        private final double rho;

        @java.beans.ConstructorProperties({"name", "lamba", "rho"})
        public SolomonMeta(String name, double lamba, double rho) {
            this.name = name;
            this.lamba = lamba;
            this.rho = rho;
        }
    }

}
