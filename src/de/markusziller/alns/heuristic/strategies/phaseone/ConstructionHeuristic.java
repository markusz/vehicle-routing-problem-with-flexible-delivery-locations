package de.markusziller.alns.heuristic.strategies.phaseone;

import de.markusziller.alns.entities.*;
import de.markusziller.alns.entities.jobs.BreakJob;
import de.markusziller.alns.entities.jobs.IdleJob;
import de.markusziller.alns.exceptions.GeneralInfeasibilityException;
import de.markusziller.alns.heuristic.SolverConfiguration;
import de.markusziller.alns.heuristic.strategies.ConstructionStrategy;
import de.markusziller.alns.heuristic.strategies.alns.insertion.NRegretRepair;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ConstructionHeuristic implements ConstructionStrategy {
    private static final Logger log = Logger.getLogger(ConstructionHeuristic.class.getName());
    private final Integer it = 0;
    private final Integer sb = 0;
    //	private ForecastRepair	fr			= new ForecastRepair(true);
//	private GreedyRepair	gr			= new GreedyRepair();
//	private RegretRepair	rr			= new RegretRepair();
    private final NRegretRepair nr = new NRegretRepair(2);
    int transfers = 0;
    private Long runTime;
    private Solution s_c;
    private Solution s_t;
    private Instance is;

    @Override
    public Solution getInitialSolution(Instance i) throws GeneralInfeasibilityException {
        return getInitialSolution(i, new SolverConfiguration(), true);
    }

    @Override
    public Solution getInitialSolution(Instance i, SolverConfiguration sc) throws GeneralInfeasibilityException {
        return getInitialSolution(i, sc, true);
    }

    @Override
    public Solution getInitialSolution(Instance i, SolverConfiguration sc, boolean multithreaded) {
        log.info("Starting to solve Instance with " + i.getJobs().size() + " Jobs," + i.getRooms().size() + " Rooms," + i.getTherapists().size() +
                " Therapists");
        is = i;
        runTime = System.currentTimeMillis();
        // Minmale als Wurzelknoten setzen
        s_c = getEmptySolution(is, sc);
        s_c.setPredecessor(null);
        // Iterationen starten hier
        while (!s_c.isComplete()) {
            if (System.currentTimeMillis() - runTime > sc.getTimelimit()) {
                printTimeout(runTime, it, sb);
                return s_c;
            }
            // Neue L�sungsinstanz erzeugen
            s_t = s_c.clone();
            s_t = nr.repair(s_t);
            s_c = s_t;
            s_c.setT(System.currentTimeMillis() - s_c.getT_s());
        }
        // Konsolenausgabe das L�sung erfolgreich berechnet wurde
        printSuccess(runTime, it, sb);
        return s_c;
    }

    private Solution getEmptySolution(Instance is, SolverConfiguration sc) {
        Solution s_0 = new Solution(is, sc);
        for (Therapist tp : is.getTherapists()) {
            Integer t_e = is.getI_conf().getNumberOfTimeSlots();
            Node m = new Node("Start", is.getBreakroom(), new BreakJob(), new Timeslot(tp.getShiftStart(), tp.getShiftStart()));
            Node n = new Node("Start", is.getBreakroom(), new IdleJob(), new Timeslot(tp.getShiftStart() + 1, t_e - 1));
            Node o = new Node("Start", is.getBreakroom(), new BreakJob(), new Timeslot(t_e, t_e));
            Route pw = new Route(m, n, o);
            s_0.addRoute(tp, pw);
        }
        s_0.update();
        return s_0;
    }

    private void printSuccess(Long runTime, Integer it, Integer sb) {
        System.err.println("-----------------------------------------------------------------------------------------------------------------");
        System.err.println("Computation of Initial Solution took " + TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - runTime) + " ms.");
        System.err.println(it + " Iterations were required to schedule " + is.getJobs().size());
        System.err.println(sb + " Stepbacks were required.");
        System.err.println("TOTAL COSTS: " + s_c.getCostFitness());
        System.err.println("-----------------------------------------------------------------------------------------------------------------");
    }

    private void printTimeout(Long runTime, Integer it, Integer sb) {
        System.err.println("-----------------------------------------------------------------------------------------------------------------");
        System.err.println("Computation of Initial Solution aborted after timeout of " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - runTime) + " Seconds.");
        System.err.println(it + " Iterations were performed ");
        System.err.println(sb + " Stepbacks were required.");
        System.err.println(s_c.getUnscheduledJobs().size() + " unscheduled Jobs remain.");
        System.err.println("---------------------------------");
    }
}
