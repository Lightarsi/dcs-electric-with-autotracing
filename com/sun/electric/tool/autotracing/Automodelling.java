/* Electric(tm) VLSI Design System
 *
 * File: Automodelling.java
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

import com.sun.electric.database.EditingPreferences;
import com.sun.electric.database.hierarchy.Library;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.variable.Variable;

import java.util.Iterator;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

import java.util.concurrent.TimeUnit;

import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.user.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * This class should be used only to prepare model scheme after autotracing
 * proccess.
 *
 * @author diivanov
 */
public class Automodelling {

    /**
     * Name of library and first-cell, first-cell should not be changed or
     * deleted.
     */
    public static final String CELL_NAME = "5400TP035";

    /**
     * Name of new cell in which autotracing proccess will be done.
     */
    public static final String AUTOTRACING_CELL_NAME = CELL_NAME + "_autotracing";

    /**
     * Private constructor to avoid creating objects of this class.
     */
    private Automodelling() {
        throw new AssertionError();
    }

    /**
     * Method creates object of class with function of preparing model scheme.
     */
    public static void modelScheme() {
        new CreateNewAutotracingSchemeAndImport();
    }

    /**
     * Method to get Cell if it exists in Library.
     *
     * @param cellName
     * @return
     */
    public static Cell getCellFromName(String cellName) {
        Iterator<Library> itrLib = Library.getLibraries();
        while (itrLib.hasNext()) {
            Library lib = itrLib.next();
            if (lib.getName().equals(CELL_NAME)) {
                Iterator<Cell> itrCell = lib.getCells();
                while (itrCell.hasNext()) {
                    Cell cell = itrCell.next();
                    if (cell.getName().equals(cellName)) {
                        return cell;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Method to write simulation path for model scheme.
     */
    private static void writeSimPath(Cell cell) throws IOException {
        java.lang.String cellnam = cell.getName();
        Library curlib = cell.getLibrary();
        java.lang.String libnam = curlib.getName();
        String workdir = User.getWorkingDirectory();

        try (PrintWriter fout = new PrintWriter(new FileWriter("C:\\CYGELENG\\scripts\\SimPath.txt"))) {
            fout.println(workdir);
            fout.println(workdir + "\\" + cellnam + ".spi");
            fout.println(libnam);
            fout.println(cellnam);
        }
    }

    /**
     * Method to write all spm keys to file.
     */
    public static void writeSPMkeys() {
        HashSet<Pair<NodeInst, String>> SPMList = AuxilarySimpleAutotracing.getAuxilaryOnlyObject().getSPMList();

        for (Pair<NodeInst, String> pair : SPMList) {
            NodeInst ni = pair.getFirstObject();
            String strBlock = pair.getSecondObject();
            Cell interCell = ni.getProtoEquivalent();
            //node symbol:SPM{ic}['SPM@1']
            //port '5400TP035:SPM<8502{ic}[SPM<8502@1].Y6'
            int SPMnumber = Integer.valueOf(Accessory.parsePortToNumber(strBlock));
            Iterator<NodeInst> nodItr = interCell.getNodes();
            NodeInst spmInst = nodItr.next();
            Accessory.printLog("expected " + spmInst);
            while (nodItr.hasNext()) {
                NodeInst unexpectedNod = nodItr.next();
                Accessory.printLog("unexpected " + unexpectedNod.toString());
            }
            Iterator<PortInst> itrpp = spmInst.getPortInsts();
            while (itrpp.hasNext()) {
                PortInst pi = itrpp.next();
                if (pi.hasConnections()) {
                    String piStr = Accessory.parsePortToPortOld(pi.toString());
                    Pattern spm = Pattern.compile("[nopqrstuvw]\\d+\\d*");
                    if (spm.matcher(piStr).matches()) {
                        String num = piStr.substring(1, piStr.length());
                        int numInt = Integer.valueOf(num);
                        String letter = piStr.substring(0, 1);
                        if (numInt % 2 == 0) {
                            int newNum = (numInt / 2) - 1;
                            Integer value = Accessory.explainSPMLetter(letter);
                            Integer result = SPMnumber + value + newNum;
                            Accessory.write(Accessory.CONFIG_PATH, (String.valueOf(result)));
                        }
                    }
                }
            }
        }
    }

    /**
     * Class for "CreateNewArc", this class implements all methods to prepare
     * model scheme after autotracing proccess.
     */
    private static class CreateNewAutotracingSchemeAndImport extends Job {

        private Cell cell;
        private Job job;

        public CreateNewAutotracingSchemeAndImport() {
            super("Create New Autotracing Scheme And Import", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            Iterator<Job> itrJob = Job.getAllJobs();
            while (itrJob.hasNext()) {
                job = itrJob.next();
                if (job.toString().contains("Duplicate")) {
                    while (!job.isFinished()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            this.cell = Automodelling.getCellFromName(AUTOTRACING_CELL_NAME);
            try {
                ImportKeys.controller(cell);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            try {
                writeSimPath(cell);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            addSourcesToSchemeAsSpiceCode(cell);
            addModellingParametersToModelScheme(cell);

            return true;
        }

        private static void addSourcesToSchemeAsSpiceCode(Cell cell) {
            ArrayList<String> sourceList = AuxilarySimpleAutotracing.getAuxilaryOnlyObject().getSourceCodes();
            if (sourceList.isEmpty()) {
                return;
            } else {
                NodeInst sourceNode = null;
                Iterator<NodeInst> itrNod = cell.getNodes();
                while (itrNod.hasNext()) {
                    NodeInst ni = itrNod.next();
                    if (ni.toString().contains("sourcesD")) {
                        sourceNode = ni;
                        break;
                    }
                }
                if (sourceNode != null) {
                    String output = "";
                    for (String source : sourceList) {
                        output += source;
                        output += "\n";
                    }

                    EditingPreferences ep = EditingPreferences.getInstance();
                    Iterator<Variable> itrVar = sourceNode.getParametersAndVariables(); // Shouldn't be iterator here coz only 1 parameter, did it to develop after
                    while (itrVar.hasNext()) {
                        Variable var = itrVar.next();
                        sourceNode.updateVar(var.getKey(), output, ep);
                    }
                }
            }
        }

        private void addModellingParametersToModelScheme(Cell autoCell) {

            Cell basicCell = Job.getUserInterface().getCurrentCell();

            NodeInst paramNode_1 = null;
            NodeInst paramNode_2 = null;
            NodeInst modelParamNode = null;
            Iterator<NodeInst> itrNod = autoCell.getNodes();
            // there is a patch
            while (itrNod.hasNext()) {

                NodeInst ni = itrNod.next();
                if (ni.toString().contains("5400TP035_core{")) {
                    paramNode_1 = ni;
                }
                if (ni.toString().contains("5400TP035_core_ac")) {
                    paramNode_2 = ni;
                }
            }

            itrNod = basicCell.getNodes();
            while (itrNod.hasNext()) {
                NodeInst ni = itrNod.next();
                if (ni.toString().contains("5400TP035_core")) {
                    modelParamNode = ni;
                    break;
                }
            }
            assert paramNode_1 != null;
            assert modelParamNode != null;

            if (paramNode_2 != null) {
                if (((paramNode_1.toString().contains("5400TP035_core{")) && (modelParamNode.toString().contains("5400TP035_core{")))) {
                    paramNode_2.kill();
                    paramNode_1.copyVarsFrom(modelParamNode);
                }
                if (((paramNode_2.toString().contains("5400TP035_core_ac")) && (modelParamNode.toString().contains("5400TP035_core_ac")))) {
                    paramNode_1.kill();
                    paramNode_2.copyVarsFrom(modelParamNode);
                }
            } else {
                paramNode_1.copyVarsFrom(modelParamNode);
            }

            /* while (itrNod.hasNext()) {
                NodeInst ni = itrNod.next();
                if (ni.toString().contains("5400TP035_core")) {
                    paramNode = ni;
                    break;
                }
            }
            
            
            itrNod = basicCell.getNodes();
            while (itrNod.hasNext()) {
                NodeInst ni = itrNod.next();
                if (ni.toString().contains("5400TP035_core")) {
                    modelParamNode = ni;
                    break;
                }
            }
            assert paramNode != null;
            assert modelParamNode != null;
            if (paramNode == modelParamNode) {
                paramNode.copyVarsFrom(modelParamNode);
            } else {
                paramNode.replace(modelParamNode.getProto(), ep, true, true, true);
                paramNode.copyVarsFrom(modelParamNode);
            }*/
        }
    }
}
