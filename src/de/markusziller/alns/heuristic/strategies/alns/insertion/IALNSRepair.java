package de.markusziller.alns.heuristic.strategies.alns.insertion;

import de.markusziller.alns.common.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.IALNSOperation;

public interface IALNSRepair extends IALNSOperation {

    public Solution repair(Solution from);
}
