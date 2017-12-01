/* Electric(tm) VLSI Design System
 *
 * File: Chain.java
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
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author diivanov
 */
public class Chain extends Vertex {

    private final List<String> vertsList = new ArrayList<>();
    private final String vertsFromGlobalGraph;
    private int weight = 1;
    private boolean affected = false;
    private boolean isIonChain = false;

    /**
     * Constructor: parse input String to port elements, adding parse return to
     * ArrayList.
     *
     * @param vertsFromGlobalGraph
     * @param label
     */
    public Chain(String vertsFromGlobalGraph, String label) {
        super(label);
        this.vertsFromGlobalGraph = vertsFromGlobalGraph;
        String[] connectedVertices = vertsFromGlobalGraph.split(" ");
        for (String connectedVertice : connectedVertices) {
            this.vertsList.add(connectedVertice);
            if (connectedVertice.contains("SPM")) {
                //weight+=15;
            }
            if (connectedVertice.contains("ION")) {
                this.isIonChain = true;
            }
        }
        if (connectedVertices.length > 8) {
            weight += 4;
        }
    }

    /**
     * Constructor: copy Contructor.
     *
     * @param chain
     */
    public Chain(Chain chain) {
        super(chain.getLabel());
        this.vertsFromGlobalGraph = chain.getLine();
        String[] connectedVertices = vertsFromGlobalGraph.split(" ");
        for (String connectedVertice : connectedVertices) {
            this.vertsList.add(connectedVertice);
            if (connectedVertice.contains("ION")) {
                this.isIonChain = true;
            }
        }
        this.weight = chain.getWeight();
    }

    /**
     * Method implements searching mechanism inside chain.
     *
     * @return
     */
    public String[] searchForCB() {
        List<String> cbChains = new ArrayList<>();
        for (String vert : vertsList) {
            if (Accessory.parsePortToBlock(vert).contains("CB")) {
                cbChains.add(Accessory.parsePortToBlock(vert));
                cbChains.add(Accessory.parsePortToPort(vert));
            }
        }
        assert (cbChains.size() > 0);
        String[] a = new String[cbChains.size()];
        a = cbChains.toArray(a);
        return a;
    }

    /**
     * Method implements searching mechanism inside chain.
     *
     * @param blockPiece
     * @return
     */
    public String searchForBlock(String blockPiece) {
        for (String vert : vertsList) {
            if (Accessory.parsePortToBlock(vert).contains(blockPiece)) {
                return vert;
            }
        }
        return null;
    }

    /**
     * Method implements searching mechanism inside chain, used in external
     * autotracing scheme.
     *
     * @param blockPiece
     * @return
     */
    public String searchForPattern(String blockPiece) {
        Pattern p = Pattern.compile(blockPiece);

        for (String vert : vertsList) {
            if (p.matcher(vert).find()) {
                return vert;
            }
        }
        return null;
    }

    /**
     * check if String @Vert contains String @port.
     */
    private boolean contains(String Vert, String port) {
        return Accessory.parsePortToBlock(Vert).equals(port);
    }

    /**
     * Check if vert from vertsList contains string.
     *
     * @param port
     * @return
     */
    public boolean checkForContains(String port) {
        for (String vert : vertsList) {
            if (contains(vert, port)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if vert from vertsList contains ION.
     * @return
     */
    public boolean checkForContainsION() {
        return isIonChain;
    }

    /**
     * Method to get String line of chain.
     *
     * @return
     */
    public String getLine() {
        return vertsFromGlobalGraph;
    }

    /**
     * Method to delete verteces connected to this chain.
     *
     * @return
     */
    public String[] getConnectedVerteces() {
        String[] a = new String[0];
        a = vertsList.toArray(a);
        return a;
    }

    /**
     * Method to get String line of chain.
     *
     * @return
     */
    public boolean isDeleted() {
        return isDeleted == true;
    }

    /**
     * Method to get String line of chain.
     */
    public void setDeleted() {
        isDeleted = true;
    }

    /**
     * Set weight for cost function.
     *
     * @param newWeight
     */
    public void setWeight(int newWeight) {
        weight = newWeight;
    }

    /**
     * Get weight to count cost function.
     *
     * @return
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Add weight to count cost function.
     */
    public void addWeight() {
        Accessory.printLog("WIncrease. " + weight);
        weight += 4;
    }

    public boolean isAffected() {
        return affected;
    }

    public void setAffected() {
        Iterator<String> vertItr = vertsList.iterator();
        while(vertItr.hasNext()) {
            String vert = vertItr.next();
            if ((!vert.contains("CB")) && (!vert.contains("SPM"))) {
                vertItr.remove();
            }
        }
        this.affected = true;
    }

}
