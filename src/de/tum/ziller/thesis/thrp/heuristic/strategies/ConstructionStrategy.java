package de.tum.ziller.thesis.thrp.heuristic.strategies;

import de.tum.ziller.thesis.thrp.common.entities.Instance;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.common.exceptions.GeneralInfeasibilityException;
import de.tum.ziller.thesis.thrp.heuristic.SolverConfiguration;

public interface ConstructionStrategy {

		public Solution getInitialSolution(Instance i) throws GeneralInfeasibilityException;
		public Solution getInitialSolution(Instance i, SolverConfiguration sc) throws GeneralInfeasibilityException;
		public Solution getInitialSolution(Instance i, SolverConfiguration sc, boolean multithreaded) throws GeneralInfeasibilityException;
}
