/* Electric(tm) VLSI Design System
 *
 * File: BlockMapForGraph.java
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

import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class serves to unite blocks and real pin names.
 */
public class BlockMapForGraph {

    public ArrayList<ListPin> list; //stores data from a file
    private static BlockMapForGraph blockMap;
    private static AuxilarySimpleAutotracing auxisa;
    private boolean checkFile;

    private BlockMapForGraph() {
        auxisa = AuxilarySimpleAutotracing.getAuxilaryOnlyObject();
        checkingFile();
        if (checkFile) {
            try {
                readerFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static BlockMapForGraph getBlockMapForGraph() {
        if (blockMap == null) {
            blockMap = new BlockMapForGraph();
        }
        return blockMap;
    }

    public boolean getCheckFile() {
        return checkFile;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     *
     * @param shortNamePin
     * @return nambe real Pin String str
     */
    public String getAdrForCau(String shortNamePin) {
        String str = null;
        switch (shortNamePin) {
            case "INM_1":
            case "INM_2":
            case "INM": // deprecated
                str = "CAU.*\\.PX[123]";
                break;
            case "INP_1":
            case "INP_2":
            case "INP": // deprecated
                str = "CAU.*\\.PY[123]";
                break;
            case "OUT_1":
            case "OUT_2":
            case "OUT": // deprecated
                str = "CAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     *
     * @param shortNamePin
     * @return nambe real Pin String str
     */
    public String getAdrForCauComp(String shortNamePin) {
        String str = null;
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "CAU.*\\.PX[123]";
                break;
            case "INP_1":
            case "INP_2":
            case "INP": // deprecated
                str = "CAU.*\\.PY[123]";
                break;
            case "OUT": // deprecated
            case "OUT_1":
            case "OUT_2":
                str = "CAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     *
     * @param shortNamePin
     * @return nambe real Pin String str
     */
    public String getAdrForCauPosFb(String shortNamePin) {
        String str = null;
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "CAU.*\\.PX[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "CAU.*\\.PZ[123]";
                break;
            case "INP_3":
            case "INP_4":
                str = "CAU.*\\.PY[123]";
                break;
            case "OUT": // deprecated
            case "OUT_1":
            case "OUT_2":
                str = "CAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     *
     * @param shortNamePin
     * @return nambe real Pin String str
     */
    public String getAdrForCauNegFb(String shortNamePin) {
        String str = null;
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "CAU.*\\.PZ[123]";
                break;
            case "INM_3":
            case "INM_4":
                str = "CAU.*\\.PX[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "CAU.*\\.PY[123]";
                break;
            case "OUT": // deprecated
            case "OUT_1":
            case "OUT_2":
                str = "CAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     *
     * @param shortNamePin
     * @return nambe real Pin String str
     */
    public String getAdrForPau(String shortNamePin) {
        String str = null;
        switch (shortNamePin) {
            case "INM_1":
            case "INM_2":
            case "INM": // deprecated
                str = "PAU.*\\.PY[123]";
                break;
            case "INP_1":
            case "INP_2":
            case "INP": // deprecated
                str = "PAU.*\\.PX[123]";
                break;
            case "OUT_1":
            case "OUT_2":
            case "OUT": // deprecated
                str = "PAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     *
     * @param shortNamePin
     * @return nambe real Pin String str
     */
    public String getAdrForPauDiff(String shortNamePin) {
        String str = null;
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "PAU.*\\.PY[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "PAU.*\\.PX[123]";
                break;
            case "OUTP": // deprecated
            case "OUTP_1":
            case "OUTP_2":
                str = "PAU.*\\.P[VW][123]";
                break;
            case "OUTM": // deprecated
            case "OUTM_1":
            case "OUTM_2":
                str = "PAU.*\\.P[UR][123]";
                break;
        }
        return str;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     *
     * @param shortNamePin
     * @return nambe real Pin String str
     */
    public String getAdrForPauDiffFb(String shortNamePin) {
        String str = null;
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "PAU.*\\.PZ[123]";
                break;
            case "INM_3":
            case "INM_4":
                str = "PAU.*\\.PY[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "PAU.*\\.PO[123]";
                break;
            case "INP_3":
            case "INP_4":
                str = "PAU.*\\.PX[123]";
                break;
            case "OUTM": // deprecated
            case "OUTM_1":
            case "OUTM_2":
                str = "PAU.*\\.P[UR][123]";
                break;
            case "OUTP": // deprecated
            case "OUTP_1":
            case "OUTP_2":
                str = "PAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     *
     * @param shortNamePin
     * @return nambe real Pin String str
     */
    public String getAdrForPauNegFb(String shortNamePin) {
        String str = null;
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "PAU.*\\.PZ[123]";
                break;
            case "INM_3":
            case "INM_4":
                str = "PAU.*\\.PY[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "PAU.*\\.PX[123]";
                break;
            case "OUT": // deprecated
            case "OUT_1":
            case "OUT_2":
                str = "PAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     *
     * @param shortNamePin
     * @return nambe real Pin String str
     */
    public String getAdrForPauComp(String shortNamePin) {
        String str = null;
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "PAU.*\\.PY[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "PAU.*\\.PX[123]";
                break;
            case "OUT": // deprecated
            case "OUT_1":
            case "OUT_2":
                str = "PAU.*\\.PS[123]";
                break;
        }
        return str;
    }

    /**
     * This method receives a NodeInst element ni returns real Pin number
     *
     * param NodeInst ni
     *
     * @return nambe real Pin String str
     */
    public String getAdrForInput(NodeInst ni) throws FunctionalException, IOException {
        String str = null;
        Iterator<PortInst> itrPi = ni.getPortInsts();
        while (itrPi.hasNext()) {
            PortInst piS = itrPi.next();
            String port = Accessory.parsePortToPortOld(piS.toString());
            if (port.equals("source")) {
                auxisa.addSourceWithSpiceCode(piS, String.valueOf(auxisa.getPaddrVariableValue(ni)));
            }
        }
        str = "PAD";
        if (auxisa.getPaddrVariableValue(ni) != null) {
            str += "DR<" + String.valueOf(auxisa.getPaddrNumber(auxisa.getPaddrVariableValue(ni))) + "\\."; // { needed to avoid being equal PADDR<18 and PADDR<180

        }
        Accessory.printLog("str = " + str);
        return str;
    }

    /**
     * This method receives a NodeInst element ni returns real Pin number
     *
     * param NodeInst ni
     *
     * @return nambe real Pin String str
     */
    public String getAdrForOutput(NodeInst ni) throws FunctionalException, IOException {
        String str = null;
        str = "PAD";
        if (auxisa.getPaddrVariableValue(ni) != null) {
            str = "<" + String.valueOf(auxisa.getPaddrNumber(auxisa.getPaddrVariableValue(ni))) + "\\.";
        }
        return str;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     *
     * param String shortNamePin
     *
     * @return nambe real Pin String str
     */
    public String getAdrForSPM(String shortNamePin) {
        String str = null;
        str = "SPM.*" + shortNamePin + "$"; // { needed to avoid being equal .X1 and .X10
        return str;
    }

    /* This method returns real Pin number
     *
     * @return nambe real Pin String str
     */
    public String getAdrForVSS() {
        String str = null;
        str = "PPC.*\\.P[YV][123]";
        Accessory.printLog("str = " + str);
        return str;
    }

    /* This method returns real Pin number
     *
     * return nambe real Pin String str
     */
    public String getAdrForREF() {
        String str = null;
        str = "ION.*\\.ION";
        Accessory.printLog("str = " + str);
        return str;
    }

    /*This method receives a shortNamePin 
    * pi
    * parameter
    *returns real Pin number
     */
    public String getAdrForCap(String shortNamePin, PortInst pi, String parameter) throws StepFailedException {
        String str = null;
        if (pi == null) {//check
            throw new StepFailedException("Null reference in dealWithBlock.");
        }
        NodeInst niP_Cap = pi.getNodeInst();
        parameter = auxisa.getParameter(niP_Cap.toString());
        if (parameter != null) {
            String index = parameter.substring(parameter.length() - 2, parameter.length());
            switch (shortNamePin) {
                case "C1":
                case "C2":
                case "c2":
                case "c1":
                    switch (index) {
                        case "PX":
                            index = "PY";
                            break;
                        case "PY":
                            index = "PX";
                            break;
                        case "PV":
                            index = "PZ";
                            break;
                        case "PZ":
                            index = "PV";
                            break;
                    }
                    str = parameter.substring(0, parameter.length() - 2) + index + "[123]";
                    Accessory.printLog("str = " + str);
                    return str;
            }
        }
        str = "PPC.*\\.P[XYVZ][123]";
        Accessory.printLog("str = " + str);
        return str;
    }

    /*This method receives a shortNamePin 
    * pi
    * parameter
    *returns real Pin number
     */
    public String getAdrForRes(String shortNamePin, PortInst pi, String parameter) throws FunctionalException {
        String str = null;
        parameter = auxisa.getParameter(pi.getNodeInst().toString());
        if (parameter != null) {
            String index = parameter.substring(parameter.length() - 2, parameter.length());
            switch (shortNamePin) {
                case "res1":
                case "r1":
                case "R1":
                case "R2":
                    switch (index) {
                        case "PO":
                            index = "PO";
                            break;
                        case "PP":
                            index = "PO";
                            break;
                        case "PQ":
                            index = "PQ";
                            break;
                        case "PR":
                            index = "PQ";
                            break;
                    }
                    str = parameter.substring(0, parameter.length() - 2) + index + "[123]";
                    Accessory.printLog("str = " + str);
                    return str;

                case "R3":
                case "R4":
                case "res2":
                case "r2":
                    switch (index) {
                        case "PO":
                            index = "PP";
                            break;
                        case "PP":
                            index = "PP";
                            break;
                        case "PQ":
                            index = "PR";
                            break;
                        case "PR":
                            index = "PR";
                            break;
                    }
                    str = parameter.substring(0, parameter.length() - 2) + index + "[123]";
                    Accessory.printLog("str = " + str);
                    return str;
            }
        }
        switch (shortNamePin) {
            case "R1":
            case "R2":
            case "res1":
            case "r1":
                str = "PPC.*\\.P[OQ][123]";  // str = "PPC.*\\.P[OQPR][123]";
                Accessory.printLog("str = " + str);
                return str;

            case "R3":
            case "R4":
            case "res2":
            case "r2":
                str = "PPC.*\\.P[PR][123]";  // str = "PPC.*\\.P[OQPR][123]";
                Accessory.printLog("str = " + str);
                return str;
        }
        throw new FunctionalException("Precision resistors don't work.");
    }

    /*
    *This method returns collection objects of file 
    * objects type ListPin
     */
    private ArrayList<ListPin> read() throws FileNotFoundException, IOException {
        ArrayList<ListPin> collectionObjectFile = new ArrayList<>();
        File myfile = new File("autotracingMap.trcf");
        FileReader fr = new FileReader(myfile);
        BufferedReader reader = new BufferedReader(fr);
        String line;
        while ((line = reader.readLine()) != null) {
            String[] say = line.split(" ");
            if ((!Search(say[0], collectionObjectFile)) || (collectionObjectFile.size() == 0)) {
                ListPin say1 = new ListPin(say[0], say[1], say[2]);
                collectionObjectFile.add(say1);
            } else {
                collectionObjectFile.get(searchIndexName(say[0], collectionObjectFile)).pin.add(say[1]);
                collectionObjectFile.get(searchIndexName(say[0], collectionObjectFile)).str.add(say[2]);
            }
        }
        return collectionObjectFile;
    }

    /*
    *This method returns object 
    * object type ListPin
     */
    public ArrayList<ListPin> getList() {
        return list;
    }

    /*
    *This method search equality name objects and shortName 
    * parametr say  - shortName 
    * parametr ArrayList<ListPin> collectionObjectFile  - collection object
    * object type ListPinr
    * return boolean true or false
    * true - if there is a coincidence
     */
    public boolean Search(String say, ArrayList<ListPin> collectionObjectFile) {
        boolean Checking = false;
        if (say == null) {
            return Checking;
        }
        for (ListPin collectionObjectFile1 : collectionObjectFile) {
            if (collectionObjectFile1.getName().equals(say)) {
                Checking = true;
                break;
            }
        }
        return Checking;
    }

    /*
    * This method search index transmitted say 
    * in the collection
    * say  - search element
    * retern int index 
     */
    public int searchIndexName(String say, ArrayList<ListPin> collectionObjectFile) {
        int index = 0;
        for (ListPin collectionObjectFile1 : collectionObjectFile) {
            if ((collectionObjectFile1.getName().equals(say)) && (say != null)) {
                index = collectionObjectFile.indexOf(collectionObjectFile1);
                break;
            }
        }
        return index;
    }

    /*
    * This method search index transmitted say 
    * in the collection
    * say  - search element
    * retern int index 
     */
    public int searchIndexPin(String say, ArrayList<String> collectionObjectFile) {
        int index = 0;
        for (String collectionObjectFile1 : collectionObjectFile) {
            if ((collectionObjectFile1.equals(say)) && (say != null)) {
                index = collectionObjectFile.indexOf(collectionObjectFile1);
                break;
            }
        }
        return index;
    }

    /*
    *This method file perform method read, make collection object in the global peremen list
     */
    public void readerFile() throws IOException {
        list = read();
    }

    /*
    *This method checks availability file
     */
    private void checkingFile() {
        checkFile = false;
        if (new File("autotracingMap.trcf").exists()) {
            checkFile = true;
        }
    }

    /* This class create object which will store
    * name - String name elsement
    * pin - ArrsyList<String> collection pin of this element 
    * str - ArrsyList<String> collection str( real nambe pin) of this element
     */
    public class ListPin {

        private String name;
        private ArrayList<String> pin = new ArrayList<>();
        private ArrayList<String> str = new ArrayList<>();

        private ListPin(String q, String n, String e) {
            name = q;
            pin.add(n);
            str.add(e);
        }

        /*
        * This method return name object
         */
        public String getName() {
            return name;
        }

        /*
        * This method return pin collection object
         */
        public ArrayList<String> getPin() {
            return pin;
        }

        /*
        * This method return real pin collection object
         */
        public ArrayList<String> getStr() {
            return str;
        }
    }
}
