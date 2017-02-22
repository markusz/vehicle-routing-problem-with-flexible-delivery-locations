package de.markusziller.alns.heuristic.strategies.alns.removal;

import de.markusziller.alns.common.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.IALNSOperation;

public interface IALNSDestroy extends IALNSOperation {

    public Solution destroy(Solution s, int nodes) throws Exception;

}
