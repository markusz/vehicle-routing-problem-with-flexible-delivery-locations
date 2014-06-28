package de.tum.ziller.thesis.thrp.common;

import de.tum.ziller.thesis.thrp.heuristic.strategies.ConstructionStrategy;
import de.tum.ziller.thesis.thrp.heuristic.strategies.ImprovementStrategy;

public class Constants {
	
	public static final ConstructionStrategy DEFAULT_PHASEONE_STRATEGY = new de.tum.ziller.thesis.thrp.heuristic.strategies.phaseone.ConstructionHeuristic();
	public static final ImprovementStrategy DEFAULT_PHASETWO_STRATEGY = new de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo.ImprovementHeuristic();

}
