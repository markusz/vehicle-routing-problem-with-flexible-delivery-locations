package de.markusziller.alns.entities;

import java.io.Serializable;

public abstract class Identifiable implements Serializable {
    private static final long serialVersionUID = 3841102840793186996L;
    private Integer id;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
