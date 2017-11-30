/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: SeaOfGatesHandlers.java
 *
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.electric.tool.routing.seaOfGates;

import com.sun.electric.database.EditingPreferences;
import com.sun.electric.database.Snapshot;
import com.sun.electric.database.constraint.Layout;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.hierarchy.EDatabase;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.variable.UserInterface;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.routing.Routing;
import com.sun.electric.tool.routing.Routing.SoGContactsStrategy;
import com.sun.electric.tool.routing.SeaOfGates;
import com.sun.electric.tool.routing.seaOfGates.SeaOfGatesEngine.RouteResolution;
import com.sun.electric.tool.user.ErrorLogger;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Factory class that provides #SeaOfGatesEngin.Handler's .
 */
public class SeaOfGatesHandlers {

    /**
     * Save mode determines how to save changes. 
     */
    public enum Save {
        SAVE_ONCE, SAVE_PERIODIC, SAVE_SNAPSHOTS
    };
    /** Default save mode. */
    private static final Save SAVE_DEFAULT = Save.SAVE_PERIODIC;

    /** Instances of class are not allowed */
    private SeaOfGatesHandlers() {
    }

    /**
     * Start routine in a Job with default save and save arcs modes
     * @param cell Cell to route
     * @param selected ArcInsts to route or null to route all unrouted arcs in the Cell
     * @param version version of SeaOfGatesEngine
     */
    public static void startInJob(Cell cell, Collection<ArcInst> selected, SeaOfGatesEngineFactory.SeaOfGatesEngineType version) {
        startInJob(cell, selected, version, SAVE_DEFAULT);
    }

    /**
     * Start routine in a Job with default save and save arcs modes
     * @param cell Cell to route
     * @param selected ArcInsts to route or null to route all unrouted arcs in the Cell
     * @param version version of SeaOfGatesEngine
     * @param save mode to save changes
     */
    public static void startInJob(Cell cell, Collection<ArcInst> selected, SeaOfGatesEngineFactory.SeaOfGatesEngineType version,
            Save save) {
        // Run seaOfGatesRoute on selected unrouted arcs
        // do the routing in a separate job
        new SeaOfGatesJob(cell, selected, version, save).startJob();
    }

    /**
     * Returns Job handler with default Save mode and default SaveArcs mode
     * @param job executing Job or null to save in raw database
     * @param ep EditingPreferences
     */
    public static SeaOfGatesEngine.Handler getDefault(Cell cell, String resultCellName, SoGContactsStrategy contactPlacementAction, Job job, EditingPreferences ep) {
        return getDefault(cell, resultCellName, contactPlacementAction, job, ep, SAVE_DEFAULT);
    }

    /**
     * Returns Job handler with default Save mode and specified SaveArcs mode
     * @param job executing Job or null to save in raw database
     * @param ep EditingPreferences
     * @param save specified Save mode
     */
    public static SeaOfGatesEngine.Handler getDefault(Cell cell, String resultCellName, SoGContactsStrategy contactPlacementAction, Job job, EditingPreferences ep, Save save) {
        return new DefaultSeaOfGatesHook(cell, resultCellName, contactPlacementAction, job, ep, save);
    }

    /**
     * Returns dummy handler
     */
    public static SeaOfGatesEngine.Handler getDummy(EditingPreferences ep, PrintStream out) {
        return new DummySeaOfGatesHandler(ep, out);
    }

    /**
     * Class to run sea-of-gates routing in a separate Job.
     */
    private static class SeaOfGatesJob extends Job {

        private final Cell cell;
        private final int[] arcIdsToRoute;
        private final SeaOfGates.SeaOfGatesOptions prefs = new SeaOfGates.SeaOfGatesOptions();
        private final SeaOfGatesEngineFactory.SeaOfGatesEngineType version;
        private final Save save;

        protected SeaOfGatesJob(Cell cell, Collection<ArcInst> arcsToRoute, SeaOfGatesEngineFactory.SeaOfGatesEngineType version,
                Save save) {
            super("Sea-Of-Gates Route", Routing.getRoutingTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.cell = cell;
            if (arcsToRoute != null) {
                arcIdsToRoute = new int[arcsToRoute.size()];
                Iterator<ArcInst> it = arcsToRoute.iterator();
                for (int i = 0; i < arcsToRoute.size(); i++) {
                    arcIdsToRoute[i] = it.next().getArcId();
                }
            } else {
                arcIdsToRoute = null;
            }
            prefs.getOptionsFromPreferences(false);
//            prefs.resultCellName = "dummyResult;1{lay}";
            this.version = version;
            this.save = save;
        }

        @Override
        public boolean doIt() throws JobException {
            SeaOfGatesEngine router = SeaOfGatesEngineFactory.createSeaOfGatesEngine(version);
            router.setPrefs(prefs);
            Layout.changesQuiet(true);
            SeaOfGatesEngine.Handler handler = getDefault(cell, router.getPrefs().resultCellName, router.getPrefs().contactPlacementAction, this, getEditingPreferences(), save);
            if (arcIdsToRoute != null) {
                List<ArcInst> arcsToRoute = new ArrayList<ArcInst>();
                for (int arcId : arcIdsToRoute) {
                    arcsToRoute.add(cell.getArcById(arcId));
                }
                router.routeIt(handler, cell, false, arcsToRoute);
            } else {
                router.routeIt(handler, cell, false);
            }
            return true;
        }

        @Override
        public void showSnapshot() {
            if (save != Save.SAVE_ONCE) {
                super.showSnapshot();
            }
        }
    }

    private static class DefaultSeaOfGatesHook implements SeaOfGatesEngine.Handler {

        private final EDatabase database;
        private final Job job;
        private final EditingPreferences ep;
        private final Save save;
        private final SeaOfGatesCellBuilder cellBuilder;
        private final UserInterface ui = Job.getUserInterface();
        private int periodicCounter;

        private DefaultSeaOfGatesHook(Cell cell, String resultCellName, SoGContactsStrategy contactPlacementAction, Job job, EditingPreferences ep, Save save) {
            this.database = cell.getDatabase();
            this.job = job;
            this.ep = ep;
            this.save = save;
            cellBuilder = new SeaOfGatesCellBuilder(database.backup(), cell.getId(), resultCellName, contactPlacementAction, ep);
            periodicCounter = 0;
        }

        /**
         * Returns EditingPreferences
         * @return EditingPreferences
         */
        @Override
        public EditingPreferences getEditingPreferences() {
            return ep;
        }

        /**
         * Check if we are scheduled to abort. If so, print message if non null and
         * return true.
         * @return true on abort, false otherwise. If job is scheduled for abort or
         *         aborted. and it will report it to standard output
         */
        @Override
        public boolean checkAbort() {
            return job != null && job.checkAbort();
        }

        /**
         * Log a message at the TRACE level.
         *
         * @param msg the message string to be logged
         */
        @Override
        public void trace(String msg) {
            printMessage(msg, true);
        }

        /**
         * Log a message at the DEBUG level.
         *
         * @param msg the message string to be logged
         */
        @Override
        public void debug(String msg) {
            if (Job.getDebug()) {
                printMessage(msg, true);
            }
        }

        /**
         * Log a message at the INFO level.
         *
         * @param msg the message string to be logged
         */
        @Override
        public void info(String msg) {
            printMessage(msg, true);
        }

        /**
         * Log a message at the WARN level.
         *
         * @param msg the message string to be logged
         */
        @Override
        public void warn(String msg) {
            printMessage("WARNING: " + msg, true);
        }

        /**
         * Log a message at the ERROR level.
         *
         * @param msg the message string to be logged
         */
        @Override
        public void error(String msg) {
            printMessage("ERROR: " + msg, true);
        }

        private void printMessage(String s, boolean newLine) {
            if (newLine) {
                System.out.println(s);
            } else {
                System.out.print(s);
            }
        }

        /**
         * Method called when all errors are logged.  Initializes pointers for replay of errors.
         */
        @Override
        public void termLogging(ErrorLogger errorLogger) {
            errorLogger.termLogging(true);
        }

        /**
         * Method to start the display of a progress dialog.
         * @param msg the message to show in the progress dialog.
         */
        @Override
        public void startProgressDialog(String msg) {
            ui.startProgressDialog(msg, null);
        }

        /**
         * Method to stop the progress bar
         */
        @Override
        public void stopProgressDialog() {
            ui.stopProgressDialog();
        }

        /**
         * Method to set a text message in the progress dialog.
         * @param message the new progress message.
         */
        @Override
        public void setProgressNote(String message) {
            ui.setProgressNote(message);
            if (Job.getDebug())
            	System.out.println(message);
        }

        /**
         * Method to update the progress bar
         * @param pct the percentage done (from 0 to 100).
         */
        @Override
        public void setProgressValue(long done, long total) {
        	int val = (int) (done * 100 / total);
            ui.setProgressValue(val);
        }

        /**
         * Method to instantiate RouteResolution
         * Can be called from any thread.
         * @param resolution RouteResolution
         */
        @Override
        public void instantiate(RouteResolution resolution) {
            cellBuilder.instantiate(resolution);
        }

        /**
         * flush changes
         * @param force unconditionally perform the final flush
         * Can be called only from database thread
         */
        @Override
        public void flush(boolean force) {
        	switch (save)
        	{
        		case SAVE_SNAPSHOTS:
        			force = true;
        			break;
        		case SAVE_PERIODIC:
        			if (periodicCounter++ > 100)
        			{
        				periodicCounter = 0;
        				force = true;
        			}
        			break;
        	}
            if (force) {
                Snapshot snapshot = cellBuilder.commit();

                database.checkChanging();
                database.lowLevelSetCanUndoing(true);
                database.undo(snapshot);
                database.lowLevelSetCanUndoing(false);
                database.getCell(cellBuilder.cellId).getLibrary().setChanged();
                if (job instanceof SeaOfGatesJob) {
                    ((SeaOfGatesJob) job).showSnapshot();
                }
            }
        }
    }

    private static class DummySeaOfGatesHandler implements SeaOfGatesEngine.Handler {

        private final EditingPreferences ep;
        private final PrintStream out;

        private DummySeaOfGatesHandler(EditingPreferences ep, PrintStream out) {
            this.ep = ep;
            this.out = out;
        }

        /**
         * Returns EditingPreferences
         * @return EditingPreferences
         */
        @Override
        public EditingPreferences getEditingPreferences() {
            return ep;
        }

        /**
         * Check if we are scheduled to abort. If so, print message if non null and
         * return true.
         * @return true on abort, false otherwise. If job is scheduled for abort or
         *         aborted. and it will report it to standard output
         */
        @Override
        public boolean checkAbort() {
            return false;
        }

        /**
         * Log a message at the TRACE level.
         *
         * @param msg the message string to be logged
         */
        @Override
        public void trace(String msg) {
        }

        /**
         * Log a message at the DEBUG level.
         *
         * @param msg the message string to be logged
         */
        @Override
        public void debug(String msg) {
            out.println(msg);
        }

        /**
         * Log a message at the INFO level.
         *
         * @param msg the message string to be logged
         */
        @Override
        public void info(String msg) {
            out.println(msg);
        }

        /**
         * Log a message at the WARN level.
         *
         * @param msg the message string to be logged
         */
        @Override
        public void warn(String msg) {
            out.println("WARN: " + msg);
        }

        /**
         * Log a message at the ERROR level.
         *
         * @param msg the message string to be logged
         */
        @Override
        public void error(String msg) {
            out.println("ERROR: " + msg);
        }

        /**
         * Method called when all errors are logged.  Initializes pointers for replay of errors.
         */
        @Override
        public void termLogging(ErrorLogger errorLogger) {
        }

        /**
         * Method to start the display of a progress dialog.
         * @param msg the message to show in the progress dialog.
         */
        @Override
        public void startProgressDialog(String msg) {
        }

        /**
         * Method to stop the progress bar
         */
        @Override
        public void stopProgressDialog() {
        }

        /**
         * Method to set a text message in the progress dialog.
         * @param message the new progress message.
         */
        @Override
        public void setProgressNote(String message) {
        }

        /**
         * Method to update the progress bar
         * @param pct the percentage done (from 0 to 100).
         */
        @Override
        public void setProgressValue(long done, long total) {
        }

        /**
         * Method to instantiate RouteResolution.
         * Can be called from any thread.
         * @param resolution RouteResolution
         */
        @Override
        public void instantiate(RouteResolution resolution) {
        }

        /**
         * flush changes
         * Can be called only from database thread
         * @param force unconditionally perform the final flush
         */
        @Override
        public void flush(boolean force) {
        }
    }
}
