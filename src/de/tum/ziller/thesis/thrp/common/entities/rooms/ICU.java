package de.tum.ziller.thesis.thrp.common.entities.rooms;

import de.tum.ziller.thesis.thrp.common.entities.Room;

public class ICU extends Room {
	
	public ICU(Integer id){
		setId(id);
	}
	
	public ICU(Integer id, String name){
		setId(id);
		setName(name);
	}

}
