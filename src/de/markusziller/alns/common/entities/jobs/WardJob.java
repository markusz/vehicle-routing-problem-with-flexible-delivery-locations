package de.markusziller.alns.common.entities.jobs;

import de.markusziller.alns.common.entities.Job;
import de.markusziller.alns.common.entities.Room;

public class WardJob extends Job implements JobWithFixedRoom, TreatmentJob {

    private Room room;


    public WardJob(Integer id) {
        setId(id);
    }

    public WardJob(int id, String name) {
        setId(id);
        setName(name);
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
