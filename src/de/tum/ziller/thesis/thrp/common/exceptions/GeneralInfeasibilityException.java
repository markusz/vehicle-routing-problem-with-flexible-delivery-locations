package de.tum.ziller.thesis.thrp.common.exceptions;

public class GeneralInfeasibilityException extends Exception {
	
	private String uuid;

	public GeneralInfeasibilityException(String string, String uuid) {
		super(string);
		this.uuid = uuid;
	}
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public String getUuid() {
        return this.uuid;
    }
}
