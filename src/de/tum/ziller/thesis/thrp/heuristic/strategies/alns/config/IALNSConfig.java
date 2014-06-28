package de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config;

public interface IALNSConfig {

	public abstract int getOmega();
	public abstract int getTau();
	public abstract double getR_p();
	public abstract int getSigma_1();
	public abstract int getSigma_2();
	public abstract int getSigma_3();
	public abstract double getC();
	public abstract double getDelta();
	public abstract double getBig_omega();

}