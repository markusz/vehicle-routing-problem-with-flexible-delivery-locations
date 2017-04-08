package de.markusziller.alns.entities;

import com.google.common.base.Objects;

import java.io.Serializable;

public class Qualification extends Identifiable implements Serializable {


    private static final long serialVersionUID = 3376111591547911476L;


    private final Integer qHash;

    private String name;

    public Qualification(Integer id) {
        setId(id);
        qHash = new Double(Math.pow(2, getId() - 1)).intValue();
    }

    public Qualification(int id, String name) {
        setId(id);
        this.name = name;
        qHash = new Double(Math.pow(2, getId() - 1)).intValue();
    }


    public Integer getQHash() {
        return qHash;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("id", getId())
                .add("name", name)
                .add("qHash", qHash)
                .toString();
    }

}
