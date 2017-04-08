package de.markusziller.alns.heuristic.strategies.phasetwo;

import com.google.common.util.concurrent.FutureCallback;
import de.markusziller.alns.entities.Solution;

import java.util.Comparator;
import java.util.TreeSet;

class ALNSProcessManager implements FutureCallback<Solution> {

    private final TreeSet<Solution> solutions = new TreeSet<>(new Comparator<Solution>() {

        @Override
        public int compare(Solution s1, Solution s2) {
            if (s1.getCosts() < s2.getCosts()) {
                return 1;
            }
            if (s1.getCosts() > s2.getCosts()) {
                return -1;
            }
            return 0;
        }
    });

    @Override
    public void onSuccess(Solution result) {
        solutions.add(result);

    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();

    }

    public TreeSet<Solution> getSolutions() {
        return this.solutions;
    }
}
