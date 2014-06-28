package de.tum.ziller.thesis.thrp.common.entities.jobs;

import lombok.Getter;
import lombok.Setter;
import de.tum.ziller.thesis.thrp.common.entities.Job;
import de.tum.ziller.thesis.thrp.common.entities.Room;

public class ICUJob extends Job implements JobWithFixedRoom, TreatmentJob{
	
	private @Getter @Setter Room room;

	public ICUJob(Integer id){
		setId(id);
	}

	public ICUJob(int id, String name) {
		setId(id);
		setName(name);
		
	}
	
}
