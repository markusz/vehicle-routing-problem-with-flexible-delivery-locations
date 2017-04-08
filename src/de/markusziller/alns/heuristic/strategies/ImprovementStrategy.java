package de.markusziller.alns.heuristic.strategies;

import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.config.IALNSConfig;
import de.markusziller.alns.heuristic.strategies.phasetwo.ControlParameter;

public interface ImprovementStrategy {

    Solution[] improveSolution(Solution s);

    Solution[] improveSolution(Solution is, int i);

    Solution[] improveSolution(Solution s, int threads, IALNSConfig ac, ControlParameter cp) throws InterruptedException;

}
