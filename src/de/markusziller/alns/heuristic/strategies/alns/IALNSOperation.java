package de.markusziller.alns.heuristic.strategies.alns;

import de.markusziller.alns.heuristic.visualization.ALNSStrategieVisualizationManager;

public interface IALNSOperation {

    public int getPi();

    public void setPi(int pi);

    public void addToPi(int pi);

    public double getP();

    public void setP(double p);

    public double getW();

    public void setW(double p);

    public void drawn();

    public int getDraws();

    public void setDraws(int d);

    public ALNSStrategieVisualizationManager getVisualizationManager();

}
