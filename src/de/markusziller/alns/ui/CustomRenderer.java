package de.markusziller.alns.ui;

import de.markusziller.alns.entities.Node;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.entities.Therapist;
import de.markusziller.alns.entities.jobs.*;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = -8529474756299749781L;

    private final Solution s;

    private final Color WARD_COLOR = new Color(255, 211, 155);
    private final Color BREAK_JOB = new Color(161, 161, 161);
    private final Color ICU_COLOR = new Color(205, 170, 125);
    private final Color OUT_COLOR = new Color(238, 180, 180);
    private final Color JOB_TRANSFER = new Color(139, 35, 35);
    private final Color NO_JOB_TRANSFER = Color.BLACK;


    public CustomRenderer(Solution s) {
        this.s = s;
        setOpaque(true); // MUST do this for background to show up.
    }

    private void setCellAttributes(int row, int column) {

        List<Therapist> thp = new ArrayList<>(s.getRoutes().keySet());

        // Therapeut #row
        Therapist t = thp.get(row);

        setBackground(Color.LIGHT_GRAY);
        setText("");
        setBorder(new MatteBorder(0, 0, 0, 0, Color.BLACK));

        // Wenn Spalte == 0 -> setze Therapeutennamen
        if (column == 0) {
            setText(t.getName());
            setBackground(Color.ORANGE);
        } else {

            Set<Node> nodes = new HashSet<>();
            try {
                nodes = s.getAllNodes(t);
            } catch (Exception e) {
                //fail silent
            }
            for (Node node : nodes) {
                // Spalten sind 1 nach rechts verschoben, d.h. t=0 -> col = 1
                int adjCol = column - 1;
                if (node.getStart() <= adjCol && node.getEnd() >= adjCol) {
                    if (node.getJob().getClass() == IdleJob.class) {
                        setBackground(Color.WHITE);
                    } else if (node.getJob().getClass() == BreakJob.class) {
                        setBackground(BREAK_JOB);
                    } else {

                        int brdr = 2;
                        Color border = node.hasJobTransfer() ? JOB_TRANSFER : NO_JOB_TRANSFER;
                        if (node.getEnd().intValue() == adjCol && node.getStart().intValue() == adjCol) {

                            setBorder(new MatteBorder(brdr, brdr, brdr, brdr, border));
                        } else {

                            if (node.getEnd().intValue() == adjCol) {
                                setBorder(new MatteBorder(brdr, 0, brdr, brdr, border));
                            }
                            if (node.getStart().intValue() == adjCol) {
                                setBorder(new MatteBorder(brdr, brdr, brdr, 0, border));
                            }
                            if (node.getStart() < adjCol && node.getEnd() > adjCol) {
                                setBorder(new MatteBorder(brdr, 0, brdr, 0, border));
                            }
                        }
                        if (node.getJob().getClass() == ICUJob.class) {
                            setBackground(ICU_COLOR);
                            setText(node.getRoom().getId() + "");
                        } else if (node.getJob().getClass() == WardJob.class) {
                            setBackground(WARD_COLOR);
                            setText(node.getRoom().getId() + "");
                        } else if (node.getJob().getClass() == OutpatientJob.class) {
                            setBackground(OUT_COLOR);
                            setText(node.getRoom().getId() + "");
                        }

                    }
                }
            }
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        table.setRowHeight(30);
        setCellAttributes(row, column);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        return this;
    }

}
