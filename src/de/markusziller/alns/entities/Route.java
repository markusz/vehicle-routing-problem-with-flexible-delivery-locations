package de.markusziller.alns.entities;

import de.markusziller.alns.utils.Comparators;
import de.markusziller.alns.entities.jobs.BreakJob;
import de.markusziller.alns.entities.jobs.IdleJob;
import de.markusziller.alns.entities.jobs.TreatmentJob;
import de.markusziller.alns.entities.jobs.WardJob;
import de.markusziller.alns.entities.rooms.TherapyCenter;
import de.markusziller.alns.exceptions.RouteConstructionException;

import java.io.Serializable;
import java.util.*;

public class Route implements Comparable<Route>, Serializable {

    private static final long serialVersionUID = -3762176626624742034L;
    private NavigableSet<Node> N = new TreeSet<>(Comparators.NODE_START_ASCENDING);

    public Route(Node... p) {
        for (Node pathwayNode : p) {
            addPathwayNode(pathwayNode);
        }
    }

    private void addPathwayNode(Node pn) {
        N.add(pn);
    }

    public Room getEndRoom() {
        return getEndNode().getRoom();
    }

    public Integer getStartTime() {
        return getStartNode().getTime().getStart();
    }

    public Integer getEndTime() {
        return getEndNode().getTime().getEnd();
    }

    private Node getEndNode() {
        return N.last();
    }

    public Room getStartRoom() {
        return getStartNode().getRoom();
    }

    private Node getStartNode() {
        return N.first();
    }

    public int noOfTreatmentJobs() {
        int i = 0;
        for (Node n : N) {
            if (n.isTreatment()) {
                i++;
            }
        }
        return i;
    }

    public Node[] getMostExpensiveArcs(Instance instance) throws Exception {
        Node[] arcs = new Node[3];
        double costs = 0;
        Node[] nodes = N.toArray(new Node[0]);
        if (nodes.length < 3) {
            throw new Exception("weniger als 3 knoten -> keine kosten auf dieser Route");
        } else {
            for (int i = 1; i < nodes.length; i++) {
                Node l = null;
                final Node m = nodes[i];
                Node n = null;
                if (!(m.getJob() instanceof TreatmentJob)) {
                    continue;
                }
                for (int j = i - 1; j >= 0; j--) {
                    if (nodes[j].getJob() instanceof TreatmentJob || nodes[j].getJob() instanceof BreakJob) {
                        l = nodes[j];
                        break;
                    }
                }
                for (int j = i + 1; j < nodes.length; j++) {
                    if (nodes[j].getJob() instanceof TreatmentJob || nodes[j].getJob() instanceof BreakJob) {
                        n = nodes[j];
                        break;
                    }
                }
                if (n == null || l == null) {
                    continue;
                }
                double c_lmn = 0.;
//				double c_ln = 0.;
                // double c_d = 0.;
                // Berechne die Kosten die durch m erzeugt werden
                c_lmn += instance.getRouteCosts(l.getRoom(), m.getRoom());
                c_lmn += instance.getRouteCosts(m.getRoom(), n.getRoom());
                if (m.getJob() instanceof WardJob && m.getRoom() instanceof TherapyCenter) {
                    WardJob j_m = (WardJob) m.getJob();
                    c_lmn += instance.getTransportCosts(j_m.getRoom(), m.getRoom());
                    c_lmn += instance.getTransportCosts(j_m.getRoom(), m.getRoom());
                }
                // Zusatzkosten durch m
                // double c_d = c_lmn - c_ln;
                if (c_lmn > costs) {
                    costs = c_lmn;
                    arcs[0] = l;
                    arcs[1] = m;
                    arcs[2] = n;
                }
                // Berechne die kosten ohne m
                // c_ln += instance.getRouteCosts(l.getRoom(), n.getRoom());
            }
            return arcs;
        }
    }

    public int noOfTreatmentOrBreakJobs() {
        int i = 0;
        for (Node n : N) {
            if (n.isTreatment() || n.getJob() instanceof BreakJob) {
                i++;
            }
        }
        return i;
    }

    public List<Node> getNodesInRoom(Room r) {
        List<Node> temp = new ArrayList<>();
        for (Node pathwayNode : N) {
            if (pathwayNode.getRoom().equals(r)) {
                temp.add(pathwayNode);
            }
        }
        return temp;
    }

    public Node getNodeForJob(Job j) throws RouteConstructionException {
        return isJobOnPathway(j);
    }

    private Node isJobOnPathway(Job j) throws RouteConstructionException {
        for (Node pathwayNode : N) {
            if (pathwayNode.getJob().equals(j)) {
                return pathwayNode;
            }
        }
        throw new RouteConstructionException("Job not on pathway");
    }

    public String toGraphicalString() {
        StringBuilder sb = new StringBuilder();
        for (Node pn : N) {
            for (int i = 0; i < pn.getTime().getEnd() - pn.getTime().getStart(); i++) {
                if (pn.getJob().getClass() == BreakJob.class) {
                    sb.append("o");
                }
                if (pn.getJob().getClass() == IdleJob.class) {
                    sb.append("x");
                }
            }
            sb.append("|");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Node node : N) {
            sb.append("[").append(node.getTime().getStart()).append(",").append(node.getTime().getEnd()).append("]: ");
            sb.append(node.getRoom().getName()).append(", ").append(node.getJob().getName());
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public int compareTo(Route o) {
        if (this.getStartTime() > o.getStartTime()) {
            return 1;
        }
        if (this.getStartTime() < o.getStartTime()) {
            return -1;
        } else {
            return 0;
        }
    }


    public void insert(Node n_i) {
        Node n_r = null;
        Node n_a = null;
        Integer t_e = N.last().getEnd();
        for (Node n : N) {
            if (n.getStart() <= n_i.getStart() && n.getEnd() >= n_i.getEnd()) {
                if (n_i.getEnd() < n.getEnd()) {

                    if (n_i.getStart().intValue() == n.getStart().intValue()) {
                        // do nothing
                    }

                    if (n_i.getStart() > n.getStart()) {
                        Node pn_before = new Node("", n.getRoom(), n.getJob(), new Timeslot(n.getStart(), n_i.getStart() - 1));
                        n_a = pn_before;
                    }
                    n.setStart(n_i.getEnd() + 1);
                }
                if (n_i.getEnd().intValue() == n.getEnd().intValue()) {

                    if (n_i.getStart() > n.getStart()) {
                        n.setEnd(n_i.getStart() - 1);
                    }

                    if (n_i.getStart().intValue() == n.getStart().intValue()) {
                        n_r = n;
                    }
                }
            }
        }
        if (n_r != null) {
            N.remove(n_r);
        }
        if (n_a != null) {
            N.add(n_a);
        }
        N.add(n_i);
        if (N.last().getEnd() < t_e) {
            System.err.println("Something went horribly wrong in Route.insert(Node n_i)");
        }
    }


    public Route remove(Node nn) throws RouteConstructionException {
        if (!N.contains(nn)) {
            throw new RouteConstructionException("Node not in route");
        }
        Node[] nodes = N.toArray(new Node[0]);
        for (int i = 0; i < nodes.length; i++) {
            Node m = nodes[i];
            if (i > 0) {
                Node l = nodes[i - 1];
                if (m == nn) {
                    Node n = new Node();
                    // Der zu l�schende Job wird durch einen Idle Job mit im gleichen Zeitfenster ersetzt, der im selben Raum wie der
                    // Vorg�nger stattfindet
                    n.setJob(new IdleJob());
                    n.setRoom(l.getRoom());
                    n.setTime(m.getTime());
                    n.setMetaData(m.getMetaData());
                    N.remove(m);
                    insert(n);
                    break;
                }
            }
        }
        cleanup();
        return this;
    }


    public void cleanup() {
        List<Node> rem = new LinkedList<>();
        Node[] nodes = N.toArray(new Node[0]);
        for (int i = 0; i < nodes.length; i++) {
            // nicht erster und nicht letzter job
            if (i > 0 && i < nodes.length - 1) {
                Node m = nodes[i];
                // Jobs die zum l�schen markiert wurden werden �bersprungen
                if (rem.contains(m)) {
                    continue;
                }
                // Idlejob -> Alle direkt folgenden Pausenjobs finden in diesem Raum statt und dauern bis zum ende des letzten konsekutiven
                if (m.getJob() instanceof IdleJob) {
                    for (int j = i + 1; j < nodes.length - 1; j++) {
                        Node t = nodes[j];
                        if (rem.contains(t)) {
                            continue;
                        }
                        if (t.getJob() instanceof IdleJob) {
                            // if (t.getRoom() != m.getRoom()) {
                            // t.setRoom(m.getRoom());
                            // }
                            m.setEnd(t.getEnd());
                            rem.add(t);
                        } else {
                            break;
                        }
                    }
                    // Behandlung
                } else {
                    for (int j = i + 1; j < nodes.length - 1; j++) {
                        Node t = nodes[j];
                        if (rem.contains(t)) {
                            continue;
                        }
                        if (t.getJob() instanceof IdleJob) {
                            if (t.getRoom() != m.getRoom()) {
                                t.setRoom(m.getRoom());
                            }
                        }
                    }
                }
            }
        }
        N.removeAll(rem);
    }

    public boolean contains(Node to_remove) {
        return N.contains(to_remove);
    }

    public NavigableSet<Node> getN() {
        return this.N;
    }

    public void setN(NavigableSet<Node> N) {
        this.N = N;
    }
}
