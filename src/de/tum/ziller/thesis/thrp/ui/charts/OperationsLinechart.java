package de.tum.ziller.thesis.thrp.ui.charts;

import java.awt.BasicStroke;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.helper.IProcessVisualizer;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.IALNSOperation;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.insertion.IALNSRepair;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal.IALNSDestroy;
import de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo.ALNSProcess;

public class OperationsLinechart implements IProcessVisualizer {

	Map<IALNSOperation, XYSeries>	map		= new HashMap<>();
	final NumberAxis				yAxis	= new NumberAxis("p");
	final NumberAxis				xAxis	= new NumberAxis("i");

	ALNSProcess						alns;

	public OperationsLinechart(ALNSProcess a) throws InterruptedException {
		this.alns = a;

	}

	public void render() {
		XYSeriesCollection data = new XYSeriesCollection();
		IALNSOperation[] o_d = alns.getDestroy_ops();
		IALNSOperation[] o_r = alns.getRepair_ops();

		for (int i = 0; i < o_r.length; i++) {
			XYSeries xy = new XYSeries(o_r[i].getClass().getSimpleName());
			map.put(o_r[i], xy);
			data.addSeries(xy);
		}

		for (int i = 0; i < o_d.length; i++) {

			XYSeries xy = new XYSeries(o_d[i].getClass().getSimpleName());

			map.put(o_d[i], xy);
			data.addSeries(xy);
		}

		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// data.getSeries(0).get

		// Create the chart
		JFreeChart chart = ChartFactory.createXYLineChart("Wahrscheinlichkeiten der Operatoren f�r Solomon-Instanz", "i", "p", data, PlotOrientation.VERTICAL, true, true, false);

		final XYPlot plot = (XYPlot) chart.getPlot();

		float lineWidth = 2f;
		float dash[] = { 8.0f };
		float dot[] = { lineWidth };
		float phase = 0.0f;
		
		

		for (int i = 0; i < o_r.length; i++) {
//			plot.getRenderer(i).
			plot.getRenderer().setSeriesStroke(i, new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, phase));
		}

		for (int i = o_r.length; i < o_d.length+o_r.length; i++) {
			plot.getRenderer().setSeriesStroke(i, new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dot, phase));
		}
		plot.setDomainAxis(xAxis);

		// Render the frame
		ChartFrame chartFrame = new ChartFrame("Wahrscheinlichkeiten", chart);
		chartFrame.setVisible(true);
		chartFrame.setSize(1000, 600);

	}

	public void update(int it) {

		for (IALNSOperation io : map.keySet()) {
			map.get(io).add(it, io.getP());
		}
	}

	@Override
	public void onThreadStart(ALNSProcess a) {
	}

	@Override
	public void onStartConfigurationObtained(ALNSProcess a) {
		this.render();
		for (IALNSOperation io : map.keySet()) {
			map.get(io).add(alns.getI(), io.getP());
		}
	}

	@Override
	public void onSolutionDestroy(ALNSProcess a, Solution s_destroy) {

	}

	@Override
	public void onSolutionRepaired(ALNSProcess a, Solution s_t) {

	}

	@Override
	public void onDestroyRepairOperationsObtained(ALNSProcess a, IALNSDestroy _destroy, IALNSRepair _repair, Solution s_c_new, int q) {
		// for(IALNSOperation io: map.keySet()){
		// map.get(io).add(a.getI, io.getP());
		// }

	}

	@Override
	public void onAcceptancePhaseFinsihed(ALNSProcess a, Solution s_t) {

	}

	@Override
	public void onIterationFinished(ALNSProcess a, Solution s_t) {

	}

	@Override
	public void onSegmentFinsihed(ALNSProcess a, Solution s_t) {
		for (IALNSOperation io : map.keySet()) {
			map.get(io).add(alns.getI(), io.getP());
		}

	}
}
