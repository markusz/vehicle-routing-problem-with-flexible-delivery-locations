package de.markusziller.alns.instancegenerator.metrics;

import de.markusziller.alns.entities.Instance;
import de.markusziller.alns.entities.Job;
import lombok.SneakyThrows;

public class BoundJobRatio implements IMetric {

    @Override
    public String getAbbreviation() {
        // TODO Auto-generated method stub
        return "BJR";
    }

    @Override
    @SneakyThrows
    public Double compute(Instance i) {

        Double cnt = 0.;

        for (Job j : i.getJobs()) {
            if (i.getProficientTherapists(j).size() == 1) {
                cnt++;
            }
        }
        // TODO Auto-generated method stub
        return cnt / (double) i.getJobs().size();
    }

}
