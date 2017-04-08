package de.markusziller.alns.utils;

import com.google.common.collect.Ordering;
import de.markusziller.alns.entities.Timeslot;
import de.markusziller.alns.exceptions.TimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TimeUtil {

    private final static Logger log = Logger.getLogger(TimeUtil.class.getName());

    public static Integer getDuration(Integer start, Integer end) {
        return end - start + 1;
    }

    public static Integer getEnd(Integer start, Integer duration) {
        return start + duration - 1;
    }

    public static Integer getStart(Integer end, Integer duration) {
        return end - duration + 1;
    }

    private static Integer subsequent(Integer time) {
        return time + 1;
    }

    private static Integer prior(Integer time) {
        return time - 1;
    }


    private static boolean noGapBetween(Timeslot... timeslots) {

        timeslots = orderedCopy(timeslots);

        boolean noGap = true;

        for (int i = 0; i < timeslots.length - 1; i++) {
            if (timeslots[i + 1].getStart() - timeslots[i].getEnd() > 1) {
                noGap = false;
                break;
            }
        }

        return noGap;
    }

    public static String timeslotIndexToTime(Integer timeslot, Integer minutesPerTimeslot) {
        Integer minutes = timeslot * minutesPerTimeslot;
        return String.format("%02d:%02d",
                TimeUnit.MINUTES.toHours(minutes),
                minutes % 60

        );
    }

    private static Timeslot[] orderedCopy(Timeslot... timeslots) {

        List<Timeslot> temp = Arrays.asList(timeslots);

        if (!Ordering.from(Comparators.TIMESLOTS_ASCENDING_BY_START).isOrdered(temp)) {
            log.finer("Received unorderd array of timeslots. Returning sorted array before proceeding");
            return Ordering.from(Comparators.TIMESLOTS_ASCENDING_BY_START).sortedCopy(temp).toArray(new Timeslot[0]);
        }

        return timeslots;
    }

    public static List<Timeslot> orderedCopy(List<Timeslot> timeslots) {


        if (!Ordering.from(Comparators.TIMESLOTS_ASCENDING_BY_START).isOrdered(timeslots)) {
            log.finer("Received unorderd list of timeslots. Returning new sorted list before proceeding");
            return Ordering.from(Comparators.TIMESLOTS_ASCENDING_BY_START).sortedCopy(timeslots);
        }

        return timeslots;
    }


    public static boolean overlappingSlots(Timeslot... timeslots) {

        timeslots = orderedCopy(timeslots);

        boolean overlap = false;

        for (int i = 0; i < timeslots.length - 1; i++) {
            if (timeslots[i + 1].getStart() <= timeslots[i].getEnd()) {
                overlap = true;
                break;
            }
        }

        return overlap;
    }


//	public static List<Timeslot> getIntersectionOLD(List<Timeslot>... slots) {
//
//		Integer max = Integer.MIN_VALUE;
//		Integer min = Integer.MAX_VALUE;
//
//		for (List<Timeslot> list : slots) {
//			for (Timeslot timeslot : list) {
//				min = Math.min(min, timeslot.getStart());
//				max = Math.max(max, timeslot.getEnd());
//			}
//		}
//
//		if(min >= max){
//			return new LinkedList<Timeslot>();
//		}
//		return getIntersection(max, slots);
//	}


    public static List<Timeslot> invert(Integer lowerBound, Integer upperBound, List<Timeslot> slots) {
        List<Timeslot> orderedslots = Ordering.from(Comparators.TIMESLOTS_ASCENDING_BY_START).sortedCopy(slots);
        List<Timeslot> inverseList = new LinkedList<>();
        Timeslot ts = null;

        for (int i = 0; i < orderedslots.size(); i++) {

            ts = orderedslots.get(i);

            if (i == 0) {
                if (ts.getStart() > 0) {
                    inverseList.add(new Timeslot(lowerBound, ts.getStart() - 1));
                }
            } else {
                Timeslot previous = orderedslots.get(i - 1);

                if (!TimeUtil.noGapBetween(ts, previous)) {
                    inverseList.add(new Timeslot(subsequent(previous.getEnd()), prior(ts.getStart())));
                }
            }

        }

        if (orderedslots.size() > 0) {
            if (upperBound > ts.getEnd()) {
                inverseList.add(new Timeslot(subsequent(ts.getEnd()), upperBound));
            }
        } else {
            inverseList.add(new Timeslot(lowerBound, upperBound));
        }

        return inverseList;

    }


    @SafeVarargs
    public static List<Timeslot> getIntersection(List<Timeslot>... slots) {

        if (slots.length == 1) {
            return slots[0];
        }

        List<Timeslot> ts = slots[0];

        for (int i = 1; i < slots.length; i++) {
            List<Timeslot> temp = new LinkedList<>();
            List<Timeslot> tsl = slots[i];
            for (Timeslot t : tsl) {
                for (Timeslot t2 : ts) {
                    int l_bound = 0;
                    int u_bound = 0;
                    l_bound = Math.max(t2.getStart(), t.getStart());
                    u_bound = Math.min(t2.getEnd(), t.getEnd());
//					if (t2.getStart() < t.getStart()) {
//						
//						if(t2.getEnd())
//						
//					}
//
//					if (t2.getStart() == t.getStart()) {
//
//					}
//					
//					if (t2.getStart() > t.getStart()) {
//
//					}

                    if (l_bound <= u_bound) {
                        temp.add(new Timeslot(l_bound, u_bound));
                    }
                }

            }

            ts = temp;
        }

        return ts;
    }

    public static List<Timeslot> getIntersection(List<Timeslot> tsl, Timeslot t2) {

        List<Timeslot> ts = new ArrayList<>();

        for (Timeslot t : tsl) {

            int l_bound = 0;
            int u_bound = 0;
            l_bound = Math.max(t2.getStart(), t.getStart());
            u_bound = Math.min(t2.getEnd(), t.getEnd());
//					if (t2.getStart() < t.getStart()) {
//						
//						if(t2.getEnd())
//						
//					}
//
//					if (t2.getStart() == t.getStart()) {
//
//					}
//					
//					if (t2.getStart() > t.getStart()) {
//
//					}

            if (l_bound <= u_bound) {
                ts.add(new Timeslot(l_bound, u_bound));
            }


//			ts = temp;
        }

        return ts;
    }



    public static List<Timeslot> makeTimeslotList(Integer... bounds) throws TimeException {

        List<Timeslot> list = new LinkedList<>();

        if (bounds.length % 2 != 0) {
            throw new TimeException("Uneven number of arguments");
        }

        for (int i = 0; i < bounds.length; i = i + 2) {

            if (bounds[i] > bounds[i + 1]) {
                throw new TimeException("Anfang (" + bounds[i] + ") > Ende (" + bounds[i + 1] + ")");
            } else {
                list.add(new Timeslot(bounds[i], bounds[i + 1]));
            }
        }

        return list;

    }

    public static void replaceTimeslot(List<Timeslot> slots, Timeslot s, Timeslot[] s2) throws Exception {

        if (!slots.contains(s)) {
            throw new Exception("List of slots does not contain Timeslot to be splitted");
        }

    }

    public static List<Timeslot> subtract(Timeslot full, List<Timeslot> l) {
        List<Timeslot> list = new LinkedList<>();
        Timeslot last = null;
        Timeslot temp;
        for (Timeslot ts : l) {

            if (last == null) {
                if (ts.getStart() > full.getStart()) {
                    temp = new Timeslot(full.getStart(), ts.getStart() - 1);
                    list.add(temp);
                }

            } else {
                if (ts.getStart() > last.getEnd() + 1) {
                    temp = new Timeslot(last.getEnd() + 1, ts.getStart() - 1);
                    list.add(temp);
                }
            }
            last = ts;
        }

        if (last.getEnd() < full.getEnd()) {
            temp = new Timeslot(last.getEnd() + 1, full.getEnd());
            list.add(temp);
        }
        return list;
    }

}
