package de.markusziller.alns.heuristic.strategies.alns.insertion;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import de.markusziller.alns.utils.Comparators;
import de.markusziller.alns.entities.*;
import de.markusziller.alns.exceptions.GeneralInfeasibilityException;
import de.markusziller.alns.exceptions.JobInfeasibilityException;
import de.markusziller.alns.exceptions.RouteConstructionException;
import de.markusziller.alns.utils.HeuristicUtil;
import de.markusziller.alns.utils.TimeUtil;
import de.markusziller.alns.utils.XMLUtil;
import de.markusziller.alns.heuristic.concurrent.InsertionEvaluator;
import de.markusziller.alns.heuristic.concurrent.InsertionManager;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ForecastRepair extends ALNSAbstractRepair implements IALNSRepair {
    private final Integer minElements = 100;
    // alternativ: Runtime.getRuntime().availableProcessors())
    private final Integer cores = XMLUtil.getMaxNumberOfCPUCores();
    // Balance zwischen Oberhead und Performance finden
    private final Float modifier = 0.01F;
    private final boolean multithreaded;
    private final Double roomWeight = 0.2;
    private final Double therapistWeight = 0.2;
    private final Double jobLengthWeight = 0.6;


    public ForecastRepair(boolean multith) {
        multithreaded = multith;
    }

    @Override
    public Solution repair(Solution s) {
        while (!s.isComplete()) {
            try {
                s = planNextJob(s, new LinkedList<Insertion>());
            } catch (JobInfeasibilityException | GeneralInfeasibilityException | RouteConstructionException e) {
                break;
            }
        }
        return s;
    }

    private Solution planNextJob(Solution s, List<Insertion> tabus) throws JobInfeasibilityException, GeneralInfeasibilityException, RouteConstructionException {
        setTabus(tabus);
        Job j = nextJob(s);
        LinkedListMultimap<Therapist, Node> a_i = calculatePossibleJobInsertions(j, s);
        if (a_i.size() < 1) {
            throw new JobInfeasibilityException("Job " + j.getId() + " can not be inserted valid", s.getUid(), j);
        }
        // Start Parallesierte Berechnung
        InsertionManager cb = new InsertionManager();
        Insertion a_best = null;
        List<Insertion> tempList = new LinkedList<>();
        // Listenelemente pro kern
        Float elemPerCore = (float) (a_i.size() / cores);
        // Anzahl an Listenelementen pro Thread
        Float elemPerThread = Math.min(Math.max(elemPerCore * modifier, minElements), a_i.size());
        // ThreadPool
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(cores));
        for (Therapist t : a_i.keySet()) {
            for (Node pn : a_i.get(t)) {
                if (multithreaded) {
                    if (tempList.size() >= elemPerThread) {
                        ListenableFuture<Insertion> pnEval = service.submit(new InsertionEvaluator(Collections.unmodifiableList(new LinkedList<>(tempList)), s));
                        Futures.addCallback(pnEval, cb);
                        tempList.clear();
                    } else {
                        tempList.add(new Insertion(pn, t));
                    }
                } else {
                    Insertion pin = new Insertion(pn, t);
                    InsertionEvaluator ie = new InsertionEvaluator(s);
                    Double costs = ie.getInsertionCosts(pin);
                    pin.setCosts(costs);
                    if (a_best == null || costs < a_best.getCosts()) {
                        a_best = pin;
                    }
                }
            }
        }
        if (!tempList.isEmpty()) {
            ListenableFuture<Insertion> pnEval = service.submit(new InsertionEvaluator(Collections.unmodifiableList(new LinkedList<>(tempList)), s));
            Futures.addCallback(pnEval, cb);
            tempList.clear();
        }
        service.shutdown();
        try {
            service.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Insertion i = multithreaded ? cb.getBestVal() : a_best;
        s.apply(i);
        s.getUnscheduledJobs().remove(j);
        asvm.onJobPlanned(this, i, s);
        return s;
    }


    private LinkedListMultimap<Therapist, Node> calculatePossibleJobInsertions(Job j, Solution s) {
        // geeignete Therapeuten und R�ume
        Collection<Therapist> th = s.getInstance().getProficientTherapists(j);
        Collection<Room> rooms = s.getInstance().getEligibleRooms(j);
        TreeMultimap<Room, Timeslot> T = s.getVacanciesForAllRooms();
        LinkedListMultimap<Therapist, Node> map = LinkedListMultimap.create();
        Random ran = new Random();
        // Checken wann Job ausgef�hrt werden kann
        List<Timeslot> jobAvails = j.getAvailabilty();
        // Alle geeigneten Therapeut/Raum-Kombinationen
        List<Therapist> blocked = new LinkedList<>();
        List<Therapist> nonBlocked = new LinkedList<>();
        for (Therapist t : th) {
            if (containsTabu(j, t)) {
                blocked.add(t);
            } else {
                nonBlocked.add(t);
            }
        }
        for (Therapist t : nonBlocked) {
            // freie Zeiten des Therapeuten
            List<Timeslot> therapistIdles = s.getIdleTimeForTherapist(t);
            for (Room r : rooms) {
                // Wann ist der Raum frei
                List<Timeslot> l = new LinkedList<>(T.get(r));
                // Schnittmenge aus Therapeuten, Job und Raumverf�gbarkeit
                List<Timeslot> RTJavailablities = TimeUtil.getIntersection(jobAvails, therapistIdles, l);
                for (Timeslot ts : RTJavailablities) {
                    // Slot nur relevant wenn Job reinpasst
                    if (ts.getLength() >= j.getDurationSlots()) {
                        int i_min = ts.getStart();
                        int i_max = ts.getEnd() - j.getDurationSlots() + 1;
                        int i = ran.nextInt(i_max - i_min + 1) + i_min;
                        Integer end = TimeUtil.getEnd(i, j.getDurationSlots());
                        map.put(t, new Node("temp", r, j, new Timeslot(i, end)));
                    }
                }
            }
        }
        return map;
    }


    private Job nextJob(Solution s) {
        Integer thpFactor = 0;
        Integer rmFactor = 0;
        Double jobLengthFactor = 0.;
        Double avgJobLength = s.unscheduledJobsAverageLength();
        for (Job j : s.getUnscheduledJobs()) {
            thpFactor = s.getInstance().getProficientTherapists(j).size();
            rmFactor = s.getInstance().getEligibleRooms(j).size();
            // Gewichtung der Jobl�nge
            // (1 + (-1 + x)^3)/(1 + (-1 + a)^3) Normierter Exponentieller Term f(x), x = job length, a = der wert f�r den f(a) = 1
            Double dev_from_avg = j.getDurationSlots() / avgJobLength;
            Double threshold = 2.;
            jobLengthFactor = Math.pow((1 + (-1 + dev_from_avg)), 3.) / Math.pow((1 + (-1 + threshold)), 3.);
            // Logarithmus zur Basis des escapeThreshold (et). Zwischen 0 und 1 im Intervall [et, inf.[
            // F�r Werte < et w�chst dieser schnell an im Jobs mit nur sehr wenig verf�gbaren Ressourcen fr�h zu planen
            Double escapeThreshold = 2.0;
            Double therapistFactor = Math.log(escapeThreshold) / Math.log(thpFactor);
            Double roomFactor = Math.log(escapeThreshold) / Math.log(rmFactor);
            Double totalWeight = (therapistFactor * therapistWeight) + (roomFactor * roomWeight) + (jobLengthFactor * jobLengthWeight);
            j.setSchedulingPriority(totalWeight);
        }
        List<Job> temp = HeuristicUtil.sort(s.getUnscheduledJobs(), Comparators.JOB_PRIORITY_SCORE_DESCENDING);
        return temp.get(0);
    }
}
