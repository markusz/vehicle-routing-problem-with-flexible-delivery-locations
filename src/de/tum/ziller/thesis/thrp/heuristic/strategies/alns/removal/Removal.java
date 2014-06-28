package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.tum.ziller.thesis.thrp.common.entities.Node;
import de.tum.ziller.thesis.thrp.common.entities.Route;

@AllArgsConstructor @Getter
public class Removal{
	Node n;
	Route r;
	
	@Override
	public String toString() {
		return n.toString();
	}
}
