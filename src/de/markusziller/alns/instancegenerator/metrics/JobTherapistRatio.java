package de.markusziller.alns.instancegenerator.metrics;

import de.markusziller.alns.entities.Instance;

public class JobTherapistRatio implements IMetric {

    @Override
    public Double compute(Instance i) {
        return (double) i.getJobs().size() / (double) i.getTherapists().size();
    }

    @Override
    public String getAbbreviation() {
        return "JTR";
    }

}
