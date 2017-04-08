package de.markusziller.alns.entities;

import java.io.Serializable;


public class Timeslot implements Comparable<Timeslot>, Serializable {

    private static final long serialVersionUID = 1872586515941837298L;
    private Integer start;
    private Integer end;

    public Timeslot(Integer start, Integer end) {

        if (start > end) {
            System.out.println("what");
        }

        this.start = start;
        this.end = end;
    }

    public Integer getLength() {
        return end - start + 1;
    }

    public Timeslot addToEnd(int i) {
        end += i;
        return this;
    }

    public Timeslot addToStart(int i) {
        start += i;
        return this;
    }

    public Timeslot subtractFromEnd(int i) {
        end -= i;
        return this;
    }

    public Timeslot subtractFromStart(int i) {
        start -= i;
        return this;
    }

    public Timeslot[] split(Integer splittime) {
        Timeslot t1 = new Timeslot(start, splittime);
        Timeslot t2 = new Timeslot(splittime + 1, end);

        return new Timeslot[]{t1, t2};
    }

    public Timeslot[] splitInclusive(Timeslot timeslot) {
        Timeslot t1 = new Timeslot(start, timeslot.start - 1);
        Timeslot t2 = new Timeslot(timeslot.start, timeslot.end);
        Timeslot t3 = new Timeslot(timeslot.end + 1, end);

        return new Timeslot[]{t1, t2, t3};
    }

    public Timeslot[] splitExclusive(Timeslot timeslot) {
        Timeslot t1 = new Timeslot(start, timeslot.start - 1);
        Timeslot t2 = new Timeslot(timeslot.end + 1, end);

        return new Timeslot[]{t1, t2};
    }

    @Override
    public String toString() {
        return "[" + start + "," + end + "]";
    }

    @Override
    public int compareTo(Timeslot o) {
        if (this.getStart() > o.getStart()) {
            return 1;
        }
        if (this.getStart() < o.getStart()) {
            return -1;
        }

        return 0;
    }

    public Integer getStart() {
        return this.start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return this.end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }
}
