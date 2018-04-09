/* Electric(tm) VLSI Design System
 *
 * File: Autotracing.java
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
import java.io.BufferedReader;
import java.io.FileReader;

import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.Tool;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

/**
 * This class is used as a controller for autotracing system All methods here is
 * just links for other classes' methods (MV model)
 */
public class Autotracing extends Tool {
    
    private static MakeTrace makeObject;

    /**
     * the Autotracing tool.
     */
    private static final Autotracing tool = new Autotracing();

    /**
     * The constructor sets up the Autotracing tool.
     */
    private Autotracing() {
        super("autotracing");
    }

    /**
     * Method to initialize the Autotracing tool.
     */
    @Override
    public void init() {
    }

    /**
     * Method to retrieve the singleton associated with the Autotracing tool,
     *
     * @return the Autotracing tool.
     */
    public static Autotracing getAutotracingTool() {
        return tool;
    }

    /**
     * Method to make path from one point to another using autotracing system.
     */
    public static void makePathOrClean() {
        File startingPointFile = new File(Accessory.POINTS_PATH);
        Accessory.cleanFile(Accessory.CONFIG_PATH);
        if (Accessory.getStringCount(startingPointFile) != 2) {
            Accessory.cleanFile(Accessory.POINTS_PATH);
            try {
                ExportKeysWithIndication();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                assert false;
            }
            System.out.println("Cleaning file and width changing.");
        } else {
            NonOrientedGlobalGraph nogg = new NonOrientedGlobalGraph("EleventhApril");
            try {
                nogg.setStartingAndEndingPoint(importFile(startingPointFile)[0], importFile(startingPointFile)[1]);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                assert false;
            }
            nogg.deikstra(false);
            try {
                ImportKeys.controller(null);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                assert false;
            }
            Accessory.cleanFile(Accessory.POINTS_PATH);
        }
    }

    /**
     * Exporting keys from scheme to file, link to ExportKeysFromScheme method.
     * @throws java.io.IOException
     */
    public static void ExportKeys() throws IOException {
        ExportKeys.ExportKeysFromScheme();
    }

    /**
     * Exporting keys from scheme to file and indicate all using nets on scheme,
     * link to ExportKeysFromSchemeWithIndication method.
     * @throws java.io.IOException
     */
    public static void ExportKeysWithIndication() throws IOException {
        ExportKeys.ExportKeysFromSchemeWithIndication();
    }

    /**
     * Link to MakeTrace method
     */
    public static void makeTrace() {
        makeObject = new MakeTrace();
    }

    /**
     * Link to getAdvice method
     */
    public static void getAdvice() {
        PrecisionExpert.getAdvice();
    }

    /**
     * Method uses external file to set first and last points... ***Develop***,
     * SHOULD BE UNIT TEST HERE
     */
    private static String[] importFile(File graphList) throws IOException {
        String[] line;
        try (BufferedReader graphListBufReader = new BufferedReader(new FileReader(graphList))) {
            line = new String[2];
            line[0] = graphListBufReader.readLine();
            line[1] = graphListBufReader.readLine();
        }
        return line;
    }
    
    public void createAndShowGUI(boolean start) {
        makeObject.createAndShowGUI(start);
    }

    /**
     *
     */
    private static class MakeTrace extends Job {

        private JFrame frame = null;

        protected MakeTrace() {
            super("Make Trace", Autotracing.getAutotracingTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            createAndShowGUI(true);
            SimpleAutotracing.getSimpleAutotracing().startTrace();
            createAndShowGUI(false);
            return true;
        }

        @Override
        public void terminateOK() {

        }

        /**
         * Method the progress bar appear and disappear,
         *
         * @Param start = true to show and false to drop progress bar.
         */
        public void createAndShowGUI(boolean start) {
            if (start) {
                frame = new JFrame("Progress");
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        e.getWindow().dispose();
                        SimpleAutotracing.getSimpleAutotracing().setExitPressed();
                        Accessory.showMessage("Autotracing will be stopped after 1 step.");
                    }
                });

                JLabel label = new JLabel("Please wait...");
                frame.getContentPane().add(label);

                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);

                frame.getContentPane().add(progressBar);

                frame.pack();
                frame.setVisible(true);
                frame.setResizable(false);
            } else {
                frame.dispose();
            }

        }
    }

}
