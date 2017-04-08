package de.markusziller.alns.common.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ImportUtil {

    /**
     * Liefert die Rohdaten einer Solomoninstanz
     *
     * @param size 25,50 oder 100
     * @param name
     * @return int[x][y]: y zeilennummer, x spaltennummer <br>
     * x<br>
     * 0: CUST NO. <br>
     * 1: XCOORD <br>
     * 2: YCOORD <br>
     * 3: DEMAND <br>
     * 4: READY TIME <br>
     * 5: DUE DATE <br>
     * 6: SERVICE <br>
     * 7: TIME
     * @throws IOException
     * @author Markus Z.
     * @date 12.12.2013
     */
    public static int[][] importSolomonRawData(int size, String name) throws IOException {

        String dataFileName = new StringBuilder().append("./import").append("/solomon").append("/solomon_").append(size).append("/").append(name).append(".txt").toString();

        BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));

        int[][] values = new int[8][size + 1];

        int data_in_x_lines = Integer.MAX_VALUE;
        int row = 0;
        String line;
        while ((line = bReader.readLine()) != null) {
            String datavalue[] = line.split("\\s+");

            if (datavalue.length > 0 && datavalue[0].equals("CUST")) {
                data_in_x_lines = 2;
                for (int i = 1; i < datavalue.length; i++) {
                    if (i == 5 || i == 7) {
//						System.out.print(datavalue[i] +" "+datavalue[++i]+"\t\t");
                    } else {
//						System.out.print(datavalue[i] +"\t\t");
                    }
                }
//				System.out.println();
            }
            if (data_in_x_lines < 1) {
                for (int i = 1; i < datavalue.length; i++) {
                    values[i - 1][row] = Integer.valueOf(datavalue[i]);
                    values[7][row] = -1;
                }
                row++;
//				System.out.println();

            }
            data_in_x_lines--;
        }
        bReader.close();
        return values;
    }

    public static int[] importSolomonMetaData(int size, String name) throws IOException {

        String dataFileName = new StringBuilder().append("./import").append("/solomon").append("/solomon_").append(size).append("/").append(name).append(".txt").toString();
        BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));

        int[] values = new int[2];

        int row = 0;

        String line;
        while ((line = bReader.readLine()) != null) {
            String datavalue[] = line.split("\\s+");

            if (row == 4) {
                values[0] = Integer.valueOf(datavalue[1]);
                values[1] = Integer.valueOf(datavalue[2]);
                break;
            }
            row++;
        }
        bReader.close();
        return values;
    }

}
