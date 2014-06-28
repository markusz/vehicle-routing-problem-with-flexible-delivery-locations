package de.tum.ziller.thesis.thrp.heuristic.concurrent;

import java.util.Comparator;
import java.util.TreeSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.google.common.util.concurrent.FutureCallback;

import de.tum.ziller.thesis.thrp.common.entities.Insertion;

 @RequiredArgsConstructor 
public class InsertionManager implements FutureCallback<Insertion> {

	private final int THRESHOLD = 20;
	
	@NonNull
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
	
	private @Getter @Setter Double best = Double.MAX_VALUE;
	private  @Getter @Setter Insertion bestVal = null;
	
	public void insertNodeList(Insertion result){
		synchronized (result) {
		if(list.size() == 0 || result.getCosts() < list.last().getCosts()){
				
				list.add(result);
				
				if(list.size() >= THRESHOLD){
					
					list.remove(list.last());
					if(list.first().getCosts() < best){
						best = list.first().getCosts();
//						log.info("Set new best solution with Costs of "+list.first().getCosts()+": "+list.first().getNode());
					}
				}
			}
		}
	}
	
	
	public synchronized void insertNodeSingle(Insertion result){
				if(bestVal == null || result.getCosts() < bestVal.getCosts()){
					bestVal = result;
//					log.info("Set new best solution with Costs of "+bestVal.getCosts()+": "+bestVal.getNode());
				}
	}
	
	@Override
	public void onSuccess(Insertion result) {
		insertNodeSingle(result);
	}

	@Override
	public synchronized void onFailure(Throwable t) {
		t.printStackTrace();
		System.out.println("Fail message: "+t.getMessage());
	}

}
