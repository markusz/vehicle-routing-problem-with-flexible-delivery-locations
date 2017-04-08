package de.markusziller.alns.heuristic.visualization;

import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.insertion.IALNSRepair;
import de.markusziller.alns.heuristic.strategies.alns.removal.IALNSDestroy;
import de.markusziller.alns.heuristic.strategies.phasetwo.ALNSProcess;

public class ALNSProcessVisualizationManager implements IProcessVisualizer {

    public final IProcessVisualizer GUI = new GUIVisualizer();
    public final IProcessVisualizer NONE = new DoNothingVisualizer();
    private final IProcessVisualizer CONSOLE = new ConsolePrintVisualizer();
    private final IProcessVisualizer ON_ITERATION_FINISHED = CONSOLE;
    private IProcessVisualizer ON_THREAD_START = CONSOLE;
    private IProcessVisualizer ON_START_CONFIG_OBTAINED = CONSOLE;
    private IProcessVisualizer AFTER_DESTROY = NONE;
    private IProcessVisualizer AFTER_REPAIR = NONE;
    private IProcessVisualizer PERIODIC_SOLUTION_STATUS = CONSOLE;
    private IProcessVisualizer PERIODIC_ROULETTE_WHEEL_STATUS = CONSOLE;

    @Override
    public void onThreadStart(ALNSProcess a) {
        ON_THREAD_START.onThreadStart(a);
    }

    public void setOnThreadStart(IProcessVisualizer iv) {
        ON_THREAD_START = iv;
    }

    @Override
    public void onStartConfigurationObtained(ALNSProcess a) {
        ON_START_CONFIG_OBTAINED.onStartConfigurationObtained(a);
    }

    public void setStartConfigurationObtained(IProcessVisualizer iv) {
        ON_START_CONFIG_OBTAINED = iv;
    }

    @Override
    public void onSolutionDestroy(ALNSProcess a, Solution s_destroy) {
        AFTER_DESTROY.onSolutionDestroy(a, s_destroy);
    }

    public void setAfterDestroy(IProcessVisualizer iv) {
        AFTER_DESTROY = iv;
    }

    @Override
    public void onSolutionRepaired(ALNSProcess a, Solution s_t) {
        AFTER_REPAIR.onSolutionRepaired(a, s_t);
    }

    public void setAfterRepair(IProcessVisualizer iv) {
        AFTER_REPAIR = iv;
    }

    @Override
    public void onDestroyRepairOperationsObtained(ALNSProcess a, IALNSDestroy _destroy, IALNSRepair _repair, Solution s_c_new, int q) {
        PERIODIC_ROULETTE_WHEEL_STATUS.onDestroyRepairOperationsObtained(a, _destroy, _repair, s_c_new, q);
    }

    public void setPeriodicRouletteWheelStatus(IProcessVisualizer iv) {
        PERIODIC_SOLUTION_STATUS = iv;
    }

    @Override
    public void onAcceptancePhaseFinsihed(ALNSProcess a, Solution s_t) {
        PERIODIC_SOLUTION_STATUS.onAcceptancePhaseFinsihed(a, s_t);
    }

    public void setPeriodicSolutionStatus(IProcessVisualizer iv) {
        PERIODIC_SOLUTION_STATUS = iv;
    }

    public void all(IProcessVisualizer iv) {
        ON_THREAD_START = iv;
        ON_START_CONFIG_OBTAINED = iv;
        AFTER_DESTROY = iv;
        AFTER_REPAIR = iv;
        PERIODIC_SOLUTION_STATUS = iv;
        PERIODIC_ROULETTE_WHEEL_STATUS = iv;
    }

    @Override
    public void onIterationFinished(ALNSProcess a, Solution s_t) {
        ON_ITERATION_FINISHED.onIterationFinished(a, s_t);
    }

    @Override
    public void onSegmentFinsihed(ALNSProcess a, Solution s_t) {

    }

}
