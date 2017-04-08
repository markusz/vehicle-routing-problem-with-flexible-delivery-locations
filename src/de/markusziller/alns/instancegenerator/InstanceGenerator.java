package de.markusziller.alns.instancegenerator;

import de.markusziller.alns.entities.Instance;
import de.markusziller.alns.exceptions.GeneralInfeasibilityException;
import de.markusziller.alns.instancegenerator.imports.SolomonInstance;

public class InstanceGenerator {

    private static InstanceGenerator igen = null;

    private InstanceGenerator() {

    }

    public static InstanceGenerator getInstanceGenerator() {
        EntityPool.initPool();
        if (igen == null) {
            igen = new InstanceGenerator();
        }

        return igen;
    }

    public Instance generateInstance(InstanceConfiguration ic) throws GeneralInfeasibilityException, InterruptedException {
        return new Instance(ic);
    }

    public Instance importInstance(SolomonInstance s, InstanceConfiguration ic) throws GeneralInfeasibilityException {
        return new Instance(s, ic);
    }

}
