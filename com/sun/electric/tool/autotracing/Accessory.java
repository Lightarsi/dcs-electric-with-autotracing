/* Electric(tm) VLSI Design System
 *
 * File: Accessory.java
 *
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
 *
 * Electric(tm) is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Electric(tm) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sun.electric.tool.autotracing;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;

import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.PrintWriter;

import java.util.regex.Pattern;

import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.tool.Job;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.Connection;
import java.util.HashSet;

/**
 *
 * @author diivanov
 */
public class Accessory {

    private static long timeStart = 0;
    private static long timeStart2 = 0;
    private static long deltaTime = 0;

    /**
     *
     */
    //public static final String PATH = new File("").getAbsolutePath();
    public static final String PATH = new File("").getAbsoluteFile().getAbsolutePath();

    /**
     *
     */
    public static final String PARENT_PATH;

    /**
     *
     */
    public static final String CONFIG_WITHOUT_MODELLING_PATH;

    /**
     *
     */
    public static final String CONFIG_PATH;

    static {
        PARENT_PATH = setStaticPath();
        CONFIG_WITHOUT_MODELLING_PATH = PARENT_PATH + "/config/wmconfig.txt";
        CONFIG_PATH = PARENT_PATH + "/config/Autotracing.txt";
    }

    /**
     *
     */
    public static final String GLOBAL_PATH = PATH + "/autotracing/globalGraph.trc";

    /**
     *
     */
    public static final String POINTS_PATH = PATH + "/autotracing/test.trc";

    /**
     *
     */
    public static final String CB_PATH = PATH + "/autotracing/AutotraCB.trc";

    /**
     *
     */
    public static final String PPC_PATH = PATH + "/autotracing/PPC.trc";

    /**
     *
     */
    public static final String SPM_PATH = PATH + "/autotracing/SPM.trc";

    /**
     *
     */
    public static final String CAU_PATH = PATH + "/autotracing/CAU.trc";

    /**
     *
     */
    public static final String PAU_PATH = PATH + "/autotracing/PAU.trc";

    /**
     *
     */
    public static final String ALL_BLOCKS = PATH + "/autotracing/allBlocks.trc";

    /**
     *
     */
    public static final String PAD_NUM = PATH + "/autotracing/PADnum.trc";

    /**
     *
     */
    public static final Integer CB_PATH_LENGTH = 149;

    /**
     * private constructor prohibits creating objects of this class.
     */
    private Accessory() {
        throw new AssertionError();
    }

    private static String setStaticPath() {
        try {
            return new File("..").getCanonicalFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
        return null;
    }

    /**
     * Method to append string @text to file @fileName.
     *
     * @param fileName
     * @param text
     */
    public static void write(String fileName, String text) {
        try {
            try (FileWriter fw = new FileWriter(fileName, true)) {
                fw.write(text + "\n");
            }
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    /**
     * Method to show dialog to user
     *
     * @param s
     */
    public static void showMessage(String s) {
        JOptionPane.showMessageDialog(null, s);
    }

    /**
     * Method to show log message to user
     *
     * @param s
     */
    public static void printLog(String s) {
        if (Constants.isLogging()) {
            System.out.println(s);
        }
    }

    /**
     * Method to recreate file with @fileName.
     *
     * @param fileName
     */
    public static void cleanFile(String fileName) {
        try {
            PrintWriter pw = new PrintWriter(fileName);
            pw.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Method to decompose in powers of two.
     *
     * @param number
     * @param base
     * @return
     */
    public static boolean[] toBinary(int number, int base) {
        final boolean[] ret = new boolean[base];
        for (int i = 0; i < base; i++) {
            ret[base - 1 - i] = ((1 << i) & number) != 0;
        }
        return ret;
    }

    /**
     * method implemets parsing of Port String to get extended Block Name.
     *
     * @param port
     * @return
     */
    public static String parsePortAndCut(String port) {
        assert port != null;
        String s1 = port.substring(port.indexOf(":") + 1, port.indexOf("{"));
        String s2 = port.substring(port.indexOf("."), port.lastIndexOf("'"));
        return (s1 + s2);
    }

    /**
     * method implemets parsing of Port String to get Block Name.
     *
     * @param port
     * @return
     */
    public static String parsePortToBlock(String port) {
        assert port != null;
        return port.substring(0, port.indexOf(".")); // name smth like CB<7454
        // port '5400TP035:ION{ic}[ION<1].ION'
    }

    /**
     * method implemets parsing of Port String to get Block Name.
     *
     * @param port
     * @return
     */
    public static String parsePortToBlockOld(String port) {
        assert port != null;
        return port.substring(port.indexOf(":") + 1, port.indexOf("{")); // name smth like CB<7454
        // port '5400TP035:ION{ic}[ION<1].ION'
    }

    /**
     * method implemets parsing of Port String to get Port Name.
     *
     * @param port
     * @return
     */
    public static String parsePortToPort(String port) {
        assert port != null;
        return port.substring(port.indexOf(".") + 1, port.length()); // name smth like CB<7454
    }

    /**
     * method implemets parsing of Port String to get Port Name.
     *
     * @param port
     * @return
     */
    public static String parsePortToPortOld(String port) {
        assert port != null;
        return port.substring(port.indexOf(".") + 1, port.lastIndexOf("'")); // name smth like CB<7454
    }

    /**
     * Method implements parsing of Port String to get NodeName with number.
     *
     * @param port
     * @return
     */
    public static String parsePortToName(String port) {
        assert port != null;
        return port.substring(port.indexOf("[") + 1, port.indexOf("]")); // name smth like CB<7454
    }

    /**
     * Method to get number of keys in scheme.
     *
     * @param port1
     * @param port2
     * @return
     */
    public static int parsePortToKey(String port1, String port2) {
        Pattern p = Pattern.compile("[nopqrstuvw]\\d+\\d*");
        if (p.matcher(port1).matches()) {
            String key = port1.substring(1, port1.length());
            if (Integer.valueOf(key) % 2 == 0) {
                int intKey = Integer.valueOf(key);
                return (intKey / 2) - 1;
            }
        }
        if (p.matcher(port2).matches()) {
            String key = port2.substring(1, port2.length());
            if (Integer.valueOf(key) % 2 == 0) {
                int intKey = Integer.valueOf(key);
                return (intKey / 2) - 1;
            }
        }
        return -1;
    }

    /**
     * Method implemets parsing of Port String to get Port Name,
     *
     * @param port
     * @return
     */
    public static String parsePortToNumber(String port) {
        String blockName = port.substring(0, port.indexOf(".")); // name smth like CB<7454
        assert !blockName.isEmpty();
        return blockName.split("<")[1];
    }

    /**
     * Method to get local number from letter in SPM.
     *
     * @param letter
     * @return
     */
    public static int explainSPMLetter(String letter) {
        switch (letter) {
            case "n":
                return 0;
            case "o":
                return 108;
            case "p":
                return 280;
            case "q":
                return 508;
            case "r":
                return 680;
            case "s":
                return 908;
            case "t":
                return 1080;
            case "u":
                return 1308;
            case "v":
                return 1480;
            case "w":
                return 1708;
            default:
                return -1;
        }
    }

    /**
     * Method to get path of file according to name of block
     *
     * @param blockName
     * @return
     */
    public static String getPathToDeclaration(String blockName) {
        switch (blockName) {
            case "CB":
                return CB_PATH;
            case "PPC":
                return PPC_PATH;
            case "SPM":
                return SPM_PATH;
            case "CAU":
                return CAU_PATH;
            case "PAU":
                return PAU_PATH;
            default:
                assert false;
        }
        return null;
    }

    /**
     * String counter in file.
     *
     * @param file
     * @return
     */
    public static int getStringCount(File file) {
        int qr = 0;
        BufferedReader bufferedReader;
        try {
            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.readLine() != null) {
                qr++;
            }
            bufferedReader.close();
            return qr;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return 0;
    }

    /**
     * Timer starts here.
     */
    public static void timeStart(String s) {
        if (timeStart2 != 0L) {
            deltaTime = (System.currentTimeMillis() - timeStart2);
            timeStart2 = System.currentTimeMillis();
            System.out.println(deltaTime + " ms");
        } else {
            timeStart2 = System.currentTimeMillis();
        }

    }

    /**
     * Timer finishes and print result.
     */
    public static void timeFinish() {
        System.out.println((System.currentTimeMillis() - timeStart) + "ms");
    }

    /**
     * Method to get array of INPUT-like chain (PADDR.PX1-6).
     *
     * @return
     */
    public static NodeInst[] getStartingNodeInst() {
        Cell curcell = Job.getUserInterface().getCurrentCell();
        Iterator<NodeInst> itr = curcell.getNodes();
        ArrayList<NodeInst> inputList = new ArrayList<>();
        while (itr.hasNext()) {
            NodeInst ni = itr.next();
            if (ni.toString().contains("INPUT")) {
                inputList.add(ni);
                //break;
            }
        }
        NodeInst[] nia = new NodeInst[0];
        nia = inputList.toArray(nia);
        return nia;
    }

    /**
     * Method to get all connected ports using one PortInst.
     *
     * @param pi
     * @param usedPortList
     * @return
     */
    public static PortInst[] getNearByPortInsts(PortInst pi, HashSet<String> usedPortList) {
        PortInst[] a = new PortInst[0];
        ArrayList<PortInst> portList = new ArrayList<>();
        Iterator<Connection> itr = pi.getConnections();
        // can't be sure in this for testing...
        if (!itr.hasNext()) {
            return a;
        }
        Connection cntn = itr.next();
        ArcInst ai = cntn.getArc();
        if ((!ai.getPortInst(0).toString().contains("Wire_Pin")) && (!ai.getPortInst(1).toString().contains("Wire_Pin"))) {
            if (ai.getPortInst(0).toString().equals(pi.toString())) {
                portList.add(ai.getPortInst(1));
            } else {
                portList.add(ai.getPortInst(0));
            }
            a = portList.toArray(a);
            return a;
        }
        if (ai.getPortInst(0).toString().equals(pi.toString())) {
            if (!ai.getPortInst(1).toString().contains("Wire_Pin")) {
                portList.add(ai.getPortInst(1));
                a = portList.toArray(a);
                return a;
            }
            findNext(ai, ai.getPortInst(1), portList, usedPortList);
        } else {
            if (!ai.getPortInst(0).toString().contains("Wire_Pin")) {
                portList.add(ai.getPortInst(1));
                a = portList.toArray(a);
                return a;
            }
            findNext(ai, ai.getPortInst(0), portList, usedPortList);
        }

        a = portList.toArray(a);
        return a;
    }

    /**
     * Internal method to implement getNearByPortInsts method.
     */
    private static void findNext(ArcInst ais, PortInst pis, ArrayList<PortInst> portList, HashSet<String> usedPortList) {
        Iterator<Connection> itr = pis.getConnections();
        while (itr.hasNext()) {
            int head = 0;
            ArcInst ai = itr.next().getArc();
            if (ai.toString().equals(ais.toString())) {
                continue;
            }
            PortInst thisPort = ai.getPortInst(0);
            PortInst thisPortNot = ai.getPortInst(1);
            if (pis.toString().equals(thisPort.toString())) {
                head = 1;
            }

            if (head == 1) {
                if (!thisPortNot.toString().contains("Wire_Pin")) {
                    if (!usedPortList.contains(thisPortNot)) {
                        portList.add(thisPortNot);
                    }

                } else {
                    findNext(ai, thisPortNot, portList, usedPortList);
                }
            } else {
                if (!thisPort.toString().contains("Wire_Pin")) {
                    if (!usedPortList.contains(thisPort)) {
                        portList.add(thisPort);
                    }
                } else {
                    findNext(ai, thisPort, portList, usedPortList);
                }
            }
        }
    }
}
