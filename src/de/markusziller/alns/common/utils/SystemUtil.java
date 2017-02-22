package de.markusziller.alns.common.utils;

public class SystemUtil {

    public static String getSystemStatus() {
        int mb = 1024 * 1024;
        StringBuilder sb = new StringBuilder();

        // Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        sb.append("##### Heap utilization statistics [MB] #####\n");
        // Print used memory
        sb.append("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb + "\n");
        // Print free memory
        sb.append("Free Memory:" + runtime.freeMemory() / mb + "\n");
        // Print total available memory
        sb.append("Total Memory:" + runtime.totalMemory() / mb + "\n");
        // Print Maximum available memory
        sb.append("Max Memory:" + runtime.maxMemory() / mb + "\n");

        return sb.toString();

    }

}
