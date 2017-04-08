package de.markusziller.alns.heuristic.strategies.alns.removal;

import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.exceptions.RouteConstructionException;
import de.markusziller.alns.heuristic.strategies.alns.ALNSAbstractOperation;

import java.util.ArrayList;
import java.util.Random;


public class RandomDestroy extends ALNSAbstractOperation implements IALNSDestroy {
    @Override
    public Solution destroy(Solution from, int q) {
        Random random = new Random();
        ArrayList<Removal> R = getRemovals(from);
        for (; q > 0; q--) {
            int idx = random.nextInt(R.size());
            Removal r = R.get(idx);
            R.remove(r);
            try {
                from.remove(r.n, r.r);
            } catch (RouteConstructionException e) {
                e.printStackTrace();
            }
        }
        return from;
    }
}
