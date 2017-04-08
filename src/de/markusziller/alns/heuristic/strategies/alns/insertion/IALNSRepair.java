package de.markusziller.alns.heuristic.strategies.alns.insertion;

import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.IALNSOperation;

public interface IALNSRepair extends IALNSOperation {

    Solution repair(Solution from);
}
