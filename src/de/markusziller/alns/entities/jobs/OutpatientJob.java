package de.markusziller.alns.entities.jobs;

import de.markusziller.alns.entities.Job;


public class OutpatientJob extends Job implements JobWithoutFixedRoom, TreatmentJob {

    public OutpatientJob(Integer id) {
        setId(id);
    }

    public OutpatientJob(int id, String name) {
        setId(id);
        setName(name);
    }

}
