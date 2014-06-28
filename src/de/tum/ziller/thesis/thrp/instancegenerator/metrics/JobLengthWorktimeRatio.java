package de.tum.ziller.thesis.thrp.instancegenerator.metrics;

import de.tum.ziller.thesis.thrp.common.entities.Instance;
import de.tum.ziller.thesis.thrp.common.entities.Job;
import de.tum.ziller.thesis.thrp.common.entities.Therapist;

public class JobLengthWorktimeRatio implements IMetric {

	@Override
	public String getAbbreviation() {
		return "JLWTR";
	}

	@Override
	public Double compute(Instance i) {

		Double totalWT = 0.;
		Double totalJL = 0.;
		
		for(Therapist t : i.getTherapists()){
			totalWT += (t.getRegularShiftEnd() - t.getShiftStart() - i.getI_conf().getFirstBreakLength());
		}
		
		for(Job j : i.getJobs()){
			totalJL += j.getDurationSlots();
		}
		
		Double avgWT = totalWT / new Double(i.getTherapists().size());
		Double avgJL = totalJL / new Double(i.getJobs().size());
	
		
		return avgJL/avgWT;
		
	}

}
