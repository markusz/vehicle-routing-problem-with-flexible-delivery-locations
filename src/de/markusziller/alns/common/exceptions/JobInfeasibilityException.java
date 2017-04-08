package de.markusziller.alns.common.exceptions;

import de.markusziller.alns.common.entities.Job;

public class JobInfeasibilityException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String uuid;
    private final Job job;


    public JobInfeasibilityException(String string, String uuid, Job j) {
        super(string);
        this.uuid = uuid;
        this.job = j;
    }

    public String getUuid() {
        return this.uuid;
    }

    public Job getJob() {
        return this.job;
    }
}
