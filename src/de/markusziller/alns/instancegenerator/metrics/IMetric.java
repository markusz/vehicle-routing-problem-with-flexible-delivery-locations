package de.markusziller.alns.instancegenerator.metrics;

import de.markusziller.alns.entities.Instance;

public interface IMetric {

    IMetric[] metrics = new IMetric[]{new ComplexityIndex(), new JobRoomRatio(), new JobTherapistRatio(), new NetworkComplexity(), new OrderStrength(), new ResourceConstrainedness(),
            new ResourceFactor(), new FreeFloatRatio(), new BoundJobRatio(), new JobLengthWorktimeRatio()};

    String getAbbreviation();

    Double compute(Instance i);

}
