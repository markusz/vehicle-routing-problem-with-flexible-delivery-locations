package de.tum.ziller.thesis.thrp.instancegenerator.metrics;

import de.tum.ziller.thesis.thrp.common.entities.Instance;

public interface IMetric {

	public static IMetric[] metrics = new IMetric[] { new ComplexityIndex(), new JobRoomRatio(), new JobTherapistRatio(), new NetworkComplexity(), new OrderStrength(), new ResourceConstrainedness(),
			new ResourceFactor(), new FreeFloatRatio(), new BoundJobRatio(), new JobLengthWorktimeRatio()};

	public String getAbbreviation();

	public Double compute(Instance i) throws Exception;

}
