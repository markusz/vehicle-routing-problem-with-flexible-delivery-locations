package de.tum.ziller.thesis.thrp.common.entities.rooms;

import de.tum.ziller.thesis.thrp.common.entities.Room;

public class BreakRoom extends Room {
	
	public BreakRoom(Integer id){
		setId(id);
	}
	
	public BreakRoom(Integer id, String name){
		setId(id);
		setName(name);
	}

}
