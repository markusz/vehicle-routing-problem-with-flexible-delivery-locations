package de.tum.ziller.thesis.thrp.heuristic;

import de.tum.ziller.thesis.thrp.common.Constants;
import de.tum.ziller.thesis.thrp.common.entities.Instance;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.common.exceptions.GeneralInfeasibilityException;
import de.tum.ziller.thesis.thrp.heuristic.strategies.ConstructionStrategy;
import de.tum.ziller.thesis.thrp.heuristic.strategies.ImprovementStrategy;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig;
import de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo.ControlParameter;

public class Solver {
	private static Solver	solver	= null;
	ConstructionStrategy	s1		= Constants.DEFAULT_PHASEONE_STRATEGY;
	ImprovementStrategy		s2		= Constants.DEFAULT_PHASETWO_STRATEGY;

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
