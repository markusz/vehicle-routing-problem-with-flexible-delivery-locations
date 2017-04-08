package de.markusziller.alns.instancegenerator.metrics;

import de.markusziller.alns.entities.Instance;

public class JobRoomRatio implements IMetric {

    @Override
    public Double compute(Instance i) {
        return (double) i.getJobs().size() / (double) i.getRooms().size();
    }

    @Override
    public String getAbbreviation() {
        return "JRR";
    }

}
