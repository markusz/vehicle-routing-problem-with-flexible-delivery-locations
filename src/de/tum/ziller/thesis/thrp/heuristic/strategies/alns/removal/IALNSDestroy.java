package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal;

import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.IALNSOperation;

public interface IALNSDestroy extends IALNSOperation{
	
	public Solution destroy(Solution s, int nodes) throws Exception;

}
