package de.tum.ziller.thesis.thrp.common.abstraction;

import java.util.ArrayList;
import java.util.List;

import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.heuristic.helper.IProcessVisualizer;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.insertion.IALNSRepair;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.removal.IALNSDestroy;
import de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo.ALNSProcess;

public class ALNSObserver implements IProcessVisualizer{
	
	private List<IProcessVisualizer> o = new ArrayList<>();
	
	public void add(IProcessVisualizer ob){
		o.add(ob);
	}
	
	public void remove(IProcessVisualizer ob){
		o.remove(ob);
	}
	
	public void clear(){
		o.clear();
	}

	@Override
	public void onThreadStart(ALNSProcess a) {
		for(IProcessVisualizer o_ : o){
			o_.onThreadStart(a);
		}
	}

	@Override
	public void onStartConfigurationObtained(ALNSProcess a) {
		for(IProcessVisualizer o_ : o){
			o_.onStartConfigurationObtained(a);
		}
	}

	@Override
	public void onDestroyRepairOperationsObtained(ALNSProcess a, IALNSDestroy _destroy, IALNSRepair _repair, Solution s_c_new, int q) {
		for(IProcessVisualizer o_ : o){
			o_.onDestroyRepairOperationsObtained(a, _destroy, _repair, s_c_new, q);
		}
	}

	@Override
	public void onSolutionDestroy(ALNSProcess a, Solution s_destroy) {
		for(IProcessVisualizer o_ : o){
			o_.onSolutionDestroy(a, s_destroy);
		}
	}

	@Override
	public void onSolutionRepaired(ALNSProcess a, Solution s_t) {
		for(IProcessVisualizer o_ : o){
			o_.onSolutionRepaired(a, s_t);
		}
	}

	@Override
	public void onAcceptancePhaseFinsihed(ALNSProcess a, Solution s_t) {
		for(IProcessVisualizer o_ : o){
			o_.onAcceptancePhaseFinsihed(a, s_t);
		}
	}

	@Override
	public void onSegmentFinsihed(ALNSProcess a, Solution s_t) {
		for(IProcessVisualizer o_ : o){
			o_.onSegmentFinsihed(a, s_t);
		}
	}

	@Override
	public void onIterationFinished(ALNSProcess a, Solution s_t) {
		for(IProcessVisualizer o_ : o){
			o_.onIterationFinished(a, s_t);
		}
	}
	
	

}
