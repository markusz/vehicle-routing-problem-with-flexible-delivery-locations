package de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ControlParameter {
	
	private @Getter boolean solutionsLinechart;
	private @Getter boolean operationsLinechart;
	private @Getter boolean c;
	
	public ControlParameter(boolean s, boolean o){
		solutionsLinechart =s;
		operationsLinechart =o;
		c = true;
	}

}
