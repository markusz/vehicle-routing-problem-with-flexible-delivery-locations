package de.markusziller.alns.heuristic;

import de.markusziller.alns.utils.Constants;
import de.markusziller.alns.entities.Instance;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.exceptions.GeneralInfeasibilityException;
import de.markusziller.alns.heuristic.strategies.ConstructionStrategy;
import de.markusziller.alns.heuristic.strategies.ImprovementStrategy;
import de.markusziller.alns.heuristic.strategies.alns.config.IALNSConfig;
import de.markusziller.alns.heuristic.strategies.phasetwo.ControlParameter;

public class Solver {
    private static Solver solver = null;
    private final ImprovementStrategy s2 = Constants.DEFAULT_PHASETWO_STRATEGY;
    private ConstructionStrategy s1 = Constants.DEFAULT_PHASEONE_STRATEGY;

    private Solver() {
    }

    public static Solver getSolver() {
        if (solver == null) {
            solver = new Solver();
        }
        return solver;
    }

    public void changeStrategy(ConstructionStrategy ss) {
        s1 = ss;
    }

    public Solution getInitialSolution(Instance i) throws GeneralInfeasibilityException {
        return s1.getInitialSolution(i);
    }

    public Solution[] improveSolution(Solution s) {
        return s2.improveSolution(s);
    }

    public Solution[] improveSolution(Solution is, int i) {
        return s2.improveSolution(is, i);
    }

    public Solution[] improveSolution(Solution is, int i, IALNSConfig ac, ControlParameter cp) throws InterruptedException {
        return s2.improveSolution(is, i, ac, cp);
    }
}
