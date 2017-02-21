package de.tum.ziller.thesis.thrp.instancegenerator;

import de.tum.ziller.thesis.thrp.common.entities.Instance;
import de.tum.ziller.thesis.thrp.common.exceptions.GeneralInfeasibilityException;
import de.tum.ziller.thesis.thrp.instancegenerator.imports.SolomonInstance;

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
