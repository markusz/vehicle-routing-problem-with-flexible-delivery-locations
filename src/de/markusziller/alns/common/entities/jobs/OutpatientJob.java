package de.markusziller.alns.common.entities.jobs;

import de.markusziller.alns.common.entities.Job;


public class OutpatientJob extends Job implements JobWithoutFixedRoom, TreatmentJob {

    public OutpatientJob(Integer id) {
        setId(id);
    }

    public OutpatientJob(int id, String name) {
        setId(id);
        setName(name);
    }

}
