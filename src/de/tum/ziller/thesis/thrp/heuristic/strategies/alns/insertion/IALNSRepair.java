package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.insertion;

import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.IALNSOperation;

public interface IALNSRepair extends IALNSOperation{

	public Solution repair(Solution from);
}
