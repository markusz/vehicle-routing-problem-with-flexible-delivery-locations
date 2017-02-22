package de.markusziller.alns.instancegenerator.metrics;

import de.markusziller.alns.common.entities.Instance;

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
