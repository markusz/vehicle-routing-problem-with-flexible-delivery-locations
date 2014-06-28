package de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ControlParameter {

	private @Getter boolean solutionsLinechart;
	private @Getter boolean operationsLinechart;
	private @Getter boolean solutionImages;

	public ControlParameter(boolean showSolutionsLinechart, boolean showOperationsLinechart, boolean createSolutionImages){
		solutionsLinechart  =   showSolutionsLinechart;
		operationsLinechart =   showOperationsLinechart;
        solutionImages      =   createSolutionImages;
	}

}
