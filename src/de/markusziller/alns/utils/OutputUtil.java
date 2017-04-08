package de.markusziller.alns.utils;

import com.google.common.collect.LinkedListMultimap;
import de.markusziller.alns.entities.*;
import de.markusziller.alns.entities.jobs.BreakJob;
import de.markusziller.alns.entities.jobs.IdleJob;
import de.markusziller.alns.entities.jobs.OutpatientJob;
import de.markusziller.alns.entities.jobs.WardJob;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class OutputUtil {
    private static final String IMAGE_PATH = "output" + File.separator + "solutions";

    public static String formatMillis(long millis) {
        long t = millis / 1000;
        long h = t % 3600;
        long min = (t % 3600) / 60;
        long s = (t % 60);
        if (h > 0) {
            return String.format("%02d:%02d", min, s);
        }
        return String.format("%02d:%02d:%02d", h, min, s);
    }

    public static String routesToString(Solution s) {
        StringBuilder sb = new StringBuilder();
        for (Therapist t : s.getInstance().getTherapists()) {
            sb.append("Route for ").append(t.getName()).append("\n");
            Set<Node> nodes = s.getAllNodes(t);
            for (Node n : nodes) {
                sb.append(toString(n));
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static void createPNG(Solution s, int it) throws IOException {
        Color[] colors = new Color[]{Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.PINK, Color.YELLOW};
        Color jt_color = Color.ORANGE;
        Color op_color = Color.RED;
        int max_x = 0;
        int max_y = 0;
        for (Therapist t : s.getInstance().getTherapists()) {
            for (Route r : s.getRoutes().get(t)) {
                for (Node n : r.getN()) {
                    if (n.getRoom().getX() > max_x) {
                        max_x = n.getRoom().getX();
                    }
                    if (n.getRoom().getY() > max_y) {
                        max_y = n.getRoom().getY();
                    }
                }
            }
        }
        int margin_x = 20;
        int margin_y = 20;
        int x_overscale = 12;
        int y_overscale = 12;
        int height = max_y * y_overscale + 2 * margin_y;
        int width = max_x * x_overscale + 2 * margin_x;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setBackground(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        BasicStroke bs = new BasicStroke(2);
        g2d.setStroke(bs);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawString(String.format("[%s] costs: %.2f, Iteration: %d", s.getInstance().getSolomonInstance().getName(), s.getCosts(), it), 10, 10);
        Color drawing_color = null;
        int t_nr = 0;
        for (Therapist t : s.getRoutes().keySet()) {
            for (Route r : s.getRoutes().get(t)) {
                Node[] nodes = r.getN().toArray(new Node[0]);
                if (r.noOfTreatmentJobs() > 0) {
                    drawing_color = colors[t_nr++ % colors.length];
                    g2d.setColor(drawing_color);
                }
                for (int i = 0; i < nodes.length; i++) {
                    Node n = nodes[i];
                    if (n.isTreatment()) {
                        if ((n.getJob() instanceof WardJob)) {
                            g2d.setColor(jt_color);
                            WardJob oo = (WardJob) n.getJob();
                            Ellipse2D.Double circle = new Ellipse2D.Double(margin_x + oo.getRoom().getX() * x_overscale, margin_y + oo.getRoom().getY() * y_overscale, 8, 8);
                            g2d.draw(circle);
                            g2d.fill(circle);
                            g2d.drawLine(margin_x + oo.getRoom().getX() * x_overscale, margin_y + oo.getRoom().getY() * y_overscale, margin_x + n.getRoom().getX() * x_overscale, margin_y
                                    + n.getRoom().getY() * y_overscale);
                            g2d.drawString("" + oo.getRoom().getId() + "[" + n.getTime().getStart() + ", " + n.getTime().getEnd() + "]", margin_x + oo.getRoom().getX() * x_overscale, margin_y
                                    + oo.getRoom().getY() * y_overscale);
                        }
                        if ((n.getJob() instanceof OutpatientJob)) {
                            Room r_0 = s.getInstance().getBreakroom();
                            OutpatientJob oo = (OutpatientJob) n.getJob();
                            g2d.setColor(jt_color);
                            Ellipse2D.Double circle = new Ellipse2D.Double(margin_x + r_0.getX() * x_overscale, margin_y + r_0.getY() * y_overscale, 3, 3);
                            g2d.setColor(op_color);
                            g2d.drawLine(margin_x + r_0.getX() * x_overscale, margin_y + r_0.getY() * y_overscale, margin_x + n.getRoom().getX() * x_overscale, margin_y + n.getRoom().getY()
                                    * y_overscale);
                            g2d.draw(circle);
                            g2d.fill(circle);
                        }
                    }
                    g2d.setColor(drawing_color);
                    if (n.isTreatment() || n.getJob() instanceof BreakJob) {
                        for (int j = i; j < nodes.length; j++) {
                            Node o = nodes[j];
                            if (n.getRoom() != o.getRoom()) {
                                g2d.drawLine(margin_x + n.getRoom().getX() * x_overscale, margin_y + n.getRoom().getY() * y_overscale, margin_x + o.getRoom().getX() * x_overscale, margin_y
                                        + o.getRoom().getY() * y_overscale);
                                break;
                            }
                        }
                    }
                }
            }
        }
        for (Therapist t : s.getInstance().getTherapists()) {
            for (Route r : s.getRoutes().get(t)) {
                for (Node n : r.getN()) {
                    int pixel_x = margin_x + n.getRoom().getX() * x_overscale;
                    int pixel_y = margin_y + n.getRoom().getY() * y_overscale;
                    int p_size = 5;
                    for (int x = pixel_x - p_size; x <= pixel_x + p_size; x++) {
                        for (int y = pixel_y - p_size; y <= pixel_y + p_size; y++) {
                            image.setRGB(x, y, Color.RED.getRGB());
                        }
                    }
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("" + n.getRoom().getId(), pixel_x - 2, pixel_y - 2);
                }
            }
        }
        BasicStroke bs2 = new BasicStroke(2);
        g2d.setStroke(bs2);
        g2d.setColor(Color.BLACK);
        StringBuilder sb = new StringBuilder(IMAGE_PATH);
        sb.append(File.separator).append(s.getUid());
        FileUtils.forceMkdir(new File(sb.toString()));
        sb.append(File.separator).append(s.getUid()).append("-").append(it).append(".png");
        ImageIO.write(image, "png", new File(sb.toString()));
    }

    public static String toString(Set<Node> pwset, Integer minutesPerTimeslot) {
        StringBuilder sb = new StringBuilder();
        for (Node node : pwset) {
            String s = TimeUtil.timeslotIndexToTime(node.getStart(), minutesPerTimeslot);
            String e = TimeUtil.timeslotIndexToTime(node.getEnd(), minutesPerTimeslot);
            sb.append("[").append(s).append(",").append(e).append("]: ").append(node.getJob().getName()).append(", ").append(node.getRoom().getName()).append(" -> ");
        }
        return sb.toString();
    }

    public static String toString(Set<Node> pwset) {
        StringBuilder sb = new StringBuilder();
        for (Node node : pwset) {
            String s = "" + node.getStart();
            String e = "" + node.getEnd();
            sb.append("[").append(s).append(",").append(e).append("]: ").append(node.getJob().getName()).append(", ").append(node.getRoom().getName()).append(" -> ");
        }
        return sb.toString();
    }

    public static String toString(Route pw) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Node node : pw.getN()) {
            sb.append("[").append(node.getTime().getStart()).append(",").append(node.getTime().getEnd()).append("]: ");
            sb.append(node.getRoom().getName()).append(", ").append(node.getJob().getName());
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    public static String toString(Route pw, Integer minutesPerTimeslot) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Node node : pw.getN()) {
            sb.append("[").append(node.getTime().getStart()).append(",").append(node.getTime().getEnd()).append("]: ");
            sb.append(node.getRoom().getName()).append(", ").append(node.getJob().getName());
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    public static String toString(Node pwn, Integer minutesPerTimeslot) {
        StringBuilder sb = new StringBuilder();
        String s = TimeUtil.timeslotIndexToTime(pwn.getStart(), minutesPerTimeslot);
        String e = TimeUtil.timeslotIndexToTime(pwn.getEnd() + 1, minutesPerTimeslot);
        sb.append("[").append(s).append(",").append(e).append("]: ").append(pwn.getJob().getName()).append(", ").append(pwn.getRoom().getName());
        return sb.toString();
    }

    private static String toString(Node pwn) {
        StringBuilder sb = new StringBuilder();
        String s = "" + pwn.getStart();
        String e = "" + pwn.getEnd();
        sb.append("[").append(s).append(",").append(e).append("]: ").append(pwn.getJob().getName()).append(", ").append(pwn.getRoom().getName());
        return sb.toString();
    }

    public static String graphicalOutput(Solution s) {
        StringBuilder sb = new StringBuilder();

        for (Therapist t : s.getRoutes().keySet()) {
            sb.append("\n").append(t.getName()).append("\n");

            Integer start = s.getShiftBoundsForTherapist(t).getStart();
            Integer end = s.getShiftBoundsForTherapist(t).getEnd();

            for (int ii = 0; ii < start; ii++) {
                sb.append("_");
            }
            sb.append("|");

            Set<Node> nods = s.getAllNodes(t);

            for (Node pathwayNode : nods) {

                for (int ii = pathwayNode.getTime().getStart(); ii <= pathwayNode.getTime().getEnd(); ii++) {
                    if (pathwayNode.getJob().getClass() == BreakJob.class) {
                        sb.append("o");
                    }
                    if (pathwayNode.getJob().getClass() == IdleJob.class) {
                        sb.append("x");
                    }
                }
                sb.append("|");
            }
            for (int ii = end; ii < s.getInstance().getI_conf().getNumberOfTimeSlots(); ii++) {
                sb.append("_");
            }
        }
        return sb.toString();
    }

    public static void printNRandomPathwayNodes(int i, LinkedListMultimap<Therapist, Node> possibleAllocations) {
        Integer rndKey = 0;
        Random r = new Random();
        Set<Therapist> thp = possibleAllocations.keySet();
        Therapist t = null;
        Node pn = null;
        int ii = 0;
        Integer rndVal = null;
        for (int j = 0; j < i; j++) {
            rndKey = r.nextInt(thp.size());
            ii = 0;
            for (Therapist therapist : thp) {
                if (ii == rndKey) {
                    t = therapist;
                    break;
                }
                ii = ii + 1;
                // break;
            }
            List<Node> list = possibleAllocations.get(t);
            if (list.size() > 0) {
                rndVal = r.nextInt(list.size());
                pn = list.get(rndVal);
                System.out.println(t.getName() + ":" + pn.getJob().getName() + " in " + pn.getRoom().getName() + " at " + pn.getTime().toString());
            } else {
                j--;
            }
        }
    }
}
