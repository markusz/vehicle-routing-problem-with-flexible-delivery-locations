package de.markusziller.alns.heuristic.visualization;

import de.markusziller.alns.entities.Insertion;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;

public class ALNSStrategieVisualizationManager implements IStrategyVisualizer {

    public final IStrategyVisualizer GUI = new GUIVisualizer();
    private final IStrategyVisualizer CONSOLE = new ConsolePrintVisualizer();
    private final IStrategyVisualizer NONE = new DoNothingVisualizer();

    private IStrategyVisualizer ON_JOB_PLANNED = CONSOLE;

    public void setOnJobPlanned(IStrategyVisualizer iv) {
        ON_JOB_PLANNED = iv;
    }

    @Override
    public void onJobPlanned(ALNSAbstractOperation a, Insertion i, Solution s) {
        ON_JOB_PLANNED.onJobPlanned(a, i, s);

    }

    public void all(IStrategyVisualizer iv) {
        ON_JOB_PLANNED = iv;
    }

    public void disableAll() {
        ON_JOB_PLANNED = NONE;
    }


}
