package de.tum.ziller.thesis.thrp.instancegenerator.metrics;

import de.tum.ziller.thesis.thrp.common.entities.Instance;

public class JobTherapistRatio implements IMetric {

	@Override
	public Double compute(Instance i) {
		return new Double(i.getJobs().size()) / new Double(i.getTherapists().size());
	}
	
	@Override
	public String getAbbreviation() {
		return "JTR";
	}

}
