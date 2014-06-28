package de.tum.ziller.thesis.thrp.common.entities.jobs;

import de.tum.ziller.thesis.thrp.common.entities.Room;

public interface JobWithFixedRoom {
	
	
	public Room getRoom();
	public void setRoom(Room r);

}
