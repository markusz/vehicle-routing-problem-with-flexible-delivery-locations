package de.markusziller.alns.entities.rooms;

import de.markusziller.alns.entities.Room;

public class TherapyCenter extends Room {

    public TherapyCenter(Integer id) {
        setId(id);
    }

    public TherapyCenter(Integer id, String name) {
        setId(id);
        setName(name);
    }

}
