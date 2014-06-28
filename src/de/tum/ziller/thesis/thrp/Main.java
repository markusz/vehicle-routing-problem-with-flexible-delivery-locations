package de.tum.ziller.thesis.thrp;

import java.io.IOException;

import lombok.SneakyThrows;

import org.dom4j.DocumentException;

import de.tum.ziller.thesis.thrp.common.entities.Instance;
import de.tum.ziller.thesis.thrp.common.entities.Solution;
import de.tum.ziller.thesis.thrp.common.exceptions.GeneralInfeasibilityException;
import de.tum.ziller.thesis.thrp.common.utils.PersistenceUtil;
import de.tum.ziller.thesis.thrp.common.utils.XMLUtil;
import de.tum.ziller.thesis.thrp.heuristic.Solver;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.ALNSConfiguration;
import de.tum.ziller.thesis.thrp.heuristic.strategies.alns.config.IALNSConfig;
import de.tum.ziller.thesis.thrp.heuristic.strategies.phasetwo.ControlParameter;
import de.tum.ziller.thesis.thrp.instancegenerator.EntityPool;
import de.tum.ziller.thesis.thrp.instancegenerator.InstanceConfiguration;
import de.tum.ziller.thesis.thrp.instancegenerator.InstanceGenerator;
import de.tum.ziller.thesis.thrp.instancegenerator.imports.SolomonInstance;

public class Main {

	private static PersistenceUtil pu = new PersistenceUtil();
	
	static String[] SOLOMON_ALL = new String[]{
		"C101","C102","C103","C104","C105","C106","C107","C108","C109","C201","C202","C203","C204","C205","C206","C207","C208",
		"R101","R102","R103","R104","R105","R106","R107","R108","R109","R110","R111","R112","R201","R202","R203","R204","R205","R206","R207","R208","R209","R210","R211",
		"RC101","RC102","RC103","RC104","RC105","RC106","RC107","RC108","RC201","RC202","RC203","RC204","RC205","RC206","RC207","RC208"
		};
	static String[] SOLOMON_CLUSTERED		= new String[]{"C101","C102","C103","C104","C105","C106","C107","C108","C109","C201","C202","C203","C204","C205","C206","C207","C208"};
	static String[] SOLOMON_RANDOM 			= new String[]{"R101","R102","R103","R104","R105","R106","R107","R108","R109","R110","R111","R112","R201","R202","R203","R204","R205","R206","R207","R208","R209","R210","R211",}; 
	static String[] SOLOMON_CLUSTERRANDOM 	= new String[]{"RC101","RC102","RC103","RC104","RC105","RC106","RC107","RC108","RC201","RC202","RC203","RC204","RC205","RC206","RC207","RC208"};
	
	static String[] 	VRPFD_INSTANCES		= new String[]{"C108", "C206", "C203", "R202", "R207", "R104", "RC202", "RC205", "RC208"};
	static double[][] 	params				= new double[][]{new double[]{1.0, 0.3}, new double[]{1.0, 0.8}, new double[]{0.8, 0.3}, new double[]{0.8, 0.8}, new double[]{0.5, 0.3}, new double[]{0.5, 0.8}};
	
	
	
	@SneakyThrows
	public static void main(String args[]) {
		String [] instances = SOLOMON_ALL;
			for (int j = 0; j < instances.length; j=j+1) {
				try {
					
				solveSolomon(
						instances[j], 					//Instanz
						100, 						//Wieviele Customer. 25,50 oder 100
						1.,							//gamma
						.0,							//rho
						ALNSConfiguration.DEFAULT, 	//ALNS Konfiguration
						1, 							//Threads
						new ControlParameter(		
								true, 				//Liniendiagramm der L�sungsqualit�t anzeigen?
								true 				//Liniendiagramm der Operatoren-Wahrscheinlichkeit anzeigen?
								));			
				XMLUtil.backupComputationExperimentResults(""+j);
				} catch (Exception e2) {
					e2.printStackTrace();
					continue;
				}
		}
	}
	
	private static Solution[] solveSolomon(String name, int size, double gamma, double rho, IALNSConfig c, int cores, ControlParameter cp) throws Exception {
		
		EntityPool.initPool();
		
		Solver 					s 		= Solver.getSolver();
		InstanceGenerator 		ig 		= InstanceGenerator.getInstanceGenerator();
		InstanceConfiguration 	ic 		= new InstanceConfiguration();
		
		SolomonInstance			si 		= new SolomonInstance(size, name, gamma, rho);
		
		Instance 				i 		= ig.importInstance(si, ic);
		
		onFinished(i);
		
		Solution is  = s.getInitialSolution(i);
		Solution[] ims = s.improveSolution(is, cores, c, cp);
		
		onFinished(ims);
		
		return ims;
		
	}
	
	private static void onFinished(Object o) throws IOException, DocumentException{
		if(o.getClass().isArray()){
			Solution[] s = (Solution[]) o;
			pu.persistToFilesystem(s);
			XMLUtil.logSolomon(s);
		}
		
		if(o instanceof Solution){
			Solution s = (Solution) o;
			pu.persistToFilesystem(s);
			XMLUtil.logSolomon(s);
		}
		
		if(o instanceof Instance){
			Instance i = (Instance) o;
			pu.persistToFilesystem(i);
			XMLUtil.logSolomon(i);
		}
	}
	
	@SuppressWarnings("unused")
	private static Solution[] solveRandomInstance(int cores, IALNSConfig c) throws GeneralInfeasibilityException, IOException{
		// Initialisiere Entit�tenpool
		EntityPool.initPool();
		// Lade Instanzkonfiguration
		InstanceConfiguration ic = new InstanceConfiguration();
		// Lade Instanzgenerator
		InstanceGenerator ig = InstanceGenerator.getInstanceGenerator();
		Instance i = ig.generateInstance(ic);
		pu.persistToFilesystem(i);
		
		Solver 					s 		= Solver.getSolver();
		
		Solution is  = s.getInitialSolution(i);
		Solution[] ims = s.improveSolution(is, cores, c, new ControlParameter(true, true));
		
		return ims;
	}
}
