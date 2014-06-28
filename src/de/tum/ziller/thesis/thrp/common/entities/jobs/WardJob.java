package de.tum.ziller.thesis.thrp.common.entities.jobs;

import lombok.Getter;
import lombok.Setter;
import de.tum.ziller.thesis.thrp.common.entities.Job;
import de.tum.ziller.thesis.thrp.common.entities.Room;

public class WardJob extends Job implements JobWithFixedRoom, TreatmentJob{
	
	private @Getter @Setter Room room;

	
	public WardJob(Integer id){
		setId(id);
	}

	public WardJob(int id, String name) {
		setId(id);
		setName(name);
	}

}
