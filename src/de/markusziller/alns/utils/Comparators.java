package de.markusziller.alns.utils;

import de.markusziller.alns.entities.*;

import java.util.Comparator;

public class Comparators {

    public static final Comparator<Therapist> THERAPIST_ID_ASCENDING = new Comparator<Therapist>() {

        @Override
        public int compare(Therapist t1, Therapist t2) {
            if (t1.getId() > t2.getId()) {
                return 1;
            }
            if (t1.getId() < t2.getId()) {
                return -1;
            }
            return 0;
        }
    };


    public static final Comparator<Job> JOB_PRIORITY_SCORE_DESCENDING = new Comparator<Job>() {

        @Override
        public int compare(Job i, Job j) {
            if (i.getSchedulingPriority() < j.getSchedulingPriority()) {
                return 1;
            }
            if (i.getSchedulingPriority() > j.getSchedulingPriority()) {
                return -1;
            }
            return 0;
        }
    };

    public static final Comparator<Room> ROOM_ID_ASCENDING = new Comparator<Room>() {

        @Override
        public int compare(Room i, Room j) {
            if (i.getId() < j.getId()) {
                return 1;
            }
            if (i.getId() > j.getId()) {
                return -1;
            }
            return 0;
        }
    };

    public static final Comparator<Node> NODE_START_ASCENDING = new Comparator<Node>() {

        @Override
        public int compare(Node t1, Node t2) {
            return t1.compareTo(t2);
        }
    };

    public static final Comparator<Route> ROUTE_START_ASCENDING = new Comparator<Route>() {

        @Override
        public int compare(Route t1, Route t2) {
            return t1.compareTo(t2);
        }
    };

    public static final Comparator<Timeslot> TIMESLOTS_ASCENDING_BY_START = new Comparator<Timeslot>() {

        @Override
        public int compare(Timeslot t1, Timeslot t2) {
            if (t1.getStart() > t2.getStart()) {
                return 1;
            }
            return -1;
        }
    };

}