/* Electric(tm) VLSI Design System
 *
 * File: PrecisionExpert.java
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

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.topology.NodeInst;

/**
 *
 * @author diivanov
 */
public class PrecisionExpert {

    private HashSet<String> usedPortList = new HashSet<>();
    private final Set<NodeInst> nodeList = new HashSet<>();
    private HashSet<String> usedNodeList = new HashSet<>();
    private ArrayList<Segment> segmentList = new ArrayList<>();
    
    private static PrecisionExpert instance;

    private PrecisionExpert() {
        
    }
    
    /**
     * Return Singleton instance.
     *
     * @return
     */
    private static synchronized PrecisionExpert getInstance() {
        if (instance == null) {
            instance = new PrecisionExpert();
        }
        return instance;
    }

    /**
     * method to start Expert System.
     */
    public static void getAdvice() {
        instance = getInstance();
        instance.renewLists();
        instance.createTable();
        instance.checkTable();
    }
    
    private void renewLists() {
        usedPortList = new HashSet<>();
        usedNodeList = new HashSet<>();
        segmentList = new ArrayList<>();
        assert (nodeList.isEmpty());
    }

    /**
     * method to check table manually by printing all internals.
     */
    private void checkTable() {
        Iterator<Segment> itr = segmentList.iterator();
        while (itr.hasNext()) {
            Segment segment = itr.next();
            segment.handle();
        }
    }

    /**
     * This method prepares the table which includes all elements from scheme
     * and their connections.
     */
    private void createTable() {
        NodeInst[] startNiArray = Accessory.getStartingNodeInst();
        for (NodeInst startNi : startNiArray) {
            createOneTable(startNi);
        }
    }

    /**
     * The main part of createTable method.
     */
    private void createOneTable(NodeInst startNi) {
        PortInst[] piArray = Accessory.getNearByPortInsts(startNi.getPortInst(0), usedPortList);
        addSegment(startNi.getPortInst(0), piArray);

        while (!nodeList.isEmpty()) {
            NodeInst ni = nodeList.iterator().next();
            Iterator<PortInst> itr2 = ni.getPortInsts();
            PortInst pi;
            while (itr2.hasNext()) {
                pi = itr2.next();
                if (!isPortUsed(pi)) {
                    piArray = Accessory.getNearByPortInsts(pi, usedPortList);
                    addSegment(pi, piArray);
                }
            }
            usedNodeList.add(ni.toString());
            nodeList.remove(ni);
        }
    }

    /**
     * Method initializes 1 more segment.
     */
    private void addSegment(PortInst pi1, PortInst[] piArray) {
        Segment segment = new Segment();
        segmentList.add(segment);

        setPortAsUsed(pi1);
        segment.add(pi1);

        for (PortInst pi : piArray) {
            setPortAsUsed(pi);
            segment.add(pi);
        }
        for (PortInst pi : piArray) {
            NodeInst nextNi = pi.getNodeInst();
            if (!usedNodeList.contains(nextNi.toString())) {
                nodeList.add(nextNi);
            }
        }
    }

    /**
     * Set port as used to avoid double-running.
     */
    private void setPortAsUsed(PortInst pi) {
        usedPortList.add(pi.toString());
    }

    /**
     * Check if port was used to avoid double-running.
     */
    private boolean isPortUsed(PortInst pi) {
        return usedPortList.contains(pi.toString());
    }

}
