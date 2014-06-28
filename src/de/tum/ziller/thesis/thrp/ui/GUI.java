package de.tum.ziller.thesis.thrp.ui;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.common.entities.Therapist;
import de.tum.ziller.thesis.thrp.common.utils.TimeUtil;

public class GUI {

	private static List<String> rowTitles = new ArrayList<>();

	private Solution s;
	private JFrame f;
	private JTable table;
	private JScrollPane sp;
	
	public void updateSolution(Solution s){
		this.s = s;
		table.setDefaultRenderer(Object.class, new CustomRenderer(this.s));
		table.invalidate();
	}

	
	public GUI(Solution s) {
		
		this.s = s;

		List<Therapist> thp = new ArrayList<>(s.getRoutes().keySet());
		
		Integer[] bounds = s.planningBounds();
		Integer cols = bounds[1] + 1;

		rowTitles.add("");
		for (int i = 0; i < cols; i++) {
			rowTitles.add(TimeUtil.timeslotIndexToTime(i, s.getMinutesPerTimeslot()));
		}

		String[][] tableD = new String[thp.size()][cols + 1];

		for (int i = 0; i < thp.size(); i++) {
			tableD[i][0] = thp.get(i).getId() + "";
		}

		for (int i = 0; i < thp.size(); i++) {
			for (int j = 1; j < cols + 1; j++) {
				tableD[i][j] = "";
			}
		}

		f = new JFrame();
		f.setExtendedState(Frame.MAXIMIZED_BOTH); 
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		table = new JTable(tableD, rowTitles.toArray());
		table.setDefaultRenderer(Object.class, new CustomRenderer(this.s));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(0).setMinWidth(60);
		
		for (int i = 0; i < tableD.length; i++) {
			
		}

		sp = new JScrollPane(table);
		f.add(sp);
		f.pack();
		f.setVisible(true);
	}

}
