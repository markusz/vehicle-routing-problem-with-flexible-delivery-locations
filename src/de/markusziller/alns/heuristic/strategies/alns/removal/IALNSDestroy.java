package de.markusziller.alns.heuristic.strategies.alns.removal;

import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.IALNSOperation;

public interface IALNSDestroy extends IALNSOperation {

    Solution destroy(Solution s, int nodes) throws Exception;

}
