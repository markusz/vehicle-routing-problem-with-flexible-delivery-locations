package de.tum.ziller.thesis.thrp.instancegenerator.metrics;

import de.tum.ziller.thesis.thrp.common.entities.Instance;

public class JobRoomRatio implements IMetric {

	@Override
	public Double compute(Instance i) {
		return new Double(i.getJobs().size()) / new Double(i.getRooms().size());
	}
	
	@Override
	public String getAbbreviation() {
		return "JRR";
	}

}
