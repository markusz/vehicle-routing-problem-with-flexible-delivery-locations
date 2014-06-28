package de.tum.ziller.thesis.thrp.heuristic;

import lombok.Getter;

public  class SolverConfiguration {

	long h = 60 * 60 * 1000L;
	long min = 60 * 1000L;
	long sec =  1000L;
	
	@Getter long timelimit = 5 * sec; 
	
}
