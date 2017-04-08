package de.markusziller.alns.heuristic.visualization;

import de.markusziller.alns.entities.Insertion;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;
import de.markusziller.alns.heuristic.strategies.alns.insertion.IALNSRepair;
import de.markusziller.alns.heuristic.strategies.alns.removal.IALNSDestroy;
import de.markusziller.alns.heuristic.strategies.phasetwo.ALNSProcess;

public class GUIVisualizer implements IProcessVisualizer, IStrategyVisualizer {

    @Override
    public void onThreadStart(ALNSProcess alnsProcess) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStartConfigurationObtained(ALNSProcess alnsProcess) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onSolutionDestroy(ALNSProcess alnsProcess, Solution s_destroy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSolutionRepaired(ALNSProcess alnsProcess, Solution s_t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroyRepairOperationsObtained(ALNSProcess alnsProcess, IALNSDestroy _destroy, IALNSRepair _repair, Solution s_c_new, int q) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAcceptancePhaseFinsihed(ALNSProcess alnsProcess, Solution s_t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onJobPlanned(ALNSAbstractOperation a, Insertion i, Solution s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onIterationFinished(ALNSProcess alnsProcess, Solution s_t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSegmentFinsihed(ALNSProcess alnsProcess, Solution s_t) {
        // TODO Auto-generated method stub

    }

}
