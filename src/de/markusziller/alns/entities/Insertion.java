package de.markusziller.alns.entities;

import de.markusziller.alns.entities.jobs.WardJob;
import de.markusziller.alns.entities.rooms.TherapyCenter;
import lombok.NonNull;

import java.io.Serializable;


public class Insertion implements Comparable<Insertion>, Serializable {
    private static final long serialVersionUID = -6687473937777930786L;

    private Double costs;

    @NonNull
    private Node node;

    private Node previousNode;
    private Node nextNode;

    @NonNull
    private Therapist therapist;

    @java.beans.ConstructorProperties({"node", "therapist"})
    public Insertion(Node node, Therapist therapist) {
        this.node = node;
        this.therapist = therapist;
    }

    @java.beans.ConstructorProperties({"costs", "node", "previousNode", "nextNode", "therapist"})
    public Insertion(Double costs, Node node, Node previousNode, Node nextNode, Therapist therapist) {
        this.costs = costs;
        this.node = node;
        this.previousNode = previousNode;
        this.nextNode = nextNode;
        this.therapist = therapist;
    }

    public Integer getStart() {
        return node.getTime().getStart();
    }

    public Integer getEnd() {
        return node.getTime().getEnd();
    }

    @Override
    public String toString() {
        return therapist.getName() + " -> " + node;
    }

    //aufsteigend sortiert
    @Override
    public int compareTo(Insertion o) {
        if (o.getCosts() < costs) {
            return 1;
        }
        if (o.getCosts() > costs) {
            return -1;
        }
        return 0;
    }

    public boolean isTransfer() {
        return (node.getJob().getClass() == WardJob.class && node.getRoom().getClass() == TherapyCenter.class);
    }

    public Double getCosts() {
        return this.costs;
    }

    public void setCosts(Double costs) {
        this.costs = costs;
    }

    @NonNull
    public Node getNode() {
        return this.node;
    }

    public void setNode(@NonNull Node node) {
        this.node = node;
    }

    public Node getPreviousNode() {
        return this.previousNode;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    public Node getNextNode() {
        return this.nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    @NonNull
    public Therapist getTherapist() {
        return this.therapist;
    }

    public void setTherapist(@NonNull Therapist therapist) {
        this.therapist = therapist;
    }
}
