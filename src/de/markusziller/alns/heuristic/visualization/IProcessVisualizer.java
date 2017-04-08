package de.markusziller.alns.heuristic.visualization;

import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.insertion.IALNSRepair;
import de.markusziller.alns.heuristic.strategies.alns.removal.IALNSDestroy;
import de.markusziller.alns.heuristic.strategies.phasetwo.ALNSProcess;

public interface IProcessVisualizer {

    void onThreadStart(ALNSProcess alnsProcess);

    void onStartConfigurationObtained(ALNSProcess alnsProcess);

    void onDestroyRepairOperationsObtained(ALNSProcess alnsProcess, IALNSDestroy _destroy, IALNSRepair _repair, Solution s_c_new, int q);

    void onSolutionDestroy(ALNSProcess alnsProcess, Solution s_destroy);

    void onSolutionRepaired(ALNSProcess alnsProcess, Solution s_t);

    void onAcceptancePhaseFinsihed(ALNSProcess alnsProcess, Solution s_t);

    void onSegmentFinsihed(ALNSProcess alnsProcess, Solution s_t);

    void onIterationFinished(ALNSProcess alnsProcess, Solution s_t);


}
