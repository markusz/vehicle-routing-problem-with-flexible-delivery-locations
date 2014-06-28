package de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo;

public class ControlParameter {

	private boolean solutionsLinechart;
	private boolean operationsLinechart;
	private boolean solutionImages;

	public ControlParameter(boolean showSolutionsLinechart, boolean showOperationsLinechart, boolean createSolutionImages){
		solutionsLinechart  =   showSolutionsLinechart;
		operationsLinechart =   showOperationsLinechart;
        solutionImages      =   createSolutionImages;
	}

    public boolean isSolutionsLinechart() {
        return this.solutionsLinechart;
    }

    public boolean isOperationsLinechart() {
        return this.operationsLinechart;
    }

    public boolean isSolutionImages() {
        return this.solutionImages;
    }
}
