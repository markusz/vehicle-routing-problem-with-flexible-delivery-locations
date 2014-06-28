package de.tum.ziller.thesis.thrp.common.entities;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import com.google.common.base.Objects;

import de.tum.ziller.thesis.thrp.common.abstraction.Identifiable;

public abstract class Room extends Identifiable implements Serializable{

	private static final long serialVersionUID = 144471639227785745L;
	private String name;
	
	@Getter @Setter int  x;
	@Getter @Setter int y;
	@Getter @Setter int k;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return Objects.toStringHelper(this.getClass()).add("id", getId())
	            .add("x,y", x+","+y)
	            .toString();
	}

}
