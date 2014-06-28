package de.tum.ziller.thesis.thrp.common.exceptions;

import lombok.Getter;

public class GeneralInfeasibilityException extends Exception {
	
	private @Getter String uuid;

	public GeneralInfeasibilityException(String string, String uuid) {
		super(string);
		this.uuid = uuid;
	}
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
