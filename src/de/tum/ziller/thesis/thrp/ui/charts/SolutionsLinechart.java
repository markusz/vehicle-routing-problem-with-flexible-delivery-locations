package de.tum.ziller.thesis.thrp.ui.charts;

import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.helper.IProcessVisualizer;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.insertion.IALNSRepair;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal.IALNSDestroy;
import de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo.ALNSProcess;
import lombok.SneakyThrows;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;

public class SolutionsLinechart implements IProcessVisualizer{
	
	double c_start;
	XYSeries xy_s_c = new XYSeries("s_c");
	XYSeries xy_s_g = new XYSeries("s_g");
	XYSeries xy_s_t = new XYSeries("s_t");
	IALNSConfig c;
	JFreeChart chart;
	final NumberAxis yAxis = new NumberAxis("c");
	final NumberAxis xAxis = new NumberAxis("i");
	XYTextAnnotation annotation;
	long time;
//	private SolutionsLinechart sl;
	double best_complete_solved = 0;
	
	ALNSProcess alns;
	
	public SolutionsLinechart(ALNSProcess a) throws InterruptedException{
		this.alns = a;
	}
	
	public void render(){
		Solution s_c = alns.getS_c();
		IALNSConfig conf = alns.getConfig();
		
		this.time = System.currentTimeMillis();
		this.c = conf;
		c_start = s_c.getCostFitness();
		String legend = String.format(
				"\u03C9=%d |\u03C4=%d | r_p=%.1f | s1=%d | s2=%d | s3=%d | c=%.6f | \u03B4=%.2f | \u03A9=%.2f ",
						c.getOmega(),
						c.getTau(),
						c.getR_p(),
						c.getSigma_1(),
						c.getSigma_2(),
						c.getSigma_3(),
						c.getC(),
						c.getDelta(),
						c.getBig_omega());
		
		yAxis.setAutoRange(true);
		yAxis.setAutoRangeIncludesZero(false);
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(xy_s_c);
		data.addSeries(xy_s_g);
		data.addSeries(xy_s_t);
		
		
		//Create the chart
		chart = ChartFactory.createXYLineChart(
				"Kosten", "i", "c", data,
				PlotOrientation.VERTICAL, true, true, false);
		
		
		TextTitle st = new TextTitle(legend);
		st.setBorder(1, 1,1,1);
		st.setBackgroundPaint(Color.LIGHT_GRAY);
		chart.addSubtitle(st);
		
		
		XYPlot plot = (XYPlot) chart.getPlot();

		plot.setRangeAxis(yAxis);
		plot.setDomainAxis(xAxis);
		
		float lineWidth = 2f;
		float dash[] = {8.0f};
		float dot[] = {lineWidth};
		float dash_phase = 1.0f;
	
		plot.getRenderer().setSeriesStroke(0, 
	            new BasicStroke(
	            		lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
	                    10.0f,dash, dash_phase
	                ));
		
		
		plot.getRenderer().setSeriesStroke(2, 
	            new BasicStroke(
	            		lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
	                    10.0f, dot, dash_phase
	                ));
		
		//Render the frame
		
		ChartFrame chartFrame = new ChartFrame("L�sungsverlauf der Nachbarschaftssuche", chart);
		chartFrame.setVisible(true);
		chartFrame.setSize(900, 500);
		
		
		
	}

//	public void update(int i, Solution s_c, Solution s_g, Solution s_t, double T) {
//		Math.round(1.);
//		double impr = s_g.getFitness()*100 / c_start;
//		long s = (System.currentTimeMillis() - time)/1000;
//		
//		String title = String.format("Beste: %.2f | Start: %.2f | \u0394_s: %.1f%%  | T=%.2f%% | t=%02d:%02d  \n", 
//				s_g.getFitness(),
//				c_start,
//				impr,
//				T*100, 
//				(s%3600)/60, 
//				(s%60)
//				);
//		
//		chart.setTitle(title);
//		if(i > 2){
//			xy_s_c.add(i, s_c.getFitness());
//			xy_s_g.add(i, s_g.getFitness());
//			xy_s_t.add(i, s_t.getFitness());
//		}
//		
//		
//		yAxis.setLowerBound(0.9 * s_g.getFitness());
//		
//	}

	@Override
	public void onThreadStart(ALNSProcess a) {
		// TODO Auto-generated method stub
		
	}

	@SneakyThrows
	@Override
	public void onStartConfigurationObtained(ALNSProcess a) {
		this.render();
	}

	@Override
	public void onSolutionDestroy(ALNSProcess a, Solution s_destroy) {
	}

	@Override
	public void onSolutionRepaired(ALNSProcess a, Solution s_t) {
	}

	@Override
	public void onDestroyRepairOperationsObtained(ALNSProcess a, IALNSDestroy _destroy, IALNSRepair _repair, Solution s_c_new, int q) {
	}

	@Override
	public void onAcceptancePhaseFinsihed(ALNSProcess a, Solution s_t) {
	}

	@Override
	public void onIterationFinished(ALNSProcess a, Solution s_t) {
		Math.round(1.);
		double impr = a.getS_g().getCostFitness()*100 / c_start;
		long s = (System.currentTimeMillis() - time)/1000;
		
		String title = String.format("Beste: %.2f | Start: %.2f | \u0394_s: %.1f%%  | T=%.2f%% | t=%02d:%02d  \n", 
				a.getS_g().getCostFitness(),
				c_start,
				impr,
				a.getT()/a.getT_s()*100, 
				(s%3600)/60, 
				(s%60)
				);
		
		chart.setTitle(title);
		
			xy_s_c.add(a.getI(), a.getS_c().getCostFitness());
			xy_s_g.add(a.getI(), a.getS_g().getCostFitness());
			xy_s_t.add(a.getI(), s_t.getCostFitness());

	}

	@Override
	public void onSegmentFinsihed(ALNSProcess a, Solution s_t) {
	}
	
	// Prepare the data set

}
