package de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo;

import java.util.Comparator;
import java.util.TreeSet;

import lombok.Getter;

import com.google.common.util.concurrent.FutureCallback;

import de.tum.ziller.thesis.thrp.common.entities.Solution;

public class ALNSProcessManager implements FutureCallback<Solution> {

	@Getter private TreeSet<Solution> solutions = new TreeSet<>(new Comparator<Solution>() {

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

}
