package de.markusziller.alns.instancegenerator;

import de.markusziller.alns.entities.Identifiable;
import de.markusziller.alns.entities.*;
import de.markusziller.alns.entities.jobs.ICUJob;
import de.markusziller.alns.entities.jobs.OutpatientJob;
import de.markusziller.alns.entities.jobs.WardJob;
import de.markusziller.alns.entities.rooms.BreakRoom;
import de.markusziller.alns.entities.rooms.ICU;
import de.markusziller.alns.entities.rooms.TherapyCenter;
import de.markusziller.alns.entities.rooms.Ward;
import de.markusziller.alns.instancegenerator.random.IProbabilityDistribution;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class EntityPool {
    private static final String[] therapistFirstNames = new String[]{"Andreas", "Bernd", "Christian", "Daniela", "Elke", "Friedrich", "Gerda", "Hubert", "Inge", "Johannes", "Karl",
            "Lukas", "Markus", "Nadine", "Olaf", "Peter", "Quentin", "Rosa", "Stefanie", "Thomas", "Udo", "Verena", "Walter", "Xaver", "Yvonne", "Zenta"};
    private static final String[] therapistSecondNames = new String[]{"A.", "B.", "C.", "D.", "E.", "F.", "G.", "H.", "I.", "J.", "K.", "L.", "M.", "N.", "O.", "P.", "Q.", "R.", "S.",
            "T.", "U.", "V.", "W.", "X.", "Y.", "Z."};
    private static final String[] qualifications = new String[]{"Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};
    private static final Room breakroom = new BreakRoom(0, "BreakRoom");
    private static List<Therapist> therapistsMaster = null;
    private static List<Qualification> qualificationMaster = null;
    private static List<ICU> ICURoomMaster = null;
    private static List<Ward> WARDRoomMaster = null;
    private static List<TherapyCenter> TCRoomMaster = null;
    private static List<Room> genericRoomMaster = null;
    private static List<ICUJob> ICUJobMaster = null;
    private static List<WardJob> WARDJobMaster = null;
    private static List<OutpatientJob> OutpatientJobMaster = null;
    private static List<Job> genericJobMaster = null;


    public static void initPool() {
        therapistsMaster = new ArrayList<>();
        qualificationMaster = new ArrayList<>();
        ICURoomMaster = new ArrayList<>();
        TCRoomMaster = new ArrayList<>();
        WARDRoomMaster = new ArrayList<>();
        genericRoomMaster = new ArrayList<>();
        ICUJobMaster = new ArrayList<>();
        OutpatientJobMaster = new ArrayList<>();
        WARDJobMaster = new ArrayList<>();
        genericJobMaster = new ArrayList<>();
        int id = 1;
        for (String s : therapistFirstNames) {
            for (String s2 : therapistSecondNames) {
                therapistsMaster.add(new Therapist(id, s.concat(" ").concat(s2)));
                id++;
            }
        }
        id = 1;
        for (String s : qualifications) {
            qualificationMaster.add(new Qualification(id, s));
            id++;
        }
        id = 1;
        Integer maxRooms = 3000;
        for (int i = id; i <= maxRooms; i++) {
            String s = "00" + i;
            String rNumber = s.substring(s.length() - 3, s.length());
            if (i <= maxRooms / 3) {
                ICURoomMaster.add(new ICU(id, "I".concat(rNumber)));
            }
            if (i > maxRooms / 3 && i <= maxRooms / 3 * 2) {
                WARDRoomMaster.add(new Ward(id, "W".concat(rNumber)));
            }
            if (i > maxRooms / 3 * 2) {
                TCRoomMaster.add(new TherapyCenter(id, "T".concat(rNumber)));
            }
            id++;
        }
        genericRoomMaster.addAll(ICURoomMaster);
        genericRoomMaster.addAll(WARDRoomMaster);
        genericRoomMaster.addAll(TCRoomMaster);
        id = 1;
        Integer maxJobs = 3000;
        for (int i = id; i <= maxJobs; i++) {
            String s = "000" + i;
            String rNumber = s.substring(s.length() - 4, s.length());
            if (i <= maxJobs / 3) {
                ICUJobMaster.add(new ICUJob(id, "ICU-Job:".concat(rNumber)));
            }
            if (i > maxJobs / 3 && i <= maxJobs / 3 * 2) {
                WARDJobMaster.add(new WardJob(id, "Ward-Job:".concat(rNumber)));
            }
            if (i > maxJobs / 3 * 2) {
                OutpatientJobMaster.add(new OutpatientJob(id, "Outpatient-Job:".concat(rNumber)));
            }
            id++;
        }
        genericJobMaster.addAll(ICUJobMaster);
        genericJobMaster.addAll(WARDJobMaster);
        genericJobMaster.addAll(OutpatientJobMaster);
    }


    public static List<Therapist> getNTherapists(Integer n) {
        return getNTherapists(n, new InstanceConfiguration());
    }

    private static void assignShiftStartsAndDurations(List<Therapist> temp, InstanceConfiguration ic) {
        Integer highestIndex = ic.getPossibleShiftStartingSlots().length - 1;
        Integer highestIndex2 = ic.getPossibleShiftLengths().length - 1;
        IProbabilityDistribution ipd = ic.getShiftStartAssignment();
        Integer maxTimeSlot = ic.getIndexOfLastTimeSlot();
        for (Therapist tp : temp) {
            Integer shiftStart = ic.getPossibleShiftStartingSlots()[ipd.getQuantity(0, highestIndex)];
            tp.setShiftStart(shiftStart);
            Integer shiftDuration = ic.getPossibleShiftLengths()[ipd.getQuantity(0, highestIndex2)];
            tp.setRegularShiftEnd(Math.min(shiftDuration + tp.getShiftStart(), maxTimeSlot));
        }
    }


    private static List<Therapist> getNTherapists(Integer n, InstanceConfiguration ic) {
        List<Therapist> temp = getNorAll(n, therapistsMaster);
        assignShiftStartsAndDurations(temp, ic);
        assignPauseTimeslots(temp, ic);
        return temp;
    }

    private static void assignPauseTimeslots(List<Therapist> temp, InstanceConfiguration ic) {
        for (Therapist therapist : temp) {
            Integer f_middle = (therapist.getShiftStart() + therapist.getRegularShiftEnd()) / 2;
            Integer f_shiftEnd = f_middle + ic.getFirstBreakLength();
            Integer f_earliest = f_middle - ic.getFirstBreakFlexibility();
            Integer f_latest = f_shiftEnd + ic.getFirstBreakFlexibility();
            therapist.setFirstPauseRange(new Timeslot(f_earliest, f_latest));
            therapist.setEarliestFirstPauseStart(f_earliest);
            therapist.setLatestFirstPauseStart(f_latest - ic.getFirstBreakLength());
        }
    }


    public static List<Qualification> getNQualifications(int n) {
        return getNorAll(n, qualificationMaster);
    }


    public static List<Ward> getNWardRooms(int n) {
        return getNorAll(n, WARDRoomMaster);
    }


    public static List<ICU> getNICURooms(int n) {
        return getNorAll(n, ICURoomMaster);
    }


    public static List<TherapyCenter> getNTCRooms(int n) {
        return getNorAll(n, TCRoomMaster);
    }


    public static List<Room> getNAllRooms(int n) {
        List<Room> rooms = getNorAll(n, genericRoomMaster);
        rooms.add(breakroom);
        return rooms;
    }


    public static List<Room> getNAllRoomsWithoutBreakroom(int n) {
        List<Room> rooms = getNorAll(n, genericRoomMaster);
        return rooms;
    }


    @SneakyThrows
    public static List<Room> getNAllRooms(int n, Double ratioWARD, Double ratioICU, Double ratioTC) {
        int nWard = new Double(n * ratioWARD).intValue();
        int nICU = new Double(n * ratioICU).intValue();
        int nTC = new Double(n * ratioTC).intValue();
        if (nWard + nICU + nTC < n) {
            nWard = nWard - (nWard + nICU + nTC - n);
        }
        if (nWard + nICU + nTC > n) {
            nWard = nWard + (n - nWard + nICU + nTC);
        }
        List<Room> all = new ArrayList<>();
        all.addAll(getNWardRooms(nWard));
        all.addAll(getNICURooms(nICU));
        all.addAll(getNTCRooms(nTC));
        for (int i = 0; i < all.size(); i++) {
            Room r = all.get(i);
            r.setId(i + 1);
        }
        all.add(breakroom);
        return all;
    }


    public static List<WardJob> getNWardJobs(int n) {
        List<WardJob> I_ward = getNorAll(n, WARDJobMaster);
        return I_ward;
    }


    public static List<ICUJob> getNICUJobs(int n) {
        List<ICUJob> I_icu = getNorAll(n, ICUJobMaster);
        return I_icu;
    }


    public static List<OutpatientJob> getNOutpatientJobs(int n) {
        List<OutpatientJob> I_out = getNorAll(n, OutpatientJobMaster);
        return I_out;
    }


    public static List<Job> getNAllJobs(int n) {
        return getNorAll(n, genericJobMaster);
    }


    @SneakyThrows
    public static List<Job> getNAllJobs(int n, Double ratioWARD, Double ratioICU, Double ratioOutpatient) {
        int nWard = new Double(n * ratioWARD).intValue();
        int nICU = new Double(n * ratioICU).intValue();
        int nOutpatient = new Double(n * ratioOutpatient).intValue();
        if (nWard + nICU + nOutpatient < n) {
            nWard = nWard + (n - nWard + nICU + nOutpatient);
        }
        if (nWard + nICU + nOutpatient > n) {
            nWard = nWard - (nWard + nICU + nOutpatient - n);
        }
        List<Job> all = new ArrayList<>();
        all.addAll(getNWardJobs(nWard));
        all.addAll(getNICUJobs(nICU));
        all.addAll(getNOutpatientJobs(nOutpatient));
        for (int i = 0; i < all.size(); i++) {
            Job r = all.get(i);
            r.setId(i + 1);
        }
        return all;
    }

    @SneakyThrows
    public static List<Job> getNAllJobs(InstanceConfiguration ic) {
        int n = ic.getTotalJobsQuantity();
        Double ratioWARD = ic.getWardJobsRatio();
        Double ratioICU = ic.getICUJobsRatio();
        Double ratioOutpatient = ic.getOutpatientJobsRatio();
        return getNAllJobs(n, ratioWARD, ratioICU, ratioOutpatient);
    }

    private static <T extends Identifiable> List<T> getNorAll(int n, List<T> source) {
        if (source == null || source.size() < 1) {
            initPool();
        }
        List<T> newL = new ArrayList<>(source);
        if (n > source.size()) {
            return newL;
        }
        List<T> random = new ArrayList<>();
        while (n > 0) {
            int i = newL.size();
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(i - 1);
            random.add(newL.get(randomInt));
            newL.remove(randomInt);
            n--;
        }
        for (int i = 0; i < random.size(); i++) {
            T r = random.get(i);
            r.setId(i + 1);
        }
        return random;
    }

    public static Room getBreakroom() {
        return EntityPool.breakroom;
    }
}
