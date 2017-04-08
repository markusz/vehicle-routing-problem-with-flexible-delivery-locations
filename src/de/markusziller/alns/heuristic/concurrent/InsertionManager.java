package de.markusziller.alns.heuristic.concurrent;

import com.google.common.util.concurrent.FutureCallback;
import de.markusziller.alns.entities.Insertion;
import lombok.NonNull;

import java.util.Comparator;
import java.util.TreeSet;

public class InsertionManager implements FutureCallback<Insertion> {

    private final int THRESHOLD = 20;

    @NonNull
    private final
    TreeSet<Insertion> list = new TreeSet<>(new Comparator<Insertion>() {

        @Override
        public int compare(Insertion o1, Insertion o2) {
            if (o1.getCosts() > o2.getCosts()) {
                return 1;
            }
            if (o1.getCosts() < o2.getCosts()) {
                return -1;
            }

            return 0;
        }
    });

    private Double best = Double.MAX_VALUE;
    private Insertion bestVal = null;

    public InsertionManager() {
    }

    public void insertNodeList(Insertion result) {
        synchronized (result) {
            if (list.size() == 0 || result.getCosts() < list.last().getCosts()) {

                list.add(result);

                if (list.size() >= THRESHOLD) {

                    list.remove(list.last());
                    if (list.first().getCosts() < best) {
                        best = list.first().getCosts();
                    }
                }
            }
        }
    }


    private synchronized void insertNodeSingle(Insertion result) {
        if (bestVal == null || result.getCosts() < bestVal.getCosts()) {
            bestVal = result;
        }
    }

    @Override
    public void onSuccess(Insertion result) {
        insertNodeSingle(result);
    }

    @Override
    public synchronized void onFailure(Throwable t) {
        t.printStackTrace();
        System.out.println("Fail message: " + t.getMessage());
    }

    public Double getBest() {
        return this.best;
    }

    public void setBest(Double best) {
        this.best = best;
    }

    public Insertion getBestVal() {
        return this.bestVal;
    }

    public void setBestVal(Insertion bestVal) {
        this.bestVal = bestVal;
    }
}
