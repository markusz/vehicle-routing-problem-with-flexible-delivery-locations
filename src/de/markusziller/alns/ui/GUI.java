package de.markusziller.alns.ui;

import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.entities.Therapist;
import de.markusziller.alns.utils.TimeUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class GUI {

    private static final List<String> rowTitles = new ArrayList<>();
    private final JTable table;
    private Solution s;

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

        JFrame f = new JFrame();
        f.setExtendedState(Frame.MAXIMIZED_BOTH);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        table = new JTable(tableD, rowTitles.toArray());
        table.setDefaultRenderer(Object.class, new CustomRenderer(this.s));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setMinWidth(60);

        for (String[] aTableD : tableD) {

        }

        JScrollPane sp = new JScrollPane(table);
        f.add(sp);
        f.pack();
        f.setVisible(true);
    }

    public void updateSolution(Solution s) {
        this.s = s;
        table.setDefaultRenderer(Object.class, new CustomRenderer(this.s));
        table.invalidate();
    }

}
