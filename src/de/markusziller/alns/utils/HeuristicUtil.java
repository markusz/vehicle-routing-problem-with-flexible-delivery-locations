package de.markusziller.alns.utils;

import de.markusziller.alns.entities.Job;
import de.markusziller.alns.entities.Room;
import de.markusziller.alns.entities.jobs.ICUJob;
import de.markusziller.alns.entities.jobs.WardJob;
import de.markusziller.alns.entities.rooms.ICU;

import java.util.*;

public class HeuristicUtil {

    public static <T> List<T> sort(Collection<T> c, Comparator<T> comp) {
        List<T> list = new ArrayList<>(c);
        java.util.Collections.sort(list, comp);
        return list;
    }

    public static List<Integer> splitQHash(Integer qHash) {

        List<Integer> l = new LinkedList<>();

        String by = Integer.toBinaryString(qHash);
        int p = 0;
        for (char c : by.toCharArray()) {

            if (Integer.parseInt(c + "") > 0) {
                Double d = (Math.pow(2.0, p * 1.0));
                Integer in = d.intValue();

                if (d > 0) {
                    l.add(in);
                }
            }

            p++;
        }

        return l;

    }

    public static Integer max(int[] arr) {
        if (arr.length < 1) {
            System.err.println("max nicht bestimmtbar, array.length = 0");
        }

        int max = Integer.MIN_VALUE;

        for (int anArr : arr) {
            if (anArr > max) {
                max = anArr;
            }
        }

        return max;
    }

    public static Integer min(int[] arr) {
        if (arr.length < 1) {
            System.err.println("min nicht bestimmtbar, array.length = 0");
        }

        int min = Integer.MAX_VALUE;

        for (int anArr : arr) {
            if (anArr < min) {
                min = anArr;
            }
        }

        return min;
    }

    public static boolean isWardJob(Job j) {
        return j instanceof WardJob;
    }

    public static boolean isICUJob(Job j) {
        return j instanceof ICUJob;
    }

    public static boolean isICU(Room r) {
        return r instanceof ICU;
    }

    static double getSolomonBestValue(String instance) {

        TreeMap<String, Double> map = new TreeMap<>();
        map.put("C101", 828.94);
        map.put("C102", 828.94);
        map.put("C103", 828.06);
        map.put("C104", 828.78);
        map.put("C105", 828.94);
        map.put("C106", 828.94);
        map.put("C107", 828.94);
        map.put("C108", 828.94);
        map.put("C109", 828.94);
        map.put("C201", 591.56);
        map.put("C202", 595.96);
        map.put("C203", 591.17);
        map.put("C204", 590.60);
        map.put("C205", 588.88);
        map.put("C206", 588.49);
        map.put("C207", 588.29);
        map.put("C208", 588.32);
        map.put("R101", 1650.80);
        map.put("R102", 1486.12);
        map.put("R103", 1292.68);
        map.put("R104", 1007.31);
        map.put("R105", 1377.11);
        map.put("R106", 1252.03);
        map.put("R107", 1104.66);
        map.put("R108", 960.88);
        map.put("R109", 1194.73);
        map.put("R110", 1118.84);
        map.put("R111", 1096.72);
        map.put("R112", 982.14);
        map.put("R201", 1252.37);
        map.put("R202", 1191.70);
        map.put("R203", 939.50);
        map.put("R204", 825.52);
        map.put("R205", 994.42);
        map.put("R206", 906.14);
        map.put("R207", 890.61);
        map.put("R208", 726.82);
        map.put("R209", 909.16);
        map.put("R210", 939.37);
        map.put("R211", 885.71);
        map.put("RC101", 1696.94);
        map.put("RC102", 1554.75);
        map.put("RC103", 1261.67);
        map.put("RC104", 1135.48);
        map.put("RC105", 1629.44);
        map.put("RC106", 1424.73);
        map.put("RC107", 1230.48);
        map.put("RC108", 1139.82);
        map.put("RC201", 1406.94);
        map.put("RC202", 1365.65);
        map.put("RC203", 1049.62);
        map.put("RC204", 798.46);
        map.put("RC205", 1297.65);
        map.put("RC206", 1146.32);
        map.put("RC207", 1061.14);
        map.put("RC208", 828.14);


        return map.get(instance);
    }

    static int getSolomonVehiclesValue(String instance) {

        TreeMap<String, Integer> map = new TreeMap<>();
        map.put("C101", 10);
        map.put("C102", 10);
        map.put("C103", 10);
        map.put("C104", 10);
        map.put("C105", 10);
        map.put("C106", 10);
        map.put("C107", 10);
        map.put("C108", 10);
        map.put("C109", 10);
        map.put("C201", 3);
        map.put("C202", 3);
        map.put("C203", 3);
        map.put("C204", 3);
        map.put("C205", 3);
        map.put("C206", 3);
        map.put("C207", 3);
        map.put("C208", 3);
        map.put("R101", 19);
        map.put("R102", 17);
        map.put("R103", 13);
        map.put("R104", 9);
        map.put("R105", 14);
        map.put("R106", 12);
        map.put("R107", 10);
        map.put("R108", 9);
        map.put("R109", 11);
        map.put("R110", 10);
        map.put("R111", 10);
        map.put("R112", 9);
        map.put("R201", 4);
        map.put("R202", 3);
        map.put("R203", 3);
        map.put("R204", 2);
        map.put("R205", 3);
        map.put("R206", 3);
        map.put("R207", 2);
        map.put("R208", 2);
        map.put("R209", 3);
        map.put("R210", 3);
        map.put("R211", 2);
        map.put("RC101", 14);
        map.put("RC102", 12);
        map.put("RC103", 11);
        map.put("RC104", 10);
        map.put("RC105", 13);
        map.put("RC106", 11);
        map.put("RC107", 11);
        map.put("RC108", 10);
        map.put("RC201", 4);
        map.put("RC202", 3);
        map.put("RC203", 3);
        map.put("RC204", 3);
        map.put("RC205", 4);
        map.put("RC206", 3);
        map.put("RC207", 3);
        map.put("RC208", 3);


        return map.get(instance);
    }

}
