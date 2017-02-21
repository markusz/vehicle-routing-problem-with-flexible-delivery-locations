package de.tum.ziller.thesis.thrp.common.entities;

import com.google.common.base.Objects;
import de.tum.ziller.thesis.thrp.common.abstraction.Identifiable;

import java.io.Serializable;

public abstract class Room extends Identifiable implements Serializable{

	private static final long serialVersionUID = 144471639227785745L;
	private String name;
	
	int  x;
	int y;
	int k;

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

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getK() {
        return this.k;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setK(int k) {
        this.k = k;
    }
}
