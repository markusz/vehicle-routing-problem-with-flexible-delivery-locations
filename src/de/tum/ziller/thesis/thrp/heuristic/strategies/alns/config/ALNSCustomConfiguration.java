package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ALNSCustomConfiguration implements IALNSConfig {
	
	private final int omega;
	private final int tau;
	private final double r_p;
	private final int sigma_1;
	private final int sigma_2;
	private final int sigma_3;
	private final double c;
	private final double delta;
	private final double big_omega;

}
