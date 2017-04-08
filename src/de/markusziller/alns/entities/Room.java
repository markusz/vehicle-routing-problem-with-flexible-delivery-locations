package de.markusziller.alns.entities;

import com.google.common.base.Objects;

import java.io.Serializable;

public abstract class Room extends Identifiable implements Serializable {

    private static final long serialVersionUID = 144471639227785745L;
    private int x;
    private int y;
    private int k;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("id", getId())
                .add("x,y", x + "," + y)
                .toString();
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getK() {
        return this.k;
    }

    public void setK(int k) {
        this.k = k;
    }
}
