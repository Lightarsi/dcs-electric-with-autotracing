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

import com.sun.electric.database.topology.PortInst;
import com.sun.electric.technology.ArcProto;
import com.sun.electric.technology.technologies.Generic;
import com.sun.electric.database.EditingPreferences;

import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.hierarchy.Export;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.Connection;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.variable.Variable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

import java.util.Iterator;

import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.user.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 * Class uses ImportKeys script algorithm to import keys to scheme from file.
 */
public class ImportKeys {

    private static float getStringCount(File file) {
        float qr = 0;
        BufferedReader bufferedReader;
        try {
            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.readLine() != null) {
                qr += 1;
            }
            bufferedReader.close();
            return qr;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return 0;
    }

    // just sort
    private static int equality(int a, int b, int keyNumber) {
        if ((a >= b) && (a <= keyNumber)) {
            return a;
        } else {
            return b;
        }
    }
    // find diff bw key and instance

    private static int keyFind(int keyNumber, String cname) {
        String[] parts = cname.split("ic");
        String part1 = parts[0];
        char[] charArray = part1.toCharArray();
        int i = 0;
        while (Character.isLetter(charArray[i])) {
            i++;
        }
        String cnumber = "";
        for (i++; i < (charArray.length - 2); i++) {
            cnumber += charArray[i];
        }
        int keyFind = keyNumber - Integer.parseInt(cnumber);
        int keyin = keyFind * 2 + 1;
        return keyin;
    }
    // find instance which is the closest to key but less or equal

    private static int findInst(int alis) {
        int bika = 0;
        String cname;
        try {
            BufferedReader br2 = new BufferedReader(new FileReader("c:\\CYGELENG\\electric\\global_scheme.info"));
            String line;
            while ((line = br2.readLine()) != null) {
                cname = line;
                String[] parts = cname.split("<");
                String part2 = parts[1];
                bika = equality(Integer.parseInt(part2), bika, alis);
            }
            br2.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return bika;
    }
    // return the name of instance that key belongs to

    private static String findbika(int bika) {
        int i = 0;
        String cname;
        try {
            BufferedReader br3 = new BufferedReader(new FileReader("c:\\CYGELENG\\electric\\global_scheme.info"));
            String line;
            while ((line = br3.readLine()) != null) {
                cname = line;
                String[] parts = cname.split("<");
                String part2 = parts[1];
                if (Integer.parseInt(part2) == bika) {
                    //return i;
                    return cname;
                }
                i++;
            }
            br3.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    /**
     * Method to import keys from file. user is specifying file.
     *
     * @param cell
     * @throws java.io.IOException
     */
    public static void controller(Cell cell) throws IOException {
        System.out.println(Thread.currentThread().getName());
        HashMap<PortInst, PortInst> arcsToImport = new HashMap<>();
        //System.out.println("Script started");
        boolean nextAuto = true;
        String DirF = Accessory.CONFIG_PATH;
        // Return cell in given window
        Cell curcell;
        if (cell == null) {
            nextAuto = false;
            curcell = Job.getUserInterface().getCurrentCell();
            JFileChooser chooser = new JFileChooser();
            File Dir = new File("c:\\CYGELENG\\config");
            chooser.setCurrentDirectory(Dir);
            chooser.setDialogTitle("Import Keys");
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                System.out.println("CurrentDirectory: " + chooser.getCurrentDirectory());
                System.out.println("SelectedFile: " + chooser.getSelectedFile());
                DirF = chooser.getSelectedFile().getAbsolutePath();
                if (!DirF.contains(".txt")) {
                    System.out.println("Wrong File ");
                    System.out.println(DirF);
                    return;
                }
            } else {
                System.out.println("No Selection ");
                return;
            }
        } else {
            curcell = cell;
        }

        File f = new File(DirF);
        float count = getStringCount(f);

        int k = 0;
        double size = 0.5;
        String[] key = new String[50000];
        int keyquant;
        String keyName;
        // each string from import.txt is checking for key and instance(from global_scheme)
        try (BufferedReader br = new BufferedReader(new FileReader(DirF))) {
            float count_cont = 0;
            int percentage_last = 0;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                boolean needExtended = true;
                // That isn't really good solution coz it's always exception when key is from ++ version.
                try {
                    key[k] = line;
                    keyquant = findInst(Integer.parseInt(key[k]));
                    keyName = findbika(keyquant);
                    int diff_import = Integer.parseInt(key[k]) - keyquant;
                    String diff_key_not_SPM = ("n" + ((Integer.parseInt(key[k]) - keyquant) * 2 + 1));
                    String diff_key_not_SPM_1 = ("n" + ((Integer.parseInt(key[k]) - keyquant) * 2 + 2));
                    String SPM_check = keyName.substring(0, 3);
                    String diff_key_SPM = "";
                    String diff_key_SPM_1 = "";
                    if (SPM_check.equals("SPM")) {
                        if (diff_import <= 107) {
                            diff_key_SPM = diff_key_not_SPM;
                            diff_key_SPM_1 = diff_key_not_SPM_1;
                        } else if ((diff_import > 107) && (diff_import <= 279)) {
                            diff_key_SPM = ("o" + (((Integer.parseInt(key[k]) - keyquant) - 108) * 2 + 1));
                            diff_key_SPM_1 = ("o" + (((Integer.parseInt(key[k]) - keyquant) - 108) * 2 + 2));
                        } else if ((diff_import > 279) && (diff_import <= 507)) {
                            diff_key_SPM = ("p" + (((Integer.parseInt(key[k]) - keyquant) - 280) * 2 + 1));
                            diff_key_SPM_1 = ("p" + (((Integer.parseInt(key[k]) - keyquant) - 280) * 2 + 2));
                        } else if ((diff_import > 507) && (diff_import <= 679)) {
                            diff_key_SPM = ("q" + (((Integer.parseInt(key[k]) - keyquant) - 508) * 2 + 1));
                            diff_key_SPM_1 = ("q" + (((Integer.parseInt(key[k]) - keyquant) - 508) * 2 + 2));
                        } else if ((diff_import > 679) && (diff_import <= 907)) {
                            diff_key_SPM = ("r" + (((Integer.parseInt(key[k]) - keyquant) - 680) * 2 + 1));
                            diff_key_SPM_1 = ("r" + (((Integer.parseInt(key[k]) - keyquant) - 680) * 2 + 2));
                        } else if ((diff_import > 907) && (diff_import <= 1079)) {
                            diff_key_SPM = ("s" + (((Integer.parseInt(key[k]) - keyquant) - 908) * 2 + 1));
                            diff_key_SPM_1 = ("s" + (((Integer.parseInt(key[k]) - keyquant) - 908) * 2 + 2));
                        } else if ((diff_import > 1079) && (diff_import <= 1307)) {
                            diff_key_SPM = ("t" + (((Integer.parseInt(key[k]) - keyquant) - 1080) * 2 + 1));
                            diff_key_SPM_1 = ("t" + (((Integer.parseInt(key[k]) - keyquant) - 1080) * 2 + 2));
                        } else if ((diff_import > 1307) && (diff_import <= 1479)) {
                            diff_key_SPM = ("u" + (((Integer.parseInt(key[k]) - keyquant) - 1308) * 2 + 1));
                            diff_key_SPM_1 = ("u" + (((Integer.parseInt(key[k]) - keyquant) - 1308) * 2 + 2));
                        } else if ((diff_import > 1479) && (diff_import <= 1707)) {
                            diff_key_SPM = ("v" + (((Integer.parseInt(key[k]) - keyquant) - 1480) * 2 + 1));
                            diff_key_SPM_1 = ("v" + (((Integer.parseInt(key[k]) - keyquant) - 1480) * 2 + 2));
                        } else if ((diff_import > 1707) && (diff_import <= 1815)) {
                            diff_key_SPM = ("w" + (((Integer.parseInt(key[k]) - keyquant) - 1708) * 2 + 1));
                            diff_key_SPM_1 = ("w" + (((Integer.parseInt(key[k]) - keyquant) - 1708) * 2 + 2));
                        }
                    }

                    NodeInst z;
                    Iterator<NodeInst> nodeItr = curcell.getNodes();
                    while (nodeItr.hasNext()) {
                        z = nodeItr.next();
                        if (z.getProto().getName().equals(keyName)) {
                            int expnum = z.getNumPortInsts();
                            int j = 0;
                            while (j < expnum) {
                                PortInst z_exp1 = z.getPortInst(j);
                                j++;
                                String myString = z_exp1.toString();
                                String[] parts1 = myString.split("].");
                                String name1 = parts1[1];
                                if (name1.equals(diff_key_not_SPM + "'")) {
                                    int q = 0;
                                    while (q < expnum) {
                                        PortInst z_exp2 = z.getPortInst(q);
                                        String myString2 = z_exp2.toString();
                                        String[] parts2 = myString2.split("].");
                                        String name2 = parts2[1];
                                        if (name2.equals(diff_key_not_SPM_1 + "'")) {
                                            //ArcProto arc = Generic.tech().universal_arc;
                                            //new CreateNewArc(arc, z_exp1, z_exp2, size);
                                            arcsToImport.put(z_exp1, z_exp2);
                                            needExtended = false;
                                        }
                                        q++;
                                    }
                                }
                                if (name1.equals(diff_key_SPM + "'")) {
                                    int q = 0;
                                    while (q < expnum) {
                                        PortInst z_exp2 = z.getPortInst(q);
                                        String myString2 = z_exp2.toString();
                                        String[] parts2 = myString2.split("].");
                                        String name2 = parts2[1];
                                        if (name2.equals(diff_key_SPM_1 + "'")) {
                                            //ArcProto arc = Generic.tech().universal_arc;
                                            //Job job = new CreateNewArc(arc, z_exp1, z_exp2, size);
                                            arcsToImport.put(z_exp1, z_exp2);
                                            needExtended = false;
                                            /*while (job.getStatus().equals("done")) {
                                        }*/
                                        }
                                        q++;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    needExtended = true;
                }
                if (needExtended) {
                    //Accessory.printLog("needExtended");
                    int keyNum = Integer.valueOf(key[k]);
                    Iterator<NodeInst> niItr = curcell.getNodes();
                    while (niItr.hasNext()) {
                        NodeInst node = niItr.next();
                        NodeInst keyNode = getKeyInNodeWithNumber(node, keyNum);
                        //Accessory.printLog(node.toString());
                        if (keyNode != null) {
                            //Accessory.printLog(keyNode.toString());
                        } else {
                            //Accessory.printLog("null");
                        }

                        if (keyNode != null) {
                            try {
                                closeKey(node, keyNode);
                            } catch (FunctionalException fe) {
                                fe.printStackTrace();
                            }

                        }
                    }
                }

                count_cont += 1;
                if (percentage_last != ((int) ((count_cont / count) * 100))) {
                    System.out.println((int) ((count_cont / count) * 100) + "%");
                    percentage_last = ((int) ((count_cont / count) * 100));
                }

            }
        }
        System.out.println("Starting import to scheme");
        ArcProto arc = Generic.tech().universal_arc;
        Job job = new CreateLotsOfNewArcs(arc, arcsToImport, size);
        if (nextAuto) {
            new NextStepForAutotracing(cell);
        }

    }

    private static NodeInst getKeyInNodeWithNumber(NodeInst node, int numberOfKey) {
        Accessory.printLog(node.toString());
        if (!node.toString().contains("{ic}")) {
            return null;
        } else if (!node.toString().contains("<")) {
            return null;
        }

        String nameOfNode = Accessory.parseNodeToBlock(node.toString());

        String[] split = nameOfNode.split("<");
        // name should be smth like SPM<13345 or returning NULL

        int nodeNum = Integer.valueOf(split[1]);
        //Accessory.printLog(String.valueOf(nodeNum));
        Cell internalCell = node.getProtoEquivalent();
        Iterator<NodeInst> internalNodesItr = internalCell.getNodes();
        while (internalNodesItr.hasNext()) {
            NodeInst ifKeyNode = internalNodesItr.next();
            if (!ifKeyNode.toString().contains("{ic}")) {
                continue;
            }
            //Accessory.printLog("if " + ifKeyNode.toString());
            String nameOfKey = Accessory.parseNodeToName(ifKeyNode.toString());
            int keyNum;
            //Accessory.printLog("nameOfKey " + nameOfKey);
            if (nameOfKey.contains("keyFull")) {
                keyNum = Integer.valueOf(nameOfKey.split(">")[1]);
                if (keyNum + nodeNum == numberOfKey) {
                    return ifKeyNode;
                }
            }
        }
        return null;
    }

    /*
    * Method to show if the key is closed or not.
    * THERE SHOULDN'T BE MORE THAN 1 
    * @Param key SHOULD BE ONLY KEY, ONLY WITH PORTS X,Y,M1,M2.
     */
    private static boolean closeKey(NodeInst ni, NodeInst key) throws FunctionalException {
        Iterator<PortInst> itrPorts = key.getPortInsts();
        PortInst outsidePort1 = null;
        PortInst outsidePort2 = null;
        while (itrPorts.hasNext()) {
            PortInst pi = itrPorts.next();
            String port = Accessory.parsePortToPort(pi.toString());
            //Accessory.printLog("port " + port);
            if (port.equals("P2'")) {
                //Accessory.printLog(pi.getNodeInst().toString());
                Iterator<Connection> ctnItr = pi.getConnections();
                Connection ctn = getOnlyIteratorObject(ctnItr);
                ArcInst ai = ctn.getArc();
                //Accessory.printLog("arc " + ai.toString());

                Connection ctnTail = ai.getConnection(0);
                Connection ctnHead = ai.getConnection(1);
                Connection ctnNext;
                if (ctn.toString().equals(ctnTail.toString())) {
                    ctnNext = ctnHead;
                } else {
                    ctnNext = ctnTail;
                }

                PortInst outPort = ctnNext.getPortInst();

                //Accessory.printLog("outPort " + outPort.toString());
                Export outExport = getOnlyIteratorObject(outPort.getExports());
                outsidePort1 = ni.findPortInstFromEquivalentProto(outExport);

                //Accessory.printLog("outsidePort1 " + outsidePort1.toString());

                /*double size = 0.5;
                ArcProto arc = Generic.tech().universal_arc;
                new CreateNewArc(arc, outsidePort, secondPort, size);*/
            } else if (port.equals("P1'")) {
                System.out.println(pi.getNodeInst().toString());
                Iterator<Connection> ctnItr = pi.getConnections();
                while (ctnItr.hasNext()) {
                    Connection ctn = ctnItr.next();
                    ArcInst ai = ctn.getArc();
                    Connection ctnTail = ai.getConnection(0);
                    Connection ctnHead = ai.getConnection(1);
                    Connection ctnNext;
                    if (ctn.toString().equals(ctnTail.toString())) {
                        ctnNext = ctnHead;
                    } else {
                        ctnNext = ctnTail;
                    }
                    PortInst outPort = ctnNext.getPortInst();

                    //Accessory.printLog("outPort " + outPort.toString());
                    try {
                        Export outExport = getOnlyIteratorObject(outPort.getExports());
                        outsidePort2 = ni.findPortInstFromEquivalentProto(outExport);
                        //Accessory.printLog("outsidePort2 " + outsidePort2.toString());
                    } catch (Exception e) {
                        //Accessory.printLog("not this port");
                    }
                }
            }
        }

        if ((outsidePort1 != null) && (outsidePort2 != null)) {
            double size = 0.5;
            ArcProto arc = Generic.tech().universal_arc;
            new CreateNewArc(arc, outsidePort1, outsidePort2, size);
            return true;
        }

        return false;
    }

    private static String getOnlyParamOfNodeInst(NodeInst ni) throws FunctionalException {
        ArrayList<String> paramList = new ArrayList<>();
        Iterator<Variable> varItr = ni.getParameters();
        while (varItr.hasNext()) {
            Variable var = varItr.next();
            paramList.add(var.getObject().toString());
            System.out.println("var " + var.getObject().toString());
        }
        if (paramList.size() != 1) {
            throw new FunctionalException("There shouldn't be more than one parameters for global blocks");
        }
        return paramList.get(0);
    }

    private static <A, B extends Iterator<A>> A getOnlyIteratorObject(B iterator) throws FunctionalException {
        ArrayList<A> objectsList = new ArrayList<>();
        while (iterator.hasNext()) {
            objectsList.add(iterator.next());
        }
        if (objectsList.size() != 1) {
            throw new FunctionalException("More than one object in iterator");
        }
        return objectsList.get(0);
    }

    /**
     * Class for "startImportJob", class launches import of keys.
     */
    public static class startImportJob extends Job {

        public startImportJob() {
            super("Import Keys", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            try {
                ImportKeys.controller(null);
            } catch (IOException ex) {
                Logger.getLogger(ImportKeys.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
    }

    /**
     * Class for "CreateNewArc", class realises createNewArc Job to avoid
     * "database changes are forbidden" error.
     */
    private static class CreateNewArc extends Job {

        ArcProto ap;
        double size;
        PortInst firstPort;
        PortInst secondPort;

        public CreateNewArc(ArcProto arc, PortInst firstPort, PortInst secondPort, double size) {
            super("Create New Arc", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.ap = arc;
            this.firstPort = firstPort;
            this.secondPort = secondPort;
            this.size = size;
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            EditingPreferences ep = EditingPreferences.getInstance();
            ArcInst newArc = ArcInst.makeInstance(ap, ep, firstPort, secondPort);
            newArc.setLambdaBaseWidth(size);
            return true;
        }
    }

    /**
     * Class for "CreateNewArc", class realises createNewArc Job to avoid
     * "database changes are forbidden" error.
     */
    private static class NextStepForAutotracing extends Job {

        Cell cell;

        public NextStepForAutotracing(Cell cell) {
            super("Next Step For Autotracing", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.cell = cell;
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            Iterator<Job> itrJob = Job.getAllJobs();
            while (itrJob.hasNext()) {
                Job job = itrJob.next();
                if (job.toString().contains("Create New Arc")) {
                    while (!job.isFinished()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            new ExportKeys.ExportAfter(cell);
            return true;
        }
    }

    /**
     * Class for "CreateNewArc", class realises createNewArc Job to avoid
     * "database changes are forbidden" error.
     */
    private static class CreateLotsOfNewArcs extends Job {

        private ArcProto ap;
        private double size;
        private final HashMap<PortInst, PortInst> mapOfPortPairs;

        public CreateLotsOfNewArcs(ArcProto arc, HashMap<PortInst, PortInst> setOfPortPairs, double size) {
            super("Create New Arc", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.ap = arc;
            this.mapOfPortPairs = setOfPortPairs;
            this.size = size;
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            EditingPreferences ep = EditingPreferences.getInstance();
            mapOfPortPairs.entrySet().stream().map((entry) -> {
                PortInst port1 = entry.getKey();
                PortInst port2 = entry.getValue();
                ArcInst newArc = ArcInst.makeInstance(ap, ep, port1, port2);
                return newArc;
            }).forEachOrdered((newArc) -> {
                newArc.setLambdaBaseWidth(size);
            });
            /*mapOfPortPairs.stream().map((portsPair) -> {
                PortInst firstPort = portsPair.getFirstObject();
                PortInst secondPort = portsPair.getSecondObject();
                ArcInst newArc = ArcInst.makeInstance(ap, ep, firstPort, secondPort);
                return newArc;
            }).forEachOrdered((newArc) -> {
                newArc.setLambdaBaseWidth(size);
            });*/
            return true;
        }
    }

}
