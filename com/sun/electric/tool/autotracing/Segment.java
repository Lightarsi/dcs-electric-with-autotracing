/* Electric(tm) VLSI Design System
 *
 * File: Segment.java
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

import java.util.ArrayList;
import java.util.Iterator;

import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.PortInst;

import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.topology.Connection;
import com.sun.electric.tool.user.Highlighter;
import com.sun.electric.tool.user.ui.EditWindow;

import java.awt.Color;

/**
 *
 * @author diivanov
 */
public class Segment {

    private final ArrayList<PortInst> elementsList = new ArrayList<>();

    /**
     *
     */
    public Segment() {
    }

    /**
     * Set port as used to avoid double-running.
     *
     * @param pi
     */
    public void add(PortInst pi) {
        elementsList.add(pi);
    }

    /**
     * Set port as used to avoid double-running.
     */
    public void printAll() {
        for (PortInst pi : elementsList) {
            System.out.println(pi.toString());
        }
        System.out.println("_");
    }

    /**
     * method to print and show message
     *
     * @param s
     */
    public void giveAdvice(String s) {
        System.out.println(s);
        Accessory.showMessage(s);
    }

    /**
     * Method to handle segment and to form advice
     */
    public void handle() {
        int res = 0;
        int pres = 0;
        int cap = 0;
        int out = 0;
        int outm = 0;
        int outp = 0;
        int out_pau = 0;
        int inm = 0;
        int inp = 0;
        int output = 0;
        String port;

        for (PortInst pi : elementsList) {
            String name = Accessory.parsePortToBlockOld(pi.toString());
            switch (name) {
                case "RES":
                    res++;
                    break;
                case "P_RES":
                    pres++;
                    break;
                case "CAP":
                    cap++;
                    break;
                case "CAU":
                    port = Accessory.parsePortToPortOld(pi.toString());
                    switch (port) {
                        case "INP":
                            inp++;
                            break;
                        case "INM":
                            inm++;
                            break;
                        case "OUT":
                            out_pau++;
                            out++;
                            break;
                    }
                    break;
                case "PAU":
                    port = Accessory.parsePortToPortOld(pi.toString());
                    switch (port) {
                        case "INP":
                            inp++;
                            break;
                        case "INM":
                            inm++;
                            break;
                        case "OUT":
                            out++;
                            break;
                        case "OUTP":
                            outp++;
                            break;
                        case "OUTM":
                            outm++;
                            break;

                    }
                    break;
                case "ADR":
                    output++;
                    break;
                case "DDR":
                    output++;
                    break;
            }
        }
        if (pres == 0) {
            if ((res >= 2) && (out == 1)) {
                for (PortInst pi : elementsList) {
                    if ((Accessory.parsePortToPortOld(pi.toString())).equals("OUT")) {
                        giveAdvice("Use 4-points in " + pi.toString());
                        highlightAdvice(pi);
                        break;
                    }
                }

            }
            if ((res >= 1) && (out == 1) && (output == 1)) {
                for (PortInst pi : elementsList) {
                    if ((Accessory.parsePortToPortOld(pi.toString())).equals("OUT")) {
                        giveAdvice("Use 4-points in " + pi.toString());
                        highlightAdvice(pi);
                        break;
                    }
                }
            }
            if ((out == 1) && (inp == 1) && (res == 1)) {
                for (PortInst pi : elementsList) {
                    if ((Accessory.parsePortToPortOld(pi.toString())).equals("OUT")) {
                        giveAdvice("Use 4-points in " + pi.toString());
                        highlightAdvice(pi);
                        break;
                    }
                }
            }
            if ((out == 1) && (inm == 1) && (res == 1)) {
                for (PortInst pi : elementsList) {
                    if ((Accessory.parsePortToPortOld(pi.toString())).equals("OUT")) {
                        giveAdvice("Use 4-points in " + pi.toString());
                        highlightAdvice(pi);
                        break;
                    }
                }
            }

        }
        if (cap == 0) {
            if (out_pau == 1) {
                for (PortInst pi : elementsList) {
                    if ((Accessory.parsePortToPortOld(pi.toString())).equals("OUT")) {
                        if ((Accessory.parsePortToBlock(pi.toString())).equals("PAU")) {
                            giveAdvice("Use shunt " + pi.toString());
                            highlightAdvice(pi);
                            break;
                        }
                    }
                }
            }
        }
        if (inp == 0) {
            if ((outp == 1) || (outm == 1)) {
                giveAdvice("bufferisation is needed.");
            }
            if ((res >= 2) && (out == 1)) {
                for (PortInst pi : elementsList) {
                    if ((Accessory.parsePortToPortOld(pi.toString())).equals("OUT")) {
                        giveAdvice("Use 4-points in " + pi.toString());
                        highlightAdvice(pi);
                        break;
                    }
                }
            }
        }
    }

    /**
     * method to print and show message
     *
     * @param pi
     */
    public void highlightAdvice(PortInst pi) {
        if (!pi.hasConnections()) {
            return;
        }
        EditWindow wnd = EditWindow.needCurrent();
        if (wnd == null) {
            return;
        }
        Cell cell = wnd.getCell();
        Highlighter highlighter = wnd.getHighlighter();

        Iterator<Connection> itr = pi.getConnections();
        while (itr.hasNext()) {
            Connection con = itr.next();
            ArcInst ai = con.getArc();
            highlighter.addElectricObject(ai, cell, Color.YELLOW);
        }
        EditWindow.repaintAllContents();
    }

}
