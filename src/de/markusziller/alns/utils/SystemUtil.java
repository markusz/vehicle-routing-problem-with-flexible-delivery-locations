package de.markusziller.alns.utils;

class SystemUtil {

    public static String getSystemStatus() {
        int mb = 1024 * 1024;
        StringBuilder sb = new StringBuilder();

        // Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        sb.append("##### Heap utilization statistics [MB] #####\n");
        // Print used memory
        sb.append("Used Memory:").append((runtime.totalMemory() - runtime.freeMemory()) / mb).append("\n");
        // Print free memory
        sb.append("Free Memory:").append(runtime.freeMemory() / mb).append("\n");
        // Print total available memory
        sb.append("Total Memory:").append(runtime.totalMemory() / mb).append("\n");
        // Print Maximum available memory
        sb.append("Max Memory:").append(runtime.maxMemory() / mb).append("\n");

        return sb.toString();

    }

}
