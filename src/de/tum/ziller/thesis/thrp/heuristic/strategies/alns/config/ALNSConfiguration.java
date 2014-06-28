package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config;


public enum ALNSConfiguration implements IALNSConfig {
	//Name											(i,			tau,	r_p,	s1,	s2,	s3,	c,				delta,	omega),
	DEFAULT											(25000,		100,	0.1,	1,	0,	5,	0.99937,		0.05,	0.5),
	;
	
	
	
	
	
	/* (non-Javadoc)
	 * @see de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig#getOmega()
	 */
	@Override
	public int getOmega() {
		return omega;
	}

	/* (non-Javadoc)
	 * @see de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig#getTau()
	 */
	@Override
	public int getTau() {
		return tau;
	}

	/* (non-Javadoc)
	 * @see de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig#getR_p()
	 */
	@Override
	public double getR_p() {
		return r_p;
	}

	/* (non-Javadoc)
	 * @see de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig#getSigma_1()
	 */
	@Override
	public int getSigma_1() {
		return sigma_1;
	}

	/* (non-Javadoc)
	 * @see de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig#getSigma_2()
	 */
	@Override
	public int getSigma_2() {
		return sigma_2;
	}

	/* (non-Javadoc)
	 * @see de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig#getSigma_3()
	 */
	@Override
	public int getSigma_3() {
		return sigma_3;
	}

	/* (non-Javadoc)
	 * @see de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig#getC()
	 */
	@Override
	public double getC() {
		return c;
	}

	/* (non-Javadoc)
	 * @see de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig#getDelta()
	 */
	@Override
	public double getDelta() {
		return delta;
	}

	/* (non-Javadoc)
	 * @see de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig#getBig_omega()
	 */
	@Override
	public double getBig_omega() {
		return big_omega;
	}

	public final int omega;
	public final int tau;
	public final double r_p;
	public final int sigma_1;
	public final int sigma_2;
	public final int sigma_3;
	public final double c;
	public final double delta;
	public final double big_omega;
	
	ALNSConfiguration(int omega, int tau, double r_p, int sigma_1, int sigma_2, int sigma_3, double c, double delta, double big_omega){
		this.omega = omega;
		this.tau = tau;
		this.r_p = r_p;
		this.sigma_1 = sigma_1;
		this.sigma_2 = sigma_2;
		this.sigma_3 = sigma_3;
		this.c = c;
		this.delta = delta;
		this.big_omega = big_omega;
	}
	
}
