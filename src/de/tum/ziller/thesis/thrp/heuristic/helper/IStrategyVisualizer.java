package de.tum.ziller.thesis.thrp.heuristic.helper;

import de.tum.ziller.thesis.thrp.common.entities.Insertion;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.ALNSAbstractOperation;

public interface IStrategyVisualizer {

	void onJobPlanned(ALNSAbstractOperation a, Insertion i, Solution s);
	

	
	

}
