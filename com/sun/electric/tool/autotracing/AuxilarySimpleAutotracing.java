/* Electric(tm) VLSI Design System
 *
 * File: AuxilarySimpleAutotracing.java
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
import com.sun.electric.database.variable.Variable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author diivanov
 */
public class AuxilarySimpleAutotracing {

    private ArrayList<String> sourceList = new ArrayList<>();
    private Map<String, String> keyHashMap = new HashMap<>();
    private HashSet<Pair<NodeInst, String>> SPMList = new HashSet<>();

    private NodeInst firstOfPair = null;
    private String secondOfPair = null;

    private static BlockMapForGraph blockMap;
    private static AuxilarySimpleAutotracing auxisa;
    

    private AuxilarySimpleAutotracing() {           
    }

    public static AuxilarySimpleAutotracing getAuxilaryOnlyObject() {
        if (auxisa == null) {
            auxisa = new AuxilarySimpleAutotracing();
        }
        return auxisa;
    }

    public void resetAuxilary() {
        SPMList = new HashSet<>();
        sourceList = new ArrayList<>();
        keyHashMap = new HashMap<>();
        blockMap = BlockMapForGraph.getBlockMapForGraph();
    }

    /**
     * Method implements autotracing reaction to all types of blocks.
     *
     * @param ni
     * @param pi
     * @return
     * @throws java.io.IOException
     */

    public String dealWithBlock(NodeInst ni, PortInst pi) throws IOException, StepFailedException, FunctionalException {
        blockMap = BlockMapForGraph.getBlockMapForGraph();
        String name = ni.toString();
        int indexName, indexPin;
        String parameter = null;
        String shortName = name.substring(name.indexOf(":") + 1, name.lastIndexOf("{"));
        String shortNamePin = "";
        if (pi != null) {
            shortNamePin = pi.toString().substring(pi.toString().lastIndexOf(".") + 1, pi.toString().lastIndexOf("'"));
        }
        String str = null;
        Accessory.printLog("shortName = " + shortName);

        if (blockMap.getCheckFile()) {
            /*
            * Check availability object
            * Check size collection of object or check Existence collection of object
            * if true then use creation  method object
             */
          
            switch (shortName) {
                case "INPUT":
                    str = blockMap.getAdrForInput(ni);
                    return str;
                case "OUTPUT_ADR":
                case "OUTPUT_DDR":
                case "OUTPUT":
                    str = blockMap.getAdrForOutput(ni);
                    break;
                case "CAP":
                    str = blockMap.getAdrForCap(shortNamePin, pi, parameter);
                    return str;

                case "RES":
                    str = blockMap.getAdrForRes(shortNamePin, pi, parameter);
                    return str;

                case "SPM":
                case "SPM1":
                case "SPM2":
                case "SPM3":
                case "SPM4":
                case "SPM5":
                case "SPM6":
                case "SPM7":
                case "SPM8":
                case "SPM9":
                    firstOfPair = ni;
                    str = blockMap.getAdrForSPM(shortNamePin);
                    break;

                case "REF":
                    for (BlockMapForGraph.ListPin nameElement : blockMap.getList()) {
                        if (nameElement.getName().equals(shortName)) {
                            str = nameElement.getStr().get(0);
                            break;
                        }
                    }
                    return str;
                case "VSS":
                    for (BlockMapForGraph.ListPin nameElement : blockMap.getList()) {
                        if (nameElement.getName().equals(shortName)) {
                            str = nameElement.getStr().get(0);
                            break;
                        }
                    }
                    return str;
                default:
                    indexName = blockMap.searchIndexName(shortName, blockMap.getList());
                    indexPin = blockMap.searchIndexPin(shortNamePin, blockMap.getList().get(indexName).getPin());
                    str = blockMap.getList().get(indexName).getStr().get(indexPin);
                    break;
            }
            assert str != null;
            Accessory.printLog("str = " + str);
            parameter = getParameter(ni.toString());
            Accessory.printLog("parameter " + parameter);
            if (parameter != null) {
                return (parameter + str.substring(3, str.length()));
            }
            return str;
        } else {
            switch (shortName) {
                case "INPUT":
                    str = blockMap.getAdrForInput(ni);
                    return str;

                case "OUTPUT_ADR":
                case "OUTPUT_DDR":
                case "OUTPUT":
                    str = blockMap.getAdrForOutput(ni);
                    break;

                case "CAU":
                    str = blockMap.getAdrForCau(shortNamePin);
                    break;

                case "CAU_COMP":
                    str = blockMap.getAdrForCauComp(shortNamePin);
                    break;

                case "CAU_POS_FB":
                    str = blockMap.getAdrForCauPosFb(shortNamePin);
                    break;

                case "CAU_NEG_FB":
                    str = blockMap.getAdrForCauNegFb(shortNamePin);
                    break;

                case "PAU":
                    str = blockMap.getAdrForPau(shortNamePin);
                    break;

                case "PAU_DIFF":
                    str = blockMap.getAdrForPauDiff(shortNamePin);
                    break;

                case "PAU_DIFF_FB":
                    str = blockMap.getAdrForPauDiffFb(shortNamePin);
                    break;

                case "PAU_NEG_FB":
                    str = blockMap.getAdrForPauNegFb(shortNamePin);
                    break;

                case "PAU_COMP":
                    str = blockMap.getAdrForPauComp(shortNamePin);
                    break;

                case "CAP":
                    str = blockMap.getAdrForCap(shortNamePin, pi, parameter);
                    return str;

                case "RES":
                    str = blockMap.getAdrForRes(shortNamePin, pi, parameter);
                    return str;

                case "SPM":
                case "SPM1":
                case "SPM2":
                case "SPM3":
                case "SPM4":
                case "SPM5":
                case "SPM6":
                case "SPM7":
                case "SPM8":
                case "SPM9":
                    firstOfPair = ni;
                    str = blockMap.getAdrForSPM(shortNamePin);
                    break;

                case "VSS":
                    str = blockMap.getAdrForVSS();
                    return str;

                case "REF":
                    str = blockMap.getAdrForREF();
                    return str;

                default:
                    Accessory.printLog("NOTHING HERE = " + str + " name " + shortNamePin);

            }
            assert str != null;
            parameter = getParameter(ni.toString());
            if (parameter != null) {
                return (parameter + str.substring(3, str.length()));
            }
            return str;
        }
    }

    /**
     * Method to add typical keys when you know blockName, e.g. CAU has 3
     * typical keys: 300k VSS 150k VSS and middle key of capacity.
     *
     * @param ni
     * @param nextBlock
     */
    public void setKeys(NodeInst ni, String nextBlock) throws FunctionalException {
        String name = ni.toString();
        boolean[] r;
        int tmp;
        String shortName = name.substring(name.indexOf(":") + 1, name.lastIndexOf("{"));
        switch (shortName) {
            case "OUTPUT":
            case "INPUT":
                addKey(nextBlock, 8);
                break;

            case "OUTPUT_ADR":
                addKey(nextBlock, 0);
                break;

            case "OUTPUT_DDR":
                addKey(nextBlock, 1);
                break;

            case "CAU":
                addKey(nextBlock, 6);
                addKey(nextBlock, 7);
                addKey(nextBlock, 8);
                addKey(nextBlock, 15);
                addKey(nextBlock, 28);
                break;

            case "CAU_COMP":
                addKey(nextBlock, 15);
                addKey(nextBlock, 17);
                addKey(nextBlock, 28);
                break;

            case "CAU_POS_FB":
                // FEEDBACK BLOCK
                tmp = OAVariableAnalysis(getOAVariableValue(ni));
                r = Accessory.toBinary(tmp, 7);
                for (int i = 0; i < 7; i++) {
                    if (r[i] == true) {
                        addKey(nextBlock, (21 + i));
                    }
                }
                //
                addKey(nextBlock, 6);
                addKey(nextBlock, 7);
                addKey(nextBlock, 8);
                addKey(nextBlock, 15);
                addKey(nextBlock, 19);
                addKey(nextBlock, 28);
                break;

            case "CAU_NEG_FB":
                // FEEDBACK BLOCK
                tmp = OAVariableAnalysis(getOAVariableValue(ni));
                r = Accessory.toBinary(tmp, 7);
                for (int i = 0; i < 7; i++) {
                    if (r[i] == true) {
                        addKey(nextBlock, (21 + i));
                    }
                }
                //
                addKey(nextBlock, 6);
                addKey(nextBlock, 7);
                addKey(nextBlock, 8);
                addKey(nextBlock, 15);
                addKey(nextBlock, 18);
                addKey(nextBlock, 28);
                break;

            case "PAU":
                addKey(nextBlock, 48);
                addKey(nextBlock, 6);
                addKey(nextBlock, 24);
                addKey(nextBlock, 41);
                break;

            case "PAU_DIFF":
                addKey(nextBlock, 49);
                addKey(nextBlock, 6);
                addKey(nextBlock, 24);
                addKey(nextBlock, 41);
                break;

            case "PAU_DIFF_FB":
                // FEEDBACK BLOCK
                String[] vars = getOAVariableValue(ni);
                String[] var1 = {vars[0], vars[2]};
                String[] var2 = {vars[1], vars[3]};
                tmp = OAVariableAnalysis(var1);
                r = Accessory.toBinary(tmp, 7);
                for (int i = 0; i < 7; i++) {
                    if (r[i] == true) {
                        addKey(nextBlock, (25 + i));
                    }
                }
                tmp = OAVariableAnalysis(var2);
                r = Accessory.toBinary(tmp, 7);
                for (int i = 0; i < 7; i++) {
                    if (r[i] == true) {
                        addKey(nextBlock, (34 + i));
                    }
                }
                //
                addKey(nextBlock, 24);
                addKey(nextBlock, 32);
                addKey(nextBlock, 33);
                addKey(nextBlock, 41);
                addKey(nextBlock, 49);
                addKey(nextBlock, 6);
                break;

            case "PAU_NEG_FB":
                // FEEDBACK BLOCK
                tmp = OAVariableAnalysis(getOAVariableValue(ni));
                r = Accessory.toBinary(tmp, 7);
                for (int i = 0; i < 7; i++) {
                    if (r[i] == true) {
                        addKey(nextBlock, (34 + i));
                    }
                }
                //
                addKey(nextBlock, 6);
                addKey(nextBlock, 33);
                addKey(nextBlock, 48);
                addKey(nextBlock, 24);
                addKey(nextBlock, 41);
                break;

            case "PAU_COMP":
                addKey(nextBlock, 6);
                addKey(nextBlock, 8);
                break;

            case "CAP":
                SimpleAutotracing.getSimpleAutotracing().handleAuxilaryCapKeys(nextBlock, ni);
                /* THAT IS VERY WRONG SOLUTION */
                break;

            case "RES":
                String var = getOnlyVariableValue(ni);
                Accessory.printLog("name " + name);
                String parameter = getParameter(name);
                Accessory.printLog("param " + parameter);
                String index = parameter.substring(parameter.length() - 2, parameter.length());
                if (index.equals("PO") || index.equals("PP")) {
                    switch (var) {
                        case "100":
                            addKey(nextBlock, 13);
                            addKey(nextBlock, 14);
                            addKey(nextBlock, 15);
                            addKey(nextBlock, 16);
                            break;

                        case "200":
                            addKey(nextBlock, 13);
                            addKey(nextBlock, 14);
                            addKey(nextBlock, 15);
                            break;

                        case "300":
                            addKey(nextBlock, 13);
                            addKey(nextBlock, 14);
                            break;

                        case "400":
                            addKey(nextBlock, 13);
                            break;

                        case "500":
                            // nothing here
                            break;

                        default:
                            Accessory.showMessage("Illegal resistance. Please check and try again");
                            throw new FunctionalException("Illegal resistance.");
                        /*Autotracing.getAutotracingTool().createAndShowGUI(false);
                            assert false;*/
                    }
                } else if (index.equals("PQ") || index.equals("PR")) {
                    switch (var) {
                        case "100":
                            addKey(nextBlock, 30);
                            addKey(nextBlock, 31);
                            addKey(nextBlock, 32);
                            addKey(nextBlock, 33);
                            break;

                        case "200":
                            addKey(nextBlock, 30);
                            addKey(nextBlock, 31);
                            addKey(nextBlock, 32);
                            break;

                        case "300":
                            addKey(nextBlock, 30);
                            addKey(nextBlock, 31);
                            break;

                        case "400":
                            addKey(nextBlock, 30);
                            break;

                        case "500":
                            // nothing here
                            break;

                        default:
                            Accessory.showMessage("Illegal resistance. Please check and try again");
                            throw new FunctionalException("Illegal resistance.");
                    }
                }

                break;

            case "SPM":

                break;

            case "VSS":
                String parameterVSS = getParameter(name);
                Accessory.printLog("param " + parameterVSS);
                String indexVSS = parameterVSS.substring(parameterVSS.length() - 2, parameterVSS.length());
                if (indexVSS.equals("PY")) {
                    addKey(nextBlock, 12);
                } else if (indexVSS.equals("PV")) {
                    addKey(nextBlock, 35);
                }
                break;
        }
    }

    /**
     * Method to check if block of this portInst has output or all possible
     * traces are used. true if you can trace, false if outs are blocked
     *
     * @param pi
     * @param nogg
     * @return
     */
    public boolean checkBlockForExistingOutput(PortInst pi, NonOrientedGlobalGraph nogg, String secondPort) throws FunctionalException {
        assert pi != null;
        String name = pi.getNodeInst().toString();
        String parameter;
        String shortName = name.substring(name.indexOf(":") + 1, name.lastIndexOf("{"));
        String shortNamePin;
        shortNamePin = Accessory.parsePortToPortOld(pi.toString());

        String str = null;
        switch (shortName) {
            case "CAP":
                NodeInst niCap = pi.getNodeInst();
                parameter = auxisa.getParameter(niCap.toString());
                if (parameter != null) {
                    String index = parameter.substring(parameter.length() - 2, parameter.length());
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
                        default:
                            throw new FunctionalException("Some problems with capacitors");
                    }
                    str = parameter.substring(0, parameter.length() - 2) + index + "[123]";
                }
                break;

            case "RES":
                parameter = auxisa.getParameter(pi.getNodeInst().toString());
                if (parameter != null) {
                    String index = parameter.substring(parameter.length() - 2, parameter.length());
                    switch (shortNamePin) {
                        case "R1":
                        case "R2":
                        case "res1":
                        case "r1"://check
                            str = parameter.substring(0, parameter.length() - 2) + index + "[123]";
                            break;
                        case "R3":
                        case "R4":
                        case "r2"://check
                        case "res2":
                            switch (index) {
                                case "PO":
                                    index = "PP";
                                    break;
                                case "PP":
                                    index = "PO";
                                    break;
                                case "PQ":
                                    index = "PR";
                                    break;
                                case "PR":
                                    index = "PQ";
                                    break;
                                default:
                                    throw new FunctionalException("Some problems with resistors");
                            }
                            str = parameter.substring(0, parameter.length() - 2) + index + "[123]";
                            break;
                    }
                }
            case "ION":
                break;

        }
        return (str == null) || (nogg.findStartingPoint(str)) != -1;
    }

    /**
     * Method to add key to autotracing file,
     *
     * @param ni
     * @param number
     * @Param ni is the name of NodeInst,
     * @Param number is the key you want to add, result key is ni number + key
     * number, add it to keyFile.
     */
    public void addKey(String ni, int number) {
        String absNum = ni.substring(ni.indexOf("<") + 1, ni.lastIndexOf("."));
        int resultKey = Integer.valueOf(absNum) + number;
        Accessory.write(Accessory.CONFIG_PATH, (String.valueOf(resultKey)));
    }

    /**
     * Method to get the PADDR number (<198) from number of
     * inout(2,4,6,19,21...).
     */
    public int getPaddrNumber(Integer i) throws IOException, FunctionalException {
        try (BufferedReader padNumBuf = new BufferedReader(new FileReader(new File(Accessory.PAD_NUM)))) {
            boolean validNum = false;
            String line;
            while ((line = padNumBuf.readLine()) != null) {
                if (i.toString().equals(line)) {
                    validNum = true;
                    break;
                }
            }
            if (validNum == false) {
                Accessory.showMessage("Not valid in/out number.");
                throw new FunctionalException("Not valid in/out number.");
                /*Autotracing.getAutotracingTool().createAndShowGUI(false);
                assert false;*/
            }
        }
        if (i >= 52) {
            i = 198 - 9 * (i - 52);
        } else if ((i >= 21) && (i <= 35)) {
            i = ((i - 21) / 2) * 9;
        } else if ((i >= 38) && (i <= 48)) {
            i = ((i - 22) / 2) * 9;
        } else if ((i >= 2) && (i <= 6)) {
            i = 126 + ((i - 2) / 2) * 9;
        } else if (i == 19) {
            i = 207;
        } else if (i == 5) {
            i = 198;
        }
        return i;
    }

    /**
     * Method to get Variable's value from other nodeInst.
     */
    private String getOnlyVariableValue(NodeInst ni) {
        Iterator<Variable> itrVar = ni.getParameters();
        //assert itrVar.hasNext();
        Variable var = itrVar.next();
        return var.getObject().toString();
    }

    /**
     * Method to get Variable's value from OA nodeInst.
     */
    private String[] getOAVariableValue(NodeInst ni) {
        ArrayList<String> list = new ArrayList<>();
        Iterator<Variable> itrVar = ni.getParameters();
        while (itrVar.hasNext()) {
            Variable var = itrVar.next();
            String value = var.getObject().toString();
            list.add(value);
        }
        assert !list.isEmpty();
        String[] a = new String[0];
        a = list.toArray(a);
        return a;
    }

    /**
     * Method to understand what keys should be taken from Variable's value in
     * OA nodeInst.
     */
    private int OAVariableAnalysis(String[] a) throws FunctionalException {
        try {
            if ((Integer.valueOf(a[0]) == 0) || (Integer.valueOf(a[1]) == 0)) {
                Accessory.showMessage("Incorrect parameters values in CAU/PAU block");
                throw new FunctionalException("Incorrect parameters values in CAU/PAU block.");
                /*Autotracing.getAutotracingTool().createAndShowGUI(false);
                assert false;*/
            }
        } catch (Exception e) {
            // just pass if there is 7.3 or smth.
        }
        int i;
        try {
            float f = 128 / (Float.valueOf(a[0]) + Float.valueOf(a[1]));
            i = Math.round(f * Float.valueOf(a[0]));
        } catch (Exception e) {
            Accessory.showMessage("Incorrect parameters values in CAU/PAU block");
            throw new FunctionalException("Incorrect parameters values in CAU/PAU block.");
            /*assert false;
            throw new Exception("Incorrect parameters values in CAU/PAU block");*/
        }

        return i;
    }

    /**
     * Method to get Variable's value from INPUT/OUTPUT nodeInst.
     */
    public Integer getPaddrVariableValue(NodeInst ni) throws FunctionalException {
        Iterator<Variable> itrVar = ni.getParameters();
        Variable var = itrVar.next();
        String value = var.getObject().toString();
        Integer i;
        try {
            i = Integer.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
            Accessory.showMessage("Not valid in/out number.");
            throw new FunctionalException("Not valid in/out number.");
        }

        if (i != 0) {
            return i;
        }
        return null;
    }

    /* Source section */
 /*
     * Method to get SpiceCode from inout source port and inout parameter.
     */
    public void addSourceWithSpiceCode(PortInst port, String inoutNumber) throws FunctionalException {
        HashSet<String> emptyList = new HashSet<>();
        if (port == null) {
            Accessory.showMessage("One of inputs doesn't have source.");
            throw new FunctionalException("Incorrect parameters values in CAU/PAU block.");
            /*throw new Exception("One of inputs doesn't have source.");*/
        }
        PortInst[] piArr = Accessory.getNearByPortInsts(port, emptyList);
        for (PortInst pi : piArr) {
            if (!pi.toString().equals(port.toString())) {
                handleSource(pi, inoutNumber);
            }
        }
    }

    /**
     * Method to get SpiceCode from source NodeInst and inout parameter.
     */
    private void handleSource(PortInst pi, String inoutNumber) throws FunctionalException {
        Map<String, String> paramMap = new HashMap<>();
        String VSP = "_" + inoutNumber;
        String realName = Accessory.parsePortToBlockOld(pi.toString());
        NodeInst ni = pi.getNodeInst();
        Iterator<Variable> itrVar = ni.getParameters();
        while (itrVar.hasNext()) {
            Variable var = itrVar.next();
            if (var.getObject() != null) {
                paramMap.put(var.getTrueName(), var.getObject().toString());
            }
        }

        String source = null;
        switch (realName) {
            case "vpulse":
                //V$(node_name) $(VSP) 0 pulse ( $(V1) $(V2) $(TD) $(TR) $(TF) $(PW) $(PER) )
                source = "V" + Accessory.parsePortToName(pi.toString()) + " " + VSP + " 0 pulse ("
                        + paramMap.get("V1") + " " + paramMap.get("V2") + " "
                        + paramMap.get("TD") + " " + paramMap.get("TR") + " "
                        + paramMap.get("TF") + " " + paramMap.get("PW") + " "
                        + paramMap.get("PER") + " )";
                break;
            case "vsource":
                //V$(node_name) $(VSP) 0 $(VAL)
                source = "V" + Accessory.parsePortToName(pi.toString()) + " " + VSP + " 0 "
                        + paramMap.get("VAL");
                break;
            case "vsin":
                //V$(node_name) $(VSP) 0 sin ( $(VO) $(VA) $(FREQ) $(TD) $(THETA))
                source = "V" + Accessory.parsePortToName(pi.toString()) + " " + VSP + " 0 sin ("
                        + paramMap.get("VO") + " " + paramMap.get("VA") + " "
                        + paramMap.get("FREQ") + " " + paramMap.get("TD") + " "
                        + paramMap.get("THETA") + " )";
                break;
            case "vsin_AC":
                //V$(node_name) $(VSP) 0 sin ( $(VO) $(VA) $(FREQ) $(TD) $(THETA)) AC $(AC)
                source = "V" + Accessory.parsePortToName(pi.toString()) + " " + VSP + " 0 sin ("
                        + paramMap.get("VO") + " " + paramMap.get("VA") + " "
                        + paramMap.get("FREQ") + " " + paramMap.get("TD") + " "
                        + paramMap.get("THETA") + " )" + "AC" + " " + paramMap.get("AC");

                break;
            case "vpwl":
                //V$(node_name) $(VSP) 0 PWL $(VAL)
                source = "V" + Accessory.parsePortToName(pi.toString()) + " " + VSP + " 0 "
                        + paramMap.get("VAL");
                break;
            default:
                Accessory.showMessage("Some problems with source " + realName);
                throw new FunctionalException("Some problems with source " + realName);
            //break;
        }
        sourceList.add(source);
    }

    /*
    * Method for checking the presence of a file 
    * 
     */
   

    /**
     * Method to get list with all sources to add them into spice declaration.
     *
     * @return
     */
    public ArrayList<String> getSourceCodes() {
        return sourceList;
    }

    /* Parameters section */
    /**
     * Method to set Parameter of block, saving it in HashMap.
     *
     * @param ni
     * @param textParameter
     */
    public void setParameter(NodeInst ni, String textParameter) {
        if (textParameter.contains("PPC")) {
            textParameter = Accessory.parsePortToBlock(textParameter) + ".*\\." + Accessory.parsePortToPort(textParameter).substring(0, 2);
        } else {
            textParameter = Accessory.parsePortToBlock(textParameter);
        }
        keyHashMap.put(ni.toString(), textParameter);
    }

    /**
     * Method to get parameter from HashMap, method uses key that is the real
     * name of block (CAU -> CAU<1111). @
     *
     *
     * param key @return
     */
    public String getParameter(String key) {
        return keyHashMap.get(key);
    }

    /* SPM section */
    public void addSPMBlock(String nextBlock) {
        assert firstOfPair != null;
        secondOfPair = nextBlock;
        Pair<NodeInst, String> pair = new Pair<>(firstOfPair, secondOfPair);
        SPMList.add(pair);
        firstOfPair = null;
        secondOfPair = null;
    }

    public HashSet<Pair<NodeInst, String>> getSPMList() {
        return SPMList;
    }
}
