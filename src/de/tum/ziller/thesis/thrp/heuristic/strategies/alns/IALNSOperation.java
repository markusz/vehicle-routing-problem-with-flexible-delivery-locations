package de.tum.ziller.thesis.thrp.heuristic.strategies.alns;

import de.tum.ziller.thesis.thrp.heuristic.helper.ALNSStrategieVisualizationManager;

public interface IALNSOperation {
	
	public void setPi(int pi);
	public int getPi();
	
	public void addToPi(int pi);
	public void setP(double p);
	public double getP();

	public void setW(double p);
	public double getW();
	
	public void drawn();
	public int getDraws();
	public void setDraws(int d);
	
	public ALNSStrategieVisualizationManager getVisualizationManager();

}
