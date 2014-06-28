package de.tum.ziller.thesis.thrp.common.entities;

import com.google.common.base.Objects;
import de.tum.ziller.thesis.thrp.common.abstraction.Identifiable;

import java.io.Serializable;

public class Qualification extends Identifiable implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3376111591547911476L;

	/**
	 * Bin�res Z�hlen identifiziert jede Qualifikationskombi eindeutig -> Kein Iterieren n�tig -> bis zu 32 Q bei Integern m�glich
	 */
	private Integer				qHash;

	private String				name;

	/**
	 * 
	 * @author Markus Z.
	 * @date 10.06.2013
	 * @return
	 *
	 */
	public Integer getQHash() {
		return qHash;
	}
	
	public Qualification(Integer id){
		setId(id);
		qHash = new Double(Math.pow(2, getId() - 1)).intValue();
	}

	public Qualification(int id, String name) {
		setId(id);
		this.name = name;
		qHash = new Double(Math.pow(2, getId() - 1)).intValue();
	}
	
	@Override
	public String toString(){
		 return Objects.toStringHelper(this.getClass()).add("id", getId())
		            .add("name", name)
		            .add("qHash", qHash)
		            .toString();
	}

}
