package de.tum.ziller.thesis.thrp.common.entities.jobs;

import de.tum.ziller.thesis.thrp.common.entities.Job;
import de.tum.ziller.thesis.thrp.common.entities.Room;

public class ICUJob extends Job implements JobWithFixedRoom, TreatmentJob{
	
	private Room room;

	public ICUJob(Integer id){
		setId(id);
	}

	public ICUJob(int id, String name) {
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
