package de.tum.ziller.thesis.thrp.common.entities.rooms;

import de.tum.ziller.thesis.thrp.common.entities.Room;

public class TherapyCenter extends Room {
	
	public TherapyCenter(Integer id){
		setId(id);
	}
	
	public TherapyCenter(Integer id, String name){
		setId(id);
		setName(name);
	}
	
}
