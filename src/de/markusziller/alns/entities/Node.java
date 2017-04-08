package de.markusziller.alns.entities;

import de.markusziller.alns.entities.jobs.IdleJob;
import de.markusziller.alns.entities.jobs.JobWithFixedRoom;
import de.markusziller.alns.entities.jobs.TreatmentJob;
import de.markusziller.alns.entities.rooms.BreakRoom;

public class Node implements Comparable<Node> {

    private String metaData;
    private Room room;
    private Job job;
    private Timeslot time;

    public Node(String metaData) {
        this.metaData = metaData;
    }

    @java.beans.ConstructorProperties({"metaData", "room", "job", "time"})
    public Node(String metaData, Room room, Job job, Timeslot time) {
        this.metaData = metaData;
        this.room = room;
        this.job = job;
        this.time = time;
    }

    public Node() {
    }

    public Timeslot getTime() {
        return time;
    }

    public void setTime(Timeslot ts) {
        this.time = ts;
    }


    public boolean hasJobTransfer() {
        return !(job instanceof JobWithFixedRoom) || ((JobWithFixedRoom) job).getRoom() != room;
    }

    public Integer getStart() {
        return time.getStart();
    }

    public void setStart(Integer i) {
        time.setStart(i);
    }

    public Integer getEnd() {
        return time.getEnd();
    }

    public void setEnd(Integer i) {
        time.setEnd(i);
    }

    @Override
    public String toString() {
        String sb = "[" +
                getStart() +
                "," +
                getEnd() +
                "]: " +
                job.getName() +
                ", " +
                room.getName();

        return sb;
    }

    //	@Override
//	public int compareTo(Node o) {
//		if (this.getFromTo().getStart() > o.getFromTo().getStart()) {
//			return 1;
//		}
//		if (this.getFromTo().getStart() < o.getFromTo().getStart()) {
//			return -1;
//		} else {
//			return 0;
//		}
//	}
    public boolean isTreatment() {
        return !(getJob() instanceof IdleJob) && !(getRoom() instanceof BreakRoom);
    }

    public boolean isIdleJobInTreatmentRoom() {
        return (getJob() instanceof IdleJob) && !(getRoom() instanceof BreakRoom);
    }

    @Override
    public int compareTo(Node o) {

        //
        if (this == o) {
            return 0;
        }
        //Beides sind keine Behandlungsjobs, haben aber den gleichen Typ (z.B. zwei Idle jobs)
        if (!(this instanceof TreatmentJob) && !(o instanceof TreatmentJob) && (this.getClass() == o.getClass())) {
            //Start ist gleich
            if (this.getTime().getStart() == o.getTime().getStart()) {
                if (this.getTime().getEnd() == o.getTime().getEnd()) {
                    return 0;
                } else {
                    if (this.getTime().getEnd() > o.getTime().getEnd()) {
                        return 1;
                    }
                    return -1;
                }
            }

            //start ist nicht gleich. > bedeutet sp�ter in der liste -> -1
            if (this.getTime().getStart() > o.getTime().getStart()) {
                return 1;
            }
            return -1;
        }

        //jobs sind gleich -> 0
        if (this.getJob().getId() == o.getJob().getId()) {
            return 0;
        } else {

            if (this.getTime().getStart() > o.getTime().getStart()) {
                return 1;
            }
            return -1;
        }
    }

    public boolean isIdle() {
        return job instanceof IdleJob;
    }

    public String getMetaData() {
        return this.metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Job getJob() {
        return this.job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
