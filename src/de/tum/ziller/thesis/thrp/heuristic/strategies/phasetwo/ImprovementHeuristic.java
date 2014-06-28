package de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.strategies.ImprovementStrategy;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.ALNSConfiguration;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImprovementHeuristic implements ImprovementStrategy {
	@Override
	public Solution[] improveSolution(Solution s) {
		return improveSolution(s, 1);
	}

	@Override
	public Solution[] improveSolution(Solution s, int threads) {
		Integer cores = threads;
		ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(cores));
		ALNSProcessManager apm = new ALNSProcessManager();
		for (int i = 0; i < cores; i++) {
			IALNSConfig[] confs = ALNSConfiguration.values();
			IALNSConfig con = confs[i % confs.length];
			ListenableFuture<Solution> thread = service.submit(new ALNSProcess(s, con));
			Futures.addCallback(thread, apm);
		}
		service.shutdown();
		try {
			service.awaitTermination(120, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Set<Solution> S = apm.getSolutions();
		return S.toArray(new Solution[0]);
	}

	@Override
	public Solution[] improveSolution(Solution s, int threads, IALNSConfig ac, ControlParameter cp) throws InterruptedException {
		Integer cores = threads;
		ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(cores));
		ALNSProcessManager apm = new ALNSProcessManager();
		for (int i = 0; i < cores; i++) {
			ListenableFuture<Solution> thread = service.submit(new ALNSProcess(s, ac, cp));
			Futures.addCallback(thread, apm);
		}
		service.shutdown();
		try {
			service.awaitTermination(120, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Set<Solution> S = apm.getSolutions();
		return S.toArray(new Solution[0]);
	}
}
