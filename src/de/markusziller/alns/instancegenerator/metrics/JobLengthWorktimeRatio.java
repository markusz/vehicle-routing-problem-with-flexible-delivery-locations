package de.markusziller.alns.instancegenerator.metrics;

import de.markusziller.alns.entities.Instance;
import de.markusziller.alns.entities.Job;
import de.markusziller.alns.entities.Therapist;

public class JobLengthWorktimeRatio implements IMetric {

    @Override
    public String getAbbreviation() {
        return "JLWTR";
    }

    @Override
    public Double compute(Instance i) {

        Double totalWT = 0.;
        Double totalJL = 0.;

        for (Therapist t : i.getTherapists()) {
            totalWT += (t.getRegularShiftEnd() - t.getShiftStart() - i.getI_conf().getFirstBreakLength());
        }

        for (Job j : i.getJobs()) {
            totalJL += j.getDurationSlots();
        }

        Double avgWT = totalWT / (double) i.getTherapists().size();
        Double avgJL = totalJL / (double) i.getJobs().size();


        return avgJL / avgWT;

    }

}
