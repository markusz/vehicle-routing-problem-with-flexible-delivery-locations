package de.tum.ziller.thesis.thrp.instancegenerator;

import java.io.Serializable;
import java.util.logging.Logger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.tum.ziller.thesis.thrp.common.entities.jobs.ICUJob;
import de.tum.ziller.thesis.thrp.common.entities.jobs.OutpatientJob;
import de.tum.ziller.thesis.thrp.common.entities.jobs.WardJob;
import de.tum.ziller.thesis.thrp.common.entities.rooms.ICU;
import de.tum.ziller.thesis.thrp.common.entities.rooms.TherapyCenter;
import de.tum.ziller.thesis.thrp.common.entities.rooms.Ward;
import de.tum.ziller.thesis.thrp.common.exceptions.InstanceConfigurationException;
import de.tum.ziller.thesis.thrp.instancegenerator.random.EqualDistribution;
import de.tum.ziller.thesis.thrp.instancegenerator.random.GaussDistribution;
import de.tum.ziller.thesis.thrp.instancegenerator.random.IProbabilityDistribution;
import de.tum.ziller.thesis.thrp.instancegenerator.random.PoissonDistribution;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InstanceConfiguration implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7862267106160181806L;


	private final static Logger log = Logger.getLogger(InstanceConfiguration.class.getName());
	

	private Integer qualificationsQuantity = 4;
	private Integer therapistsQuantity = 22;

	private Integer totalRoomsQuantity = 50;
	private Integer totalJobsQuantity = 120;

	// Rooms
	private Integer ICURoomsQuantity;
	private Integer WardRoomsQuantity;
	private Integer TCRoomsQuantity;

	private Double ICURoomsRatio = 0.16;
	private Double WardRoomsRatio = 0.5;
	private Double TCRoomsRatio = 0.34;

	private Double costPerCell = 1.0;
	
	private Double routingCostFactor = 1.;
	private Double patientTransportCostFactor = .0;

	// Jobs
	private Integer ICUJobsQuantity;
	private Integer WardJobsQuantity;
	private Integer OutpatientJobsQuantity;

	private Double ICUJobsRatio = 0.1;
	private Double WardJobsRatio = 0.5;
	private Double OutpatientJobsRatio = 0.4;

	private Double avgJobsPerWard = (WardJobsRatio * totalJobsQuantity) / (WardRoomsRatio * totalRoomsQuantity);
	private Double avgJobsPerICU = (ICUJobsRatio * totalJobsQuantity) / (ICURoomsRatio * totalRoomsQuantity);
	private Double avgJobsPerTC = (OutpatientJobsRatio * totalJobsQuantity) / (TCRoomsRatio * totalRoomsQuantity);

	

	/**
	 * in minutes
	 */
	private Integer minutesPerTimeslot = 15;
	private Integer minutesPerPlanningHorizon = 24 * 60;

	private Integer numberOfTimeSlots = minutesPerPlanningHorizon / minutesPerTimeslot;

	public Integer getIndexOfLastTimeSlot() {
		return numberOfTimeSlots - 1;
	}
	
	private Integer firstBreakLength_MINUTES = 45;
	private Integer firstBreakFlexibility_MINUTES = 30;
	private Integer secondBreakLength_MINUTES = 15;
	private Integer secondBreakFlexibility_MINUTES = 30;
	
	/**
	 * Min, Avg. Max Jobl�ngen in Minuten pro Job
	 * [0] = ICU, [1] = Ward, [2] = Outpatient
	 */
	private Integer[] JobMinLengths_MINUTES = new Integer[]{Math.max(10, minutesPerTimeslot),Math.max(10, minutesPerTimeslot),Math.max(10, minutesPerTimeslot)};
	private Integer[] JobAvgLengths_MINUTES = new Integer[]{20,20,20};
	private Integer[] JobMaxLengths_MINUTES = new Integer[]{45,45,45};
	
	private Integer firstBreakLength = (int) Math.floor(firstBreakLength_MINUTES.doubleValue() / minutesPerTimeslot.doubleValue());
	private Integer firstBreakFlexibility = (int) Math.floor(firstBreakFlexibility_MINUTES.doubleValue() / minutesPerTimeslot.doubleValue());
	private Integer secondBreakLength = (int) Math.floor(secondBreakLength_MINUTES.doubleValue() / minutesPerTimeslot.doubleValue());
	private Integer secondBreakFlexibility = (int) Math.floor(secondBreakFlexibility_MINUTES.doubleValue() / minutesPerTimeslot.doubleValue());
	
	
	/**
	 * Min, Avg. Max Jobl�ngen in Timeslots pro Job
	 * [0] = ICU, [1] = Ward, [2] = Outpatient
	 */
	private Integer[] JobMinLengths_TS = new Integer[]{
			(int) Math.floor(JobMinLengths_MINUTES[0].doubleValue() / minutesPerTimeslot.doubleValue()),
			(int) Math.floor(JobMinLengths_MINUTES[1].doubleValue() / minutesPerTimeslot.doubleValue()),
			(int) Math.floor(JobMinLengths_MINUTES[2].doubleValue() / minutesPerTimeslot.doubleValue())};
	
	private Integer[] JobAvgLengths_TS = new Integer[]{
			(int) Math.floor(JobAvgLengths_MINUTES[0].doubleValue() / minutesPerTimeslot.doubleValue()),
			(int) Math.floor(JobAvgLengths_MINUTES[1].doubleValue() / minutesPerTimeslot.doubleValue()),
			(int) Math.floor(JobAvgLengths_MINUTES[2].doubleValue() / minutesPerTimeslot.doubleValue())};
	
	private Integer[] JobMaxLengths_TS = new Integer[]{
			(int) Math.floor(JobMaxLengths_MINUTES[0].doubleValue() / minutesPerTimeslot.doubleValue()),
			(int) Math.floor(JobMaxLengths_MINUTES[1].doubleValue() / minutesPerTimeslot.doubleValue()),
			(int) Math.floor(JobMaxLengths_MINUTES[2].doubleValue() / minutesPerTimeslot.doubleValue())};
	
	
	
	
//	private Integer ICUJobMinLength_MINUTES = 10;
//	private Integer ICUJobMaxLength_MINUTES = 45;
//	private Integer WardJobMinLength_MINUTES = 10;
//	private Integer WardJobMaxLength_MINUTES = 45;
//	private Integer OutpatientJobMinLength_MINUTES = 10;
//	private Integer OutpatientJobMaxLength_MINUTES = 45;
//	private Integer ICUJobMinLength_TS = (int) Math.floor(ICUJobMinLength_MINUTES.doubleValue() / minutesPerTimeslot.doubleValue());;
//	private Integer ICUJobMaxLength_TS = (int) Math.floor(ICUJobMaxLength_MINUTES.doubleValue() / minutesPerTimeslot.doubleValue());
//	private Integer OutpatientJobMinLength_TS = (int) Math.floor(OutpatientJobMinLength_MINUTES.doubleValue() / minutesPerTimeslot.doubleValue());;
//	private Integer OutpatientJobMaxLength_TS = (int) Math.floor(OutpatientJobMaxLength_MINUTES.doubleValue() / minutesPerTimeslot.doubleValue());
//	private Integer WardJobMinLength_TS = (int) Math.floor(WardJobMinLength_MINUTES.doubleValue() / minutesPerTimeslot.doubleValue());;
//	private Integer WardJobMaxLength_TS = (int) Math.floor(WardJobMaxLength_MINUTES.doubleValue() / minutesPerTimeslot.doubleValue());

	private Integer[] possibleShiftStartingSlots = new Integer[] { 0 * 60 / minutesPerTimeslot, 4 * 60 / minutesPerTimeslot, 8 * 60 / minutesPerTimeslot, 12 * 60 / minutesPerTimeslot, 16 * 60 / minutesPerTimeslot };
	private Integer[] possibleShiftLengths = new Integer[] { 4 * 60 / minutesPerTimeslot, 8 * 60 / minutesPerTimeslot };
	
	private transient IProbabilityDistribution qualificationToTherapistAssignment = new PoissonDistribution(1.5);
	private transient IProbabilityDistribution qualificationToJobAssignment = new PoissonDistribution(1.0);
	private transient IProbabilityDistribution timeslotToJobAssignment = new PoissonDistribution(2. + Math.random() * 2.);
	private transient IProbabilityDistribution shiftStartAssignment = new EqualDistribution();
	private transient IProbabilityDistribution shiftDurationAssignment = new GaussDistribution(0.0, 1.3);

	@SuppressWarnings("rawtypes")
	Multimap<Class, Class> roomToJobAssignment = initRoomToJobAssignment();

	public void validateConfiguration() throws InstanceConfigurationException {
		if (ICURoomsRatio + TCRoomsRatio + WardRoomsRatio < 0.999999 || ICURoomsRatio + TCRoomsRatio + WardRoomsRatio > 1.00000001) {
			throw new InstanceConfigurationException("Room-Ratios don't sum up to 1");
		}
		
		if (ICUJobsRatio + OutpatientJobsRatio + WardJobsRatio < 0.999999 || ICUJobsRatio + OutpatientJobsRatio + WardJobsRatio > 1.00000001) {
			throw new InstanceConfigurationException("Job-Ratios don't sum up to 1");
		}
		
		if(totalJobsQuantity > 3000){
			throw new InstanceConfigurationException("More than 3000 Jobs are not supported");
		}
		
		if(totalRoomsQuantity > 3000){
			throw new InstanceConfigurationException("More than 3000 Rooms are not supported");
		}
		
		if(totalRoomsQuantity > 3000){
			throw new InstanceConfigurationException("More than 3000 Rooms are not supported");
		}
		
		for (int i = 0; i < JobMinLengths_MINUTES.length; i++) {
			if(JobMinLengths_MINUTES[i] < minutesPerTimeslot){
				throw new InstanceConfigurationException("Min Length of Jobs is smaller than Timeslot size. This causes problems. Please fix");
			}
		}

		for (int i = 0; i < JobAvgLengths_MINUTES.length; i++) {
			if(JobAvgLengths_MINUTES[i] < minutesPerTimeslot){
				throw new InstanceConfigurationException("Avg Length of Jobs is smaller than Timeslot size. This causes problems. Please fix");
			}
		}

		for (int i = 0; i < JobMaxLengths_MINUTES.length; i++) {
			if(JobMaxLengths_MINUTES[i] < minutesPerTimeslot){
				throw new InstanceConfigurationException("Max Length of Jobs is smaller than Timeslot size. This causes problems. Please fix");
			}
		}
		
		if(avgJobsPerWard < 1.0 || avgJobsPerWard > 4.0 ){
			log.warning("Average Jobs per Ward is "+avgJobsPerWard+". Typically this metric should be between 1 and 4.");
		}
		
		if(avgJobsPerTC < 8.0){
			log.warning("Average Jobs per TC is "+avgJobsPerTC+". Typically this metric should be > 8.");
		}
		
		if(avgJobsPerICU > 1.5){
			log.warning("Average Jobs per ICU is "+avgJobsPerICU+". Typically this metric should be < 1,5.");
		}
		
		if((ICUJobsRatio * totalJobsQuantity) > (ICURoomsRatio * totalRoomsQuantity)){
			log.warning("Not enough ICU-Rooms ("+Math.floor(ICURoomsRatio * totalRoomsQuantity)+") for "+Math.floor(ICUJobsRatio * totalJobsQuantity)+" ICU Jobs");
//			throw new InstanceConfigurationException("Not enough ICU-Rooms ("+Math.floor(ICURoomsRatio * totalRoomsQuantity)+") for "+Math.floor(ICUJobsRatio * totalJobsQuantity)+" ICU Jobs");
		}
	}

	@SuppressWarnings("rawtypes")
	private Multimap<Class, Class> initRoomToJobAssignment() {
		Multimap<Class, Class> map = HashMultimap.create();

		map.put(WardJob.class, Ward.class);
		map.put(WardJob.class, TherapyCenter.class);
		map.put(ICUJob.class, ICU.class);
		map.put(OutpatientJob.class, TherapyCenter.class);

		return map;
	}
	
	
	/**
	 * *********************************
	 * 
	 * IMPORT SETTINGS
	 * 
	 * *********************************
	 */
	
//	private double gamma = 1.0;
	private boolean useQ = false;
}
