package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal;

import de.tum.ziller.thesis.thrp.common.entities.Node;
import de.tum.ziller.thesis.thrp.common.entities.Route;

public class Removal{
	Node n;
	Route r;

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
