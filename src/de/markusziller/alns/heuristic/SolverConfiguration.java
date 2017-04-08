package de.markusziller.alns.heuristic;

public class SolverConfiguration {

    long h = 60 * 60 * 1000L;
    long min = 60 * 1000L;
    private final long sec = 1000L;

    private final long timelimit = 5 * sec;

    public long getTimelimit() {
        return this.timelimit;
    }
}
