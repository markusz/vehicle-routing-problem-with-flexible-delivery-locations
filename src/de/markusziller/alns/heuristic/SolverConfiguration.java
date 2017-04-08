package de.markusziller.alns.heuristic;

public class SolverConfiguration {

    private final long sec = 1000L;
    private final long timelimit = 5 * sec;
    long h = 60 * 60 * 1000L;
    long min = 60 * 1000L;

    public long getTimelimit() {
        return this.timelimit;
    }
}
