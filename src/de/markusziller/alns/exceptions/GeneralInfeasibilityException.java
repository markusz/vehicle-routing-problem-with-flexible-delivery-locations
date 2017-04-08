package de.markusziller.alns.exceptions;

public class GeneralInfeasibilityException extends Exception {


    private static final long serialVersionUID = 1L;
    private final String uuid;


    public GeneralInfeasibilityException(String string, String uuid) {
        super(string);
        this.uuid = uuid;
    }

    public String getUuid() {
        return this.uuid;
    }
}
