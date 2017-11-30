/* Electric(tm) VLSI Design System
 *
 * File: ExportKeys.java
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

import java.util.Iterator;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.topology.Connection;
import com.sun.electric.database.EditingPreferences;
import com.sun.electric.technology.ArcProto;
import com.sun.electric.technology.technologies.Generic;
import com.sun.electric.database.hierarchy.Export;
import com.sun.electric.database.prototype.PortProto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.scripts.DeleteUnusedPart;
import com.sun.electric.tool.user.User;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author diivanov
 */
public class ExportKeys {

    private static final String[] globVerts = {"X11", "X12", "X13", "X14", "X15", "X16", "X21", "X22", "X23", "X24", "X25", "X26",
        "Y11", "Y12", "Y13", "Y14", "Y15", "Y16", "Y21", "Y22", "Y23", "Y24", "Y25", "Y26",
        "X", "Y"};

    private static Set<ArcInst> aiList = new HashSet<ArcInst>();
    private static Set<ArcInst> arcRedList = new HashSet<ArcInst>();

    /**
     * Method to extract keys from cell.
     */
    public static void ExportKeysFromScheme() {
        Accessory.cleanFile(Accessory.CONFIG_WITHOUT_MODELLING_PATH);
        Cell curcell = Job.getUserInterface().getCurrentCell();

        Iterator<ArcInst> itr = curcell.getArcs();
        while (itr.hasNext()) {
            ArcInst ai = itr.next();
            PortInst thisPort = ai.getPortInst(0);
            PortInst thisPortNot = ai.getPortInst(1);
            if ((!thisPort.toString().contains("Wire_Pin")) && (!thisPortNot.toString().contains("Wire_Pin"))) {
                Pattern p = Pattern.compile(".*(\\.n)+(.*)");
                if (p.matcher(thisPort.toString()).matches()) {
                    String blockNum = parsePortToBlock(thisPort.toString());
                    String keyNum = parsePortToKey(thisPort.toString(), thisPortNot.toString());
                    int result = Integer.valueOf(blockNum) + (Integer.valueOf(keyNum));

                    Accessory.write(Accessory.CONFIG_WITHOUT_MODELLING_PATH, (String.valueOf(result)));
                }
            }
        }
    }

    public static void ExportKeysFromSchemeWithIndication() throws IOException {
        ExportKeysFromSchemeWithIndication(null, true);
    }

    /**
     * Method to extract keys from cell, uses scheme to indicate used nets.
     *
     * @throws java.io.IOException
     */
    public static void ExportKeysFromSchemeWithIndication(Cell curcell, boolean fromScheme) throws IOException {
        Accessory.cleanFile(Accessory.CONFIG_WITHOUT_MODELLING_PATH);
        if (curcell == null) {
            curcell = Job.getUserInterface().getCurrentCell();
        }

        Iterator<ArcInst> itr = curcell.getArcs();
        while (itr.hasNext()) {
            ArcInst ai = itr.next();
            PortInst thisPort = ai.getPortInst(0);
            PortInst thisPortNot = ai.getPortInst(1);
            if ((!thisPort.toString().contains("Wire_Pin")) && (!thisPortNot.toString().contains("Wire_Pin"))) {
                Pattern p = Pattern.compile(".*(CB<)+.*(\\.n)+(.*)");
                Pattern ppc = Pattern.compile(".*(PPC<)+.*(\\.n)+(.*)");
                Pattern cau = Pattern.compile(".*(CAU<)+.*(\\.n)+(.*)");
                Pattern pau = Pattern.compile(".*(PAU<)+.*(\\.n)+(.*)");
                Pattern spm = Pattern.compile(".*(SPM<)+.*(\\.n)+(.*)");
                if (p.matcher(thisPort.toString()).matches()) {
                    String blockNum = parsePortToBlock(thisPort.toString());
                    String keyNum = parsePortToKey(thisPort.toString(), thisPortNot.toString());
                    int result = Integer.valueOf(blockNum) + (Integer.valueOf(keyNum));
                    NodeInst ni = thisPort.getNodeInst();
                    PortInst[] piArray = getPortsToIndicate(keyNum, ni);
                    for (PortInst pi : piArray) {
                        if (pi.getConnections() != null) {
                            Iterator<Connection> itr2 = pi.getConnections();
                            while (itr2.hasNext()) {
                                Connection cntn = itr2.next();
                                doMakeWide(cntn.getArc(), pi);
                            }
                        }
                    }
                    Accessory.write(Accessory.CONFIG_WITHOUT_MODELLING_PATH, (String.valueOf(result)));
                } else if (indicationWithPattern(ppc, thisPort, thisPortNot, Accessory.PPC_PATH)); else if (indicationWithPattern(cau, thisPort, thisPortNot, Accessory.CAU_PATH)); else if (indicationWithPattern(pau, thisPort, thisPortNot, Accessory.PAU_PATH)); else if (indicationWithPattern(spm, thisPort, thisPortNot, Accessory.SPM_PATH));
            }
        }
        if(fromScheme) {
            new ReplaceArcs(aiList, false);       // only surrounds of path
            new ReplaceArcs(arcRedList, true);    // only direct path
        } else {
            //new ReplaceArcsForAutotracing(aiList, false);       // only surrounds of path
            new ReplaceArcsForAutotracing(arcRedList, true);    // only direct path
        }

        


        aiList = new HashSet<>();
        arcRedList = new HashSet<>();
    }

    /**
     * Method implemets indication with not CB block (others are PPC, CAU, PAU).
     */
    private static boolean indicationWithPattern(Pattern pat, PortInst thisPort, PortInst thisPortNot, String PATH) throws IOException {
        if (pat.matcher(thisPort.toString()).matches()) {
            String blockNum = parsePortToBlock(thisPort.toString());
            String keyNum = parsePortToKey(thisPort.toString(), thisPortNot.toString());
            int result = Integer.valueOf(blockNum) + (Integer.valueOf(keyNum));
            NodeInst ni = thisPort.getNodeInst();
            PortInst[] piArray = getPortsToIndicateOther(keyNum, ni, PATH);
            for (PortInst pi : piArray) {
                if (pi.getConnections() != null) {
                    Iterator<Connection> itr2 = pi.getConnections();
                    while (itr2.hasNext()) {
                        Connection cntn = itr2.next();
                        doMakeWide(cntn.getArc(), pi);
                    }
                }
            }
            Accessory.write(Accessory.CONFIG_WITHOUT_MODELLING_PATH, (String.valueOf(result)));
            return true;
        }
        return false;
    }

    /**
     * Method implemets parsing of Port String to get Port Name,
     *
     * @Deprecated.
     */
    private static String parsePortToBlock(String port) {
        String blockName = port.substring(port.indexOf("'") + 1, port.indexOf("{")); // name smth like CB<7454
        assert !blockName.isEmpty();
        return blockName.split("<")[1];
    }

    /**
     * Method to get configuration number of keys in scheme.
     */
    private static String parsePortToKey(String port1, String port2) {
        if ((port1.substring(port1.lastIndexOf("n") + 1, port1.lastIndexOf("'"))) != null) {
            String key = port1.substring(port1.lastIndexOf("n") + 1, port1.lastIndexOf("'"));
            if (Integer.valueOf(key) % 2 == 0) {
                int intKey = Integer.valueOf(key);
                return String.valueOf((intKey / 2) - 1);
            }
        }
        if ((port2.substring(port2.lastIndexOf("n") + 1, port2.lastIndexOf("'"))) != null) {
            String key = port2.substring(port2.lastIndexOf("n") + 1, port2.lastIndexOf("'"));
            if (Integer.valueOf(key) % 2 == 0) {
                int intKey = Integer.valueOf(key);
                return String.valueOf((intKey / 2) - 1);
            }
        }
        return null;
    }

    /**
     * Method to get ports that is near arcs should be indicated.
     */
    private static PortInst[] getPortsToIndicate(String KeyNumber, NodeInst ni) throws IOException {
        PortInst[] a;
        try (BufferedReader getPortReader = new BufferedReader(new FileReader(Accessory.CB_PATH))) {
            String line;
            List<PortInst> portList = new ArrayList<>();
            while ((line = getPortReader.readLine()) != null) {
                String[] divided = line.split(" : ");
                if (divided[1].equals(KeyNumber)) {
                    String[] externalPin = checkLineForExternalPins(divided[0]);
                    if (externalPin.length != 0) {
                        for (String ep : externalPin) {
                            portList.add(ni.findPortInst(ep));
                        }
                    }
                }
            }
            a = new PortInst[0];
            a = portList.toArray(a);
        }
        return a;
    }

    /**
     * Method to get ports that is near arcs should be indicated, PPC ONLY.
     */
    private static PortInst[] getPortsToIndicateOther(String KeyNumber, NodeInst ni, String PATH) throws IOException {
        PortInst[] a;
        try (BufferedReader getPortReader = new BufferedReader(new FileReader(PATH))) {
            String line;
            List<PortInst> portList = new ArrayList<>();
            while ((line = getPortReader.readLine()) != null) {
                String[] divided = line.split(" -- ");
                if (divided[1].equals(KeyNumber)) {
                    portList.add(ni.findPortInst(divided[0]));
                }
            }
            a = new PortInst[0];
            a = portList.toArray(a);
        }
        return a;
    }

    /**
     * Method to check line if it has external pin or not, it returns all
     * external pins.
     */
    private static String[] checkLineForExternalPins(String pins) {
        List<String> portNames = new ArrayList<>();
        String[] divided = pins.split(" -- ");
        for (String vert : globVerts) {
            for (String div : divided) {
                if (div.equals(vert)) {
                    portNames.add(vert);
                }
            }
        }
        String[] a = new String[0];
        a = portNames.toArray(a);
        return a;
    }

    /**
     * Method to get external PortInst from NodeInst internal PortInst and check
     * if it connects.
     */
    private static boolean getPortAndCheckConnect(PortInst pi, NodeInst ni) {
        Iterator<Export> expItr = pi.getExports();
        if (!expItr.hasNext()) {
            return false;
        }
        Export newExp = expItr.next();
        PortInst newPi = ni.findPortInstFromEquivalentProto(newExp);
        return newPi.hasConnections();
    }

    /**
     * Method to adequate implement getPortAndCheckConnect method with key's P3
     * PortInst.
     */
    private static boolean useAnotherKeyPin(PortInst pi, NodeInst nis) {
        NodeInst ni = pi.getNodeInst();
        String portStr = pi.toString();
        String replacedStr = portStr.replaceAll("P3", "P2");
        Iterator<PortInst> itrPI = ni.getPortInsts();
        PortInst newPi = null;
        while (itrPI.hasNext()) {
            PortInst checkPi = itrPI.next();
            if (checkPi.toString().equals(replacedStr)) {
                newPi = checkPi;
            }
        }
        if (newPi == null) {
            return false;
        }
        Iterator<Connection> itr = newPi.getConnections();
        ArcInst ai = itr.next().getArc();
        assert !itr.hasNext();
        for (int i = 0; i < 2; i++) {
            if (ai.getPortInst(i).toString().contains("iopin")) {
                return getPortAndCheckConnect(ai.getPortInst(i), nis);
            }
        }
        return false;

    }

    /**
     * Method to recursively go through all arcs in network.
     */
    private static boolean nextAi(PortInst pi, ArcInst ais, NodeInst ni) {
        Iterator<Connection> itr = pi.getConnections();
        boolean back = false;
        while (itr.hasNext()) {
            int head = 0;
            ArcInst ai = itr.next().getArc();
            if (ai.toString().equals(ais.toString())) {
                continue;
            }
            PortInst thisPort = ai.getPortInst(0);
            PortInst thisPortNot = ai.getPortInst(1);
            if (pi.toString().equals(thisPort.toString())) {
                head = 1;
            }
            if (head == 1) {

                if (thisPortNot.toString().contains("iopin")) {
                    if (getPortAndCheckConnect(thisPortNot, ni)) {
                        return true;
                    }
                } else {
                    back = nextAi(thisPortNot, ai, ni);
                    if (back) {
                        return true;
                    }
                }
            } else {
                if (thisPort.toString().contains("iopin")) {
                    if (getPortAndCheckConnect(thisPort, ni)) {
                        return true;
                    }
                } else {
                    back = nextAi(thisPort, ai, ni);
                    if (back) {
                        return true;
                    }
                }
            }
        }
        if (pi.toString().contains("P3")) {
            if (useAnotherKeyPin(pi, ni)) {
                back = true;
            }
        }
        return back;
    }

    /**
     * Method to check if arc should be red.
     */
    private static boolean getStarted(PortInst pis) {
        PortProto pp = pis.getPortProto();
        NodeInst ni = pis.getNodeInst();
        Cell interCell = ni.getProtoEquivalent();
        if(interCell == null) {
            return false;  // handle ION and multiplex (can't check internal keys obvious)
        }
        Iterator<Export> itrpp = interCell.getExports();
        while (itrpp.hasNext()) {
            Export exp = itrpp.next();
            if (pp.toString().equals(exp.toString())) {
                PortInst pi = exp.getOriginalPort();
                Iterator<Connection> con = pi.getConnections();
                while (con.hasNext()) {
                    Connection cntn = con.next();
                    ArcInst aiInternal = cntn.getArc();
                    PortInst thisP = aiInternal.getPortInst(0);
                    PortInst thisPNot = aiInternal.getPortInst(1);
                    if (thisP.toString().equals(pi.toString())) {
                        if (thisPNot.toString().contains("iopin")) {
                            if (getPortAndCheckConnect(thisPNot, ni)) {
                                return true;
                            }
                            continue;
                        }
                        if (nextAi(thisPNot, aiInternal, ni)) {
                            return true;
                        }
                    } else {
                        if (thisP.toString().contains("iopin")) {
                            if (getPortAndCheckConnect(thisP, ni)) {
                                return true;
                            }
                            continue;
                        }
                        if (nextAi(thisP, aiInternal, ni)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method to make ArcInst @ai wide and orange,
     *
     * @Param choice is the width of new arc.
     */
    private static void makeWide(ArcInst ai) {
        Cell curcell = Job.getUserInterface().getCurrentCell();
        Iterator<ArcInst> itrArc = curcell.getArcs();
        while (itrArc.hasNext()) {
            ArcInst ainew = itrArc.next();
            if (ainew.toString().equals(ai.toString())) {
                aiList.add(ainew);
                break;
            }
        }
    }

    /**
     * Method to recursively do all arcs connected to each other wide,
     *
     * @Param ais shows the ArcInst we're investigating now,
     * @Param pis shows one of the PortInsts of ais to implement direction.
     */
    private static boolean findMoreArcs(ArcInst ais, PortInst pis, Set<ArcInst> arcList) {
        boolean allRes = false;
        boolean allResForReturn = false;
        boolean isRed;
        makeWide(ais);
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
                if ((!thisPortNot.toString().contains("Wire_Pin")) && (!thisPortNot.toString().contains(".X'")) && (!thisPortNot.toString().contains(".Y'"))) {
                    boolean res = getStarted(thisPortNot);
                    if (res) {
                        allRes = true;
                        arcList.add(ai);
                    }
                    makeWide(ai);
                } else if ((thisPortNot.toString().contains(".X'")) || (thisPortNot.toString().contains(".Y'"))) {
                    boolean res = getStarted(thisPortNot);
                    if (res) {
                        allRes = true;
                    }
                    makeWide(ai);
                    isRed = findMoreArcs(ai, thisPortNot, arcList);
                    if (isRed) {
                        allRes = true;
                    }
                } else {
                    isRed = findMoreArcs(ai, thisPortNot, arcList);
                    if (isRed) {
                        allRes = true;
                    }
                }
            } else {
                if ((!thisPort.toString().contains("Wire_Pin")) && ((!thisPort.toString().contains(".X'")) && (!thisPort.toString().contains(".Y'")))) {
                    boolean res = getStarted(thisPort);
                    if (res) {
                        allRes = true;
                        arcList.add(ai);
                    }
                    makeWide(ai);
                } else if ((thisPort.toString().contains(".X'")) || (thisPort.toString().contains(".Y'"))) {
                    boolean res = getStarted(thisPort);
                    if (res) {
                        allRes = true;
                    }
                    makeWide(ai);
                    isRed = findMoreArcs(ai, thisPort, arcList);
                    if (isRed) {
                        allRes = true;
                    }
                } else {
                    isRed = findMoreArcs(ai, thisPort, arcList);
                    if (isRed) {
                        allRes = true;
                    }
                }
            }

            if (allRes) {
                arcList.add(ais);
                allResForReturn = true;
            }
            allRes = false;
        }

        return allResForReturn;
    }

    /**
     * Controller method to do arcs connected to @TheArc wide and red.
     */
    private static void doMakeWide(ArcInst theArc, PortInst internalPortInst) {
        ArcInst current = theArc;

        Set<ArcInst> arcList = new HashSet<>();
        PortInst thisPort = current.getPortInst(0);
        PortInst thisPortNot = current.getPortInst(1);

        if (thisPort.toString().equals(internalPortInst.toString())) {
            findMoreArcs(current, thisPortNot, arcList);
        } else {
            findMoreArcs(current, thisPort, arcList);
        }

        if ((!thisPort.toString().contains(".wire")) && (!thisPortNot.toString().contains(".wire"))) {
            if (getStarted(thisPort) && getStarted(thisPortNot)) {
                makeWide(current);
                arcList.add(current);
            }
        }

        for (ArcInst aili : arcList) {
            arcRedList.add(aili);
        }
    }

    /**
     * Class for "replaceArc"
     */
    private static class ReplaceArcs extends Job {

        private final Set<ArcInst> aiList;
        private final boolean isRed;

        /**
         *
         */
        public ReplaceArcs(Set<ArcInst> aiList, boolean isRed) {
            super("Replace Schematic Arcs", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.aiList = aiList;
            this.isRed = isRed;
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            System.out.println("Replace Schematic Arcs");
            replace(aiList);
            return true;
        }
        
        private void replace(Set<ArcInst> set) {
            EditingPreferences ep = EditingPreferences.getInstance();
            ArcProto arc = Generic.tech().universal_arc;
            Iterator<ArcInst> itr = set.iterator();
            while (itr.hasNext()) {
                ArcInst ai = itr.next();
                if (!ai.getProto().getName().equals(arc.getName())) {
                    ai.setLambdaBaseWidth(0.5);
                }
                if (isRed) {
                    ai.replace(arc, ep);
                    ai.setLambdaBaseWidth(1);
                }
            }
        }
    }
    
    /**
     * Class for "replaceArc"
     */
    private static class ReplaceArcsForAutotracing extends Job {

        private final transient Set<ArcInst> aiList;
        private final boolean isRed;

        /**
         *
         */
        public ReplaceArcsForAutotracing(Set<ArcInst> aiList, boolean isRed) {
            super("Replace Schematic Arcs", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.aiList = aiList;
            this.isRed = isRed;
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            replace(aiList);
            return true;
        }
        
        private void replace(Set<ArcInst> set) {
            EditingPreferences ep = EditingPreferences.getInstance();
            ArcProto arc = Generic.tech().universal_arc;
            Iterator<ArcInst> itr = set.iterator();
            while (itr.hasNext()) {
                ArcInst ai = itr.next();
                if (!ai.getProto().getName().equals(arc.getName())) {
                    ai.setLambdaBaseWidth(0.5);
                }
                if (isRed) {
                    ai.replace(arc, ep);
                    ai.setLambdaBaseWidth(1);
                }
            }
        }
    }

    /**
     * Class for "ExportKeys"
     */
    public static class ExportAfter extends Job {

        Cell cell;

        /**
         *
         */
        public ExportAfter(Cell cell) {
            super("Export Keys", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.cell = cell;
            System.out.println("ExportAfter");
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            Iterator<Job> itrJob = Job.getAllJobs();
            while (itrJob.hasNext()) {
                Job job = itrJob.next();
                if (job.toString().contains("Create")) {
                    while (!job.isFinished()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            try {
                ExportKeysFromSchemeWithIndication(cell, false);
            } catch (Exception e) {
                System.out.println("Something went wrong.");
            }

            new DeleteUnusedPart.DeleteUnusedArcs(cell);
            return true;
        }
    }
}
