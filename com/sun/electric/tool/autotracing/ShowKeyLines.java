/* Electric(tm) VLSI Design System
 *
 * File: ShowKeyLines.java
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
import com.sun.electric.database.geometry.Poly;
import com.sun.electric.database.geometry.ScreenPoint;
import com.sun.electric.database.prototype.PortProto;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.technology.ArcProto;
import com.sun.electric.technology.technologies.Generic;
import com.sun.electric.technology.technologies.Schematics;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import static com.sun.electric.tool.user.Highlight.drawLine;
import com.sun.electric.tool.user.User;
import com.sun.electric.tool.user.UserInterfaceMain;
import com.sun.electric.tool.user.ui.EditWindow;
import com.sun.electric.tool.user.ui.WindowFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.GlyphVector;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * This class is used in highlight.java (to draw key's line and number)
 *
 * @author diivanov
 */
public class ShowKeyLines {

    /**
     * Scheme Singleton for 5400TP035 links.
     */
    private static final Scheme scheme = Scheme.getInstance();
    private static boolean isFocused = false;
    private static Pair<PortInst, PortInst> pair = null;

    private ShowKeyLines() {
        throw new AssertionError();
    }

    /**
     * Method to draw line if it is a key.
     *
     * @param pp
     * @param originalPi
     * @param ni
     * @param g
     * @param wnd
     * @param poly
     * @param offX
     * @param offY
     * @return
     */
    public static GlyphVector showKeyLines(PortProto pp, PortInst originalPi, NodeInst ni, Graphics g, EditWindow wnd, Poly poly, long offX, long offY, boolean wired) {
        // keys indication for 5400TP035 project
        if (originalPi == null) {
            return null;
        }
        GlyphVector v2 = null;
        if (Constants.isKeysIndicated()) {
            Pattern p = Pattern.compile("[nopqrstuvw]\\d+\\d*");
            String ppName = pp.getName();
            PortInst originalPi2 = null;
            if (p.matcher(ppName).matches()) {
                String num = ppName.substring(1, ppName.length());
                int numInt = Integer.valueOf(num);
                int nextNum;
                if (numInt % 2 == 0) {
                    nextNum = numInt - 1;
                } else {
                    nextNum = numInt + 1;
                }
                String nextPPName = ppName.substring(0, 1) + String.valueOf(nextNum);
                Iterator<PortInst> itrPi = ni.getPortInsts();
                while (itrPi.hasNext()) {
                    PortInst next = itrPi.next();
                    if (next.getPortProto().getName().equals(nextPPName)) {
                        originalPi2 = next;
                    }
                }
                if (originalPi2 == null) {
                    return null;
                }
                int keyNum = Accessory.parsePortToKey(Accessory.parsePortToPortOld(originalPi.toString()), Accessory.parsePortToPortOld(originalPi2.toString()));
                int letterInfluence = Accessory.explainSPMLetter(ppName.substring(0, 1));
                if (letterInfluence != -1) {
                    keyNum += letterInfluence;
                }
                String key = String.valueOf(keyNum);
                Font font = new Font(User.getDefaultFont(), Font.PLAIN, (int) (1.5 * EditWindow.getDefaultFontSize()));
                v2 = wnd.getGlyphs(key, font);

                ScreenPoint scr1 = wnd.databaseToScreen(originalPi.getCenter());
                ScreenPoint scr2 = wnd.databaseToScreen(originalPi2.getCenter());

                Graphics g2 = g;
                g2.setColor(Color.YELLOW);
                drawLine(g, wnd, scr1.getX() + offX, scr1.getY() + offY, scr2.getX() + offX, scr2.getY() + offY);
                if (!wired) {
                    isFocused = true;
                    pair = new Pair<>(originalPi, originalPi2);
                }
            } else {
                isFocused = false;
                pair = null;
            }
        }
        return v2;
    }

    /**
     * Method to draw number of key if it is a key.
     *
     * @param originalPi
     * @param scrP
     * @param wnd
     * @param g
     * @param font
     * @param offX
     * @param offY
     */
    public static void drawKeyName(PortInst originalPi, ScreenPoint scrP, EditWindow wnd, Graphics g, Font font, long offX, long offY) {
        if ((originalPi != null) && (scheme.getNetNameFrom(originalPi.toString()) != null)) {
            GlyphVector v3 = wnd.getGlyphs(scheme.getNetNameFrom(originalPi.toString()), font);
            ((Graphics2D) g).drawGlyphVector(v3, (float) scrP.getX() + offX, (float) scrP.getY() + offY + 15f);
        }
    }

    public static class MouseListenerForKeys {

        public MouseListenerForKeys() {
            MouseListener mListen = WindowFrame.getMouseListener();
        }

        public static void mousePressed(MouseEvent evt) {
            if ((evt.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {
                if (isFocused && pair != null && Constants.isKeysIndicated() != false) {
                    ArcProto arc = Generic.tech().universal_arc;
                    new CreateNewArc(arc, pair.getFirstObject(), pair.getSecondObject(), 0);
                }
            }
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
}
