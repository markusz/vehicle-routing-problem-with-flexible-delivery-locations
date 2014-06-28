package de.tum.ziller.thesis.thrp.common.exceptions;

import lombok.Getter;
import de.tum.ziller.thesis.thrp.common.entities.Job;

public class JobInfeasibilityException extends Exception {
	
	private @Getter String uuid;
	private @Getter Job job;

	public JobInfeasibilityException(String string, String uuid, Job j) {
		super(string);
		this.uuid = uuid;
		this.job = j;
	}
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
