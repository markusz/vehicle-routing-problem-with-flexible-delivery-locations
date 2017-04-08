package de.markusziller.alns.heuristic.strategies.alns.removal;

import de.markusziller.alns.entities.Node;
import de.markusziller.alns.entities.Route;

public class Removal {
    final Node n;
    final Route r;

    @java.beans.ConstructorProperties({"n", "r"})
    public Removal(Node n, Route r) {
        this.n = n;
        this.r = r;
    }

    @Override
    public String toString() {
        return n.toString();
    }

    public Node getN() {
        return this.n;
    }

    public Route getR() {
        return this.r;
    }
}
