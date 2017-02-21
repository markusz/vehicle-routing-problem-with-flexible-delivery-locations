package de.tum.ziller.thesis.thrp.common.exceptions;

import de.tum.ziller.thesis.thrp.common.entities.Job;

public class JobInfeasibilityException extends Exception {
	
	private String uuid;
	private Job job;

	public JobInfeasibilityException(String string, String uuid, Job j) {
		super(string);
		this.uuid = uuid;
		this.job = j;
	}
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public String getUuid() {
        return this.uuid;
    }

    public Job getJob() {
        return this.job;
    }
}
