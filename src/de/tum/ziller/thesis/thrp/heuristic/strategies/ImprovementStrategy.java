package de.tum.ziller.thesis.thrp.heuristic.strategies;

import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig;
import de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo.ControlParameter;

public interface ImprovementStrategy {

	Solution[] improveSolution(Solution s);
	Solution[] improveSolution(Solution is, int i);
	Solution[] improveSolution(Solution s, int threads, IALNSConfig ac, ControlParameter cp);

}
