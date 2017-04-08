package de.markusziller.alns.entities.jobs;

import de.markusziller.alns.entities.Room;

public interface JobWithFixedRoom {
    Room getRoom();

    void setRoom(Room r);
}
