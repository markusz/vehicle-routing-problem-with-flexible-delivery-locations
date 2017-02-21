package de.tum.ziller.thesis.thrp.heuristic;

public  class SolverConfiguration {

	long h = 60 * 60 * 1000L;
	long min = 60 * 1000L;
	long sec =  1000L;
	
	long timelimit = 5 * sec;

    public long getTimelimit() {
        return this.timelimit;
    }
}
