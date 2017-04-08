package de.markusziller.alns.utils;

import de.markusziller.alns.heuristic.strategies.ConstructionStrategy;
import de.markusziller.alns.heuristic.strategies.ImprovementStrategy;
import de.markusziller.alns.heuristic.strategies.phaseone.ConstructionHeuristic;
import de.markusziller.alns.heuristic.strategies.phasetwo.ImprovementHeuristic;

public class Constants {

    public static final ConstructionStrategy DEFAULT_PHASEONE_STRATEGY = new ConstructionHeuristic();
    public static final ImprovementStrategy DEFAULT_PHASETWO_STRATEGY = new ImprovementHeuristic();

}
