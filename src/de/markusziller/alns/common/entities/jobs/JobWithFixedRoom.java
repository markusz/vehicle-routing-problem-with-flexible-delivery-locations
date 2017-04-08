package de.markusziller.alns.common.entities.jobs;

import de.markusziller.alns.common.entities.Room;

public interface JobWithFixedRoom {
    Room getRoom();

    void setRoom(Room r);
}
