package de.markusziller.alns.common.entities;

import com.google.common.base.Objects;
import de.markusziller.alns.common.abstraction.Identifiable;

import java.io.Serializable;

public class Qualification extends Identifiable implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3376111591547911476L;

    /**
     * Bin�res Z�hlen identifiziert jede Qualifikationskombi eindeutig -> Kein Iterieren n�tig -> bis zu 32 Q bei Integern m�glich
     */
    private Integer qHash;

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

    /**
     * @return
     * @author Markus Z.
     * @date 10.06.2013
     */
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
