/** Electric(tm) VLSI Design System
 *
 * File: DeleteUnusedPart.java
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
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
 *
 */
package com.sun.electric.tool.scripts;

import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.EditingPreferences;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.tool.user.CircuitChangeJobs;
import com.sun.electric.database.topology.Geometric;
import com.sun.electric.database.variable.ElectricObject;
import com.sun.electric.technology.ArcProto;
import com.sun.electric.technology.technologies.Schematics;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import java.util.concurrent.TimeUnit;
import com.sun.electric.tool.autotracing.Automodelling;

import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.user.User;

/**
 * Class for individual script realization (Delete Unused Part Script), script
 * is deleting blocks without keys, mostly usable in after-autotracing modelling
 * proccess.
 *
 * @author diivanov
 */
public class DeleteUnusedPart {

    private static void deleteUnusedPart(Cell cell) {
        Cell curcell;
        if (cell == null) {
            curcell = Job.getUserInterface().getCurrentCell();
        } else {
            curcell = cell;
        }

        System.out.println("Cell to delete is " + curcell.getName());

        ArrayList<Geometric> deleteList = new ArrayList<>();
        Iterator<NodeInst> itr = curcell.getNodes();

        while (itr.hasNext()) {
            NodeInst z = itr.next();
            String xname = z.getName();
            if ((xname.substring(0, 3).equals("CAU")) || (xname.substring(0, 3).equals("PAU"))
                    || (xname.substring(0, 3).equals("PAD")) || (xname.substring(0, 3).equals("PPC"))
                    || (xname.substring(0, 3).equals("CB<")) || (xname.substring(0, 3).equals("SPM"))) {
                Iterator<PortInst> itr2 = z.getPortInsts();
                int check = 0;
                while (itr2.hasNext()) {
                    PortInst port = itr2.next();
                    String myString = port.toString();
                    String[] parts1 = myString.split("].");
                    String name = parts1[1];
                    if ((name.substring(0, 1).equals("n")) || (name.substring(0, 1).equals("o"))
                            || (name.substring(0, 1).equals("p")) || (name.substring(0, 1).equals("q"))
                            || (name.substring(0, 1).equals("r")) || (name.substring(0, 1).equals("s"))
                            || (name.substring(0, 1).equals("t")) || (name.substring(0, 1).equals("u"))
                            || (name.substring(0, 1).equals("v")) || (name.substring(0, 1).equals("w"))) {
                        if (port.hasConnections()) {
                            check++;
                            break;
                        }
                    }
                }
                if (check == 0) {
                    deleteList.add(z);
                }
            }
        }

        EditingPreferences ep = EditingPreferences.getInstance();
        Set<ElectricObject> stuffToHighlight = new HashSet<>();

        for (Geometric ni : deleteList) {
            if (ni instanceof NodeInst) {
                if (((NodeInst) ni).isLocked()) {
                    ((NodeInst) ni).clearLocked();
                }
            }
        }

        CircuitChangeJobs.eraseObjectsInList(curcell, deleteList,
                true, stuffToHighlight, ep);

    }

    /**
     * Method to create new object that keeps script.
     */
    public static Object doDeleteUnusedPartAfterAutotracing() {
        return new DeleteUnusedPartScript();
    }

    public static void deleteUnusedArcs(Cell cell) {
        Set<ArcInst> arcToDelete = new HashSet<>();
        ArcProto arc = Schematics.tech().wire_arc;
        Iterator<ArcInst> arcItr = cell.getArcs();
        while (arcItr.hasNext()) {
            ArcInst nextArc = arcItr.next();
            if (nextArc.getProto().compareTo(arc) == 0) {
                arcToDelete.add(nextArc);
            }
        }
        for(ArcInst ai : arcToDelete) {
            System.out.println(ai.toString());
        }
        new DeleteArcs(arcToDelete);
    }
    
    private static class DeleteArcs extends Job {

        private final transient Set<ArcInst> arcsToDelete;

        public DeleteArcs(Set<ArcInst> arcsToDelete) {
            super("Delete arcs for autotracing", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.arcsToDelete = arcsToDelete;
            startJob();
        }

        public boolean doIt() throws JobException {
            for (ArcInst ai : arcsToDelete) {
                NodeInst h = ai.getHeadPortInst().getNodeInst();
                NodeInst t = ai.getTailPortInst().getNodeInst();
                ai.kill();

                // also delete freed pin nodes
                if (h.getProto().getFunction().isPin()
                        && !h.hasConnections() && !h.hasExports()) {
                    h.kill();
                }
                if (t.getProto().getFunction().isPin()
                        && !t.hasConnections() && !t.hasExports()) {
                    t.kill();
                }
            }
            System.out.println("Deleted " + arcsToDelete.size() + " arcs");
            return true;
        }
    }
    

    /**
     * Class for "DeleteUnusedPartScript", Thos class realises deleteUnusedPart
     * function.
     */
    private static class DeleteUnusedPartScript extends Job {

        private Cell cell;
        private Job job;

        public DeleteUnusedPartScript() {
            super("Delete Unused Part Script", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            System.out.println("DeleteUnusedPartScript");
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            Iterator<Job> itrJob = Job.getAllJobs();
            while (itrJob.hasNext()) {
                job = itrJob.next();
                if (job.toString().contains("Autotracing") || job.toString().contains("Create New Arc") || job.toString().contains("Delete Unused Arc")) {
                    while (!job.isFinished()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (Exception e) {
                        }
                    }

                }
            }
            cell = Automodelling.getCellFromName(Automodelling.AUTOTRACING_CELL_NAME);
            deleteUnusedPart(cell);
            return true;
        }
    }

    /**
     * Class for "DeleteUnusedArcs"
     */
    public static class DeleteUnusedArcs extends Job {

        Cell cell;

        public DeleteUnusedArcs(Cell cell) {
            super("Delete Unused Arcs", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.cell = cell;
            System.out.println("DeleteUnusedArcs");
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            Iterator<Job> itrJob = Job.getAllJobs();
            while (itrJob.hasNext()) {
                Job job = itrJob.next();
                if (job.toString().contains("Replace")||job.toString().contains("Create")) {
                    while (!job.isFinished()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            
            try {
                deleteUnusedArcs(cell);
            } catch (Exception e) {
            }
            
            doDeleteUnusedPartAfterAutotracing();

            return true;
        }
    }
}
