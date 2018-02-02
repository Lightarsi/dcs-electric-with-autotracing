/* Electric(tm) VLSI Design System
 *
 * File: NonOrientedGlobalGraph.java
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class is used to describe global graph, this global graph is the complex
 * of all elements in scheme, global graph is using local CB graphs as parts.
 */
public final class NonOrientedGlobalGraph extends NonOrientedGraph {

    private Chain[] vertexArray; 															// Array of Vertices
    private Set<String> UsedBlockList = new HashSet<>();							// Used to avoid double-using blocks in autotracing
    private List<NonOrientedCBGraph> noCBgList = new ArrayList<>();		// List of all local(CB) graphs linked to this global graph
    private List<Integer> VertToDeleteList = new ArrayList<>();						//
    private List<Integer> VertToAffectList = new ArrayList<>();	                                        // For SPM double-used ports
    private List<Integer> VertToIncreaseList = new ArrayList<>();					//
    private final int VERTEX_MAX = 950;  													// 867 is the real number of verteces.
    private int startingPoint, endingPoint;													// set points those describe (vertexArray[int]) chain

    /**
     * Constructor: base constructor to create new global graph object, forming
     * internal structure from external file.
     *
     * @param graphName
     */
    public NonOrientedGlobalGraph(String graphName) {
        super(graphName);
        Init(VERTEX_MAX);
        File fileForImport = new File(Accessory.GLOBAL_PATH);
        try {
            integrateChains(fileForImport);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Constructor: copy constructor (copy chain objects only, others are
     * typical).
     *
     * @param noggToCopy
     */
    public NonOrientedGlobalGraph(NonOrientedGlobalGraph noggToCopy) {
        super(noggToCopy.getLabel());
        Init(VERTEX_MAX);
        for (Chain chain : noggToCopy.getVertexArray()) {
            if (chain == null) {
                continue;
            }
            this.vertexArray[vertexCount++] = new Chain(chain);
        }
    }

    /**
     * Method to get name
     *
     * @param point
     * @param name
     * @return
     */
    public String getNameFromPoint(int point, String name) {
        if (vertexArray[point] == null) {
            return null;
        }
        return vertexArray[point].searchForPattern(name);
    }

    /**
     * Method to find point that starts the autotracing iteration,
     *
     * @param name
     * @return
     * @Param name is for accurate PADDR in scheme.
     */
    public int findStartingPoint(String name) {
        for (int i = 0; i < vertexArray.length; i++) {
            if (vertexArray[i] != null) {
                if (vertexArray[i].searchForPattern(name) != null) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Method to find point that starts the autotracing iteration,
     *
     * @param name
     * @return
     * @Param name is for accurate PADDR in scheme.
     */
    public ArrayList<Integer> findStartingPointAsList(String name) {
        ArrayList<Integer> nameList = new ArrayList<>();
        for (int i = 0; i < vertexArray.length; i++) {
            if (vertexArray[i] != null) {
                if (vertexArray[i].searchForPattern(name) != null) {
                    nameList.add(i);
                }
            }
        }
        return nameList;
    }

    /**
     * Method to find point that starts the autotracing iteration,
     *
     * @param name
     * @return
     * @Param name is for accurate PADDR in scheme.
     */
    public String findStartingPointName(String name) {
        for (Chain vert : vertexArray) {
            if (vert != null) {
                if (vert.searchForPattern(name) != null) {
                    return vert.searchForPattern(name);
                }
            }
        }
        return null;
    }

    /**
     * Method to find point that starts the autotracing iteration,
     *
     * @param name
     * @return
     * @Param name is for accurate PADDR in scheme.
     */
    public ArrayList<String> findStartingPointNamesAsList(String name) {
        ArrayList<String> nameList = new ArrayList<>();
        for (Chain vert : vertexArray) {
            if (vert != null) {
                String vertName = vert.searchForPattern(name);
                if (vertName != null) {
                    nameList.add(vertName);
                }
            }
        }
        return nameList;
    }

    /**
     * Method to set block as used to avoid double-using it in autotracing.
     *
     * @param blockName
     */
    public void setBlockAsUsed(String blockName) {
        UsedBlockList.add(blockName);
        Accessory.printLog("Block " + blockName + " set as used.");
    }

    /**
     * Method to set block as used to avoid double-using it in autotracing.
     *
     * @param blockName
     * @param port
     */
    public void setPPCPartAsUsed(String blockName, String port) {
        String index = port.substring(0, 2);
        String firstOne = blockName + ".*" + index;
        UsedBlockList.add(firstOne);
        Accessory.printLog("1st PPC part " + firstOne + " set as used.");
        switch (index) {
            case "PO":
                index = "PP";
                break;
            case "PP":
                index = "PO";
                break;
            case "PQ":
                index = "PR";
                break;
            case "PR":
                index = "PQ";
                break;
            case "PX":
                index = "PY";
                break;
            case "PY":
                index = "PX";
                break;
            case "PV":
                index = "PZ";
                break;
            case "PZ":
                index = "PV";
                break;
        }
        String secondOne = blockName + ".*" + index;
        UsedBlockList.add(secondOne);
        Accessory.printLog("2nd PPC part " + secondOne + " set as used.");
    }

    /**
     * Starting deikstra method, this method is used in "point to point" only,
     * starting and ending points must be set before using this method.
     *
     * @param doDelete
     */
    public void deikstra(boolean doDelete) {
        assert startingPoint != endingPoint;
        deikstra(startingPoint, endingPoint, doDelete);
    }

    /**
     * This method implements the deikstra algorithm to find the optimal way
     * through graph, this method can delete vertices to dynamic modification of
     * graph,
     *
     * @param startPoint IS STRING.
     * @param niName
     * @param param
     * @param doDelete is true to delete all vertices on the way,
     * @param doWrite
     * @return
     */
    public Pair<String, Integer> deikstra(String startPoint, String niName, String param, boolean doDelete, boolean doWrite) {
        int point = findStartingPoint(startPoint);
        return deikstra(point, niName, param, doDelete, doWrite);
    }

    public Pair<String, Integer> deikstra(int startPoint, String niName, String param, boolean doDelete, boolean doWrite) {
        return deikstra(startPoint, niName, param, doDelete, doWrite, false);
    }

    /**
     * This method implements the deikstra algorithm to find the optimal way
     * through graph, this method can delete vertices to dynamic modification of
     * graph,
     *
     * @param startPoint
     * @param param
     * @param niName
     * @param doDelete
     * @param doWrite
     * @param SPMAffected
     * @return
     * @Param doDelete is true to delete all vertices on the way,
     * @Param startPoint shows the edges labels of needed way.
     */
    public Pair<String, Integer> deikstra(int startPoint, String niName, String param, boolean doDelete, boolean doWrite, boolean SPMAffected) {
        BinaryHeapNew heap = new BinaryHeapNew();
        boolean ion = niName.contains("ION");
        int curPathCount;
        Integer closestVertex;
        int currentVertex = startPoint;
        int endPoint = -1;
        boolean endIsFound = false;
        int count;
        int lastCount = -1;
        String result;
        String lastResult = null;

        vertexArray[currentVertex].setVisited(true);
        vertexArray[currentVertex].setPathCount(0);
        heap.add(vertexArray[currentVertex].getPathCount(), currentVertex);

        // we should check first Chain first
        result = vertexArray[currentVertex].searchForPattern(niName);   // method optimized
        if (!checkForUsed(result, niName)) {                            // if result == 0, checkForUsed returns false
            if ((result != null)) {
                count = vertexArray[currentVertex].getPathCount();
                if (lastCount == -1) {
                    lastCount = count;
                    endPoint = currentVertex;
                    endIsFound = true;
                    lastResult = result;
                } 
            }
        }

        while (((closestVertex = heap.getMinKey()) != -1)) {
            vertexArray[closestVertex].setVisited(true);
            Integer[] a = getCloseVerteces(closestVertex);
            if (a == null) {
                continue;
            }
            for (Integer a1 : a) {
                if (!ion) {
                    if (vertexArray[a1].checkForContainsION()) { // method to optimize checkForContains("ION")
                        continue;
                    }
                }
                if (vertexArray[a1].isAffected()) {
                    String res = vertexArray[a1].searchForPattern(niName); // method optimized
                    if (res == null) {
                        continue;
                    }
                }
                currentVertex = a1;
                int prewe = getWeight(currentVertex, closestVertex);
                int weight = prewe + vertexArray[currentVertex].getWeight();
                if (prewe == -1) {
                    continue;
                }
                if ((vertexArray[currentVertex].getPathCount()) > (curPathCount = (vertexArray[closestVertex].getPathCount()
                        + weight))) {

                    vertexArray[currentVertex].setPathCount(curPathCount);
                    // maybe should be for each chain, not only with lower path
                    result = vertexArray[currentVertex].searchForPattern(niName);   // method optimized
                    if (!checkForUsed(result, niName)) {                            // if result == 0, checkForUsed returns false
                        if ((result != null)) {
                            count = vertexArray[currentVertex].getPathCount();
                            if (lastCount == -1) {
                                lastCount = count;
                                endPoint = currentVertex;
                                endIsFound = true;
                                lastResult = result;
                            } else {
                                if (count < lastCount) {
                                    lastCount = count;
                                    endPoint = currentVertex;
                                    lastResult = result;
                                }
                            }
                        }
                    }
                }
                heap.add(vertexArray[currentVertex].getPathCount(), currentVertex);
            }
        }

        for (int j = 0; j < vertexCount; j++) {
            if (vertexArray[j] != null) {
                vertexArray[j].setVisited(false);
            }
        }

        if (!endIsFound) {
            Pair<String, Integer> pair = new Pair<>(null, -1);
            resetVertices();
            return pair;
        }
        deikstra_backway_with_delete(endPoint, startPoint, doDelete, doWrite, SPMAffected);
        resetVertices();
        Pair<String, Integer> pair = new Pair<>(lastResult, lastCount);
        return pair;
    }

    /**
     * This method is used to delete all marked chains if needed, chains are
     * being marked while being used in deikstra backway method, marks reset
     * with reset() method.
     */
    public void doDelete() {
        if (!VertToDeleteList.isEmpty()) {
            Iterator<Integer> deleteItr = VertToDeleteList.iterator();
            while (deleteItr.hasNext()) {
                deleteVertex(deleteItr.next());
                deleteItr.remove();
            }
        }
        if (!VertToAffectList.isEmpty()) {
            Iterator<Integer> deleteItr = VertToAffectList.iterator();
            while (deleteItr.hasNext()) {
                affectVertex(deleteItr.next());
                deleteItr.remove();
            }
        }
        for (NonOrientedCBGraph nocbg : noCBgList) {
            nocbg.doDeleteUsedVerts();
        }
    }

    /**
     * Soft reset of nogg, only lists those were filled during non-delete
     * autotracing step.
     */
    public void resetLists() {
        VertToDeleteList = new ArrayList<>();
        VertToAffectList = new ArrayList<>();
    }

    /**
     * Full reset of NonOrientedGlobalGraph, method resets used block and marks
     * lists, verteces' pathcounts and NonOrientedCBGraphs.
     */
    /*public void reset() {
        UsedBlockList = new HashSet<>();
        VertToDeleteList = new ArrayList<>();
        VertToAffectList = new ArrayList<>();
        resetVertices();
        for (NonOrientedCBGraph nocbg : noCBgList) {
            nocbg.reset();
        }
    }*/
    /**
     * Change weight variables after non-delete autotracing.
     */
    public void applyWeightChanges() {
        if (!VertToIncreaseList.isEmpty()) {
            for (Integer vert : VertToIncreaseList) {
                vertexArray[vert].addWeight();
            }
            VertToIncreaseList = new ArrayList<>();
        }
    }

    /**
     * Method has a role of the getCloseVertex function to use it in global
     * tracing, method Overrides getCloseVertex(int).
     *
     * @param v
     * @return
     */
    @Override
    protected Integer[] getCloseVerteces(int v) {
        String[] CBLabel = getCloseCBs(v);
        Integer[] a = getCloseChains(CBLabel);
        return a;
    }

    /**
     * Method has a role of the getCloseVertex function to use it in global
     * tracing, method returns two CBs those are connected to chain.
     *
     * @param v
     * @return
     */
    protected String[] getCloseCBs(int v) {
        return vertexArray[v].searchForCB();
    }

    /**
     * Method has a role of the getCloseVertex function to use it in global
     * tracing, method realises second step of autotracing, method returns the
     * array of closest verteces using connected CB blocks.
     *
     * @param CBLabel
     * @return
     */
    protected Integer[] getCloseChains(String[] CBLabel) {
        Set<Integer> cbChains = new HashSet<>();
        for (int v = 0; v < vertexCount; v++) {
            if (vertexArray[v] == null) {
                continue;
            }
            if (vertexArray[v].getVisited() == true) {
                continue;
            }
            for (String cbl : CBLabel) {
                if (vertexArray[v].checkForContains(cbl)) {
                    cbChains.add(v);
                }
            }
        }
        if (cbChains.size() > 0) {
            Integer[] a = new Integer[0];
            a = cbChains.toArray(a);
            return a;
        }
        return null;
    }

    /**
     * Method to form new vertex in graph as Chain.
     *
     * @param line
     * @param label
     * @return
     */
    @Override
    protected boolean addVertex(String line, String label) {
        vertexArray[vertexCount++] = new Chain(line, label);
        return true;
    }

    /**
     * Method to delete vertex from graph,
     *
     * @param count
     * @Param count is the number of vertex that should be deleted,
     */
    @Override
    protected void deleteVertex(int count) {
        assert count >= 0;
        if (vertexArray[count] != null) {
            Accessory.printLog("Deleted Vertex " + vertexArray[count].getLine());
            String[] conVerts = vertexArray[count].getConnectedVerteces();
            vertexArray[count] = null;
            for (String vert : conVerts) {
                String spl = Accessory.parsePortToBlock(vert);
                String port = Accessory.parsePortToPort(vert);
                if (spl.split("<")[0].equals("CB")) {                                      // Only CB's verteces should be deleted
                    NonOrientedCBGraph noCBg = getOrCreateLocalGraph(spl);
                    Accessory.printLog(spl + " toDelete");
                    noCBg.deleteKeyFromCBGraph(port, true);
                    /*if(vertexArray[count].isXYGlobal()) {
                        noCBg.deleteKeyFromCBGraph(spl, Accessory.parsePortToPort(vert));
                    }*/
                }

            }
        }
    }

    /**
     * Method to affect vertex in graph, SPM affected vertex was used in SPM but
     * wasn't used by other blocks
     *
     * @param count
     * @Param count is the number of vertex that should be deleted,
     */
    protected void affectVertex(int count) {
        assert count >= 0;
        if (vertexArray[count] != null) {
            Accessory.printLog("Affected Vertex " + vertexArray[count].getLine());
            vertexArray[count].setAffected();
        }
    }

    /**
     * Method initialises array vertexArray with Chain objects there instead of
     * Vertex.
     *
     * @param VERTEX_MAX
     */
    @Override
    protected void Init(int VERTEX_MAX) {
        vertexArray = new Chain[VERTEX_MAX];
    }

    /**
     * Method to check if this block was used before in autotracing.
     */
    private boolean checkForUsed(String block, String isOld) { // (result, niName);
        if (block == null) {
            return false;
        }
        for (String cursor : UsedBlockList) {
            if (cursor.contains("PPC")) {
                Pattern ppc = Pattern.compile(cursor);
                if (ppc.matcher(block).find()) {
                    if (!isOld.contains("<")) {
                        return true;
                    }
                }
            } else {
                if (block.contains(cursor)) {
                    if (!isOld.contains("<")) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * Method to support copy constructor (returning the array of chains from
     * copied class).
     */
    private Chain[] getVertexArray() {
        return vertexArray;
    }

    /**
     * Method initialises CB graphs to use it's internal structure, method is
     * using external file with list of CBs.
     */
    private void initiateCBs() throws IOException {
        String line;
        try (BufferedReader allCBsReader = new BufferedReader(new FileReader(new File(Accessory.ALL_BLOCKS)))) {
            while ((line = allCBsReader.readLine()) != null) {
                if (line.contains("CB")) {
                    getOrCreateLocalGraph(line);
                    prepareChains(line);
                } else if (line.contains("PPC")) {
                    prepareChains(line);
                } else if (line.contains("SPM")) {
                    prepareChains(line);
                } else if (line.contains("CAU")) {
                    prepareChains(line);
                } else if (line.contains("PAU")) {
                    prepareChains(line);
                }
            }
        }
    }

    /**
     * Method to reset all pathcounts of chains in graph.
     */
    @Override
    protected void resetVertices() {
        for (int i = 0; i < vertexCount; i++) {
            if (vertexArray[i] != null) {
                vertexArray[i].resetPathCount();
            }
        }
    }

    /**
     * Method uses external Strings to set starting point and ending point.
     *
     * @param startLine
     * @param endLine
     */
    protected void setStartingAndEndingPoint(String startLine, String endLine) {
        startingPoint = -1;
        endingPoint = -1;
        String[] startLabel = startLine.split(" -- ");
        String[] endLabel = endLine.split(" -- ");
        assert startLabel.length > 1;
        assert endLabel.length > 1;
        for (int i = 0; i < vertexCount; i++) {
            for (String label : startLabel) {
                if (vertexArray[i].getLabel().equals(Accessory.parsePortAndCut(label))) {
                    startingPoint = i;
                }
            }
            for (String label : endLabel) {
                if (vertexArray[i].getLabel().equals(Accessory.parsePortAndCut(label))) {
                    endingPoint = i;
                }
            }
        }
        if (startingPoint == -1) {
            for (int i = 0; i < vertexCount; i++) {
                String vertString = vertexArray[i].getLine();
                for (String str : vertString.split(" ")) {
                    if (str.equals(startLabel[0])) {
                        startingPoint = i;
                    }
                }
            }
        }
        if (endingPoint == -1) {
            for (int i = 0; i < vertexCount; i++) {
                String vertString = vertexArray[i].getLine();
                for (String str : vertString.split(" ")) {
                    if (str.equals(endLabel[0])) {
                        endingPoint = i;
                    }
                }
            }
        }
        try {
            initiateCBs();
        } catch (IOException ioe) {
            Accessory.showMessage("CBs won't be initiated.");
            assert false;
        }
    }

    /**
     * This method implements the deikstra algorithm to find the optimal way
     * through graph, this method can delete vertices to dynamic modification of
     * graph,
     *
     * @Param doDelete is true to delete all vertices on the way,
     * @Params startPoint and endPoint show the edges labels of needed way.
     */
    private boolean deikstra(int startPoint, int endPoint, boolean doDelete) {
        BinaryHeapNew heap = new BinaryHeapNew();
        int curPathCount;
        Integer closestVertex;
        int currentVertex = startPoint;
        boolean endIsFound = false;
        int counter = 0;

        vertexArray[currentVertex].setVisited(true);
        vertexArray[currentVertex].setPathCount(0);

        heap.add(vertexArray[currentVertex].getPathCount(), currentVertex);

        while (((closestVertex = heap.getMinKey()) != -1)) {
            if (closestVertex == endPoint) {
                break;									// not sure if this is still optimal solution
            }
            counter++;
            assert counter < 1000;
            vertexArray[closestVertex].setVisited(true);
            Integer[] a = getCloseVerteces(closestVertex);
            if (a == null) {
                continue;
            }
            for (Integer a1 : a) {
                assert a.length < 30000;
                currentVertex = a1;
                int prewe = getWeight(currentVertex, closestVertex);
                int weight = prewe + vertexArray[currentVertex].getWeight();
                if (prewe == -1) {
                    continue;
                }
                if ((vertexArray[currentVertex].getPathCount()) > (curPathCount = (vertexArray[closestVertex].getPathCount()
                        + weight))) {
                    vertexArray[currentVertex].setPathCount(curPathCount);

                    if (currentVertex == endPoint) {
                        endIsFound = true;
                    }
                }
                heap.add(vertexArray[currentVertex].getPathCount(), currentVertex);
            }

        }
        for (int j = 0; j < vertexCount; j++) {
            if (vertexArray[j] != null) {
                vertexArray[j].setVisited(false);
            }
        }
        if (!endIsFound) {
            System.out.println("There is no way here.");
            return false;
        }
        deikstra_backway_with_delete(endPoint, startPoint, doDelete, true, false);
        resetVertices();
        return true;
    }

    /**
     * This method use deikstra results to find all verteces used by this way,
     * this method may delete vertices to dynamic modification of graph,
     *
     * @Param doDelete is true to delete all vertices on the way,
     * @Params startPoint and endPoint show the edges of needed way.
     */
    private int deikstra_backway_with_delete(int endPoint, int startPoint, boolean doDelete, boolean doWrite, boolean SPMAffected) {
        int currentVertex = endPoint;
        if (doDelete) {
            if ((SPMAffected) && (vertexArray[currentVertex].checkForContains("SPM"))) {
                VertToAffectList.add(currentVertex);
            } else {
                VertToDeleteList.add(currentVertex);
            }
        }

        int counter = 1;
        Integer[] a;
        do {
            a = getCloseVerteces(currentVertex);
            for (Integer a1 : a) {
                int preWe = getWeight(currentVertex, a1);
                int weight;
                if (preWe == -1) {
                    continue;
                }
                weight = preWe + vertexArray[currentVertex].getWeight();
                if ((vertexArray[currentVertex].getPathCount() - vertexArray[a1].getPathCount()) == weight) {
                    if (doWrite) {
                        getConfigurationKeys(currentVertex, a1);
                    }
                    if (!doDelete) {
                        VertToIncreaseList.add(currentVertex);
                    } else {
                        VertToDeleteList.add(currentVertex);
                    }
                    currentVertex = a1;
                    counter++;
                    if (currentVertex == startPoint) {
                        if (doDelete) {
                            if ((SPMAffected) && (vertexArray[currentVertex].searchForPattern("SPM") != null)) {
                                VertToAffectList.add(currentVertex);
                            } else {
                                VertToDeleteList.add(currentVertex);
                            }
                        }
                    }

                    break;
                }
            }
            assert counter < 3000;
        } while (currentVertex != startPoint);

        /*if (doDelete) {
            doDelete();
        }*/
        return counter;
    }

    /**
     * Method to get weight of this link, used to get the number of needed keys
     * in CB to go from @currentVertex to @closestVertex.
     */
    private int getWeight(int currentVertex, int closestVertex) {
        String[] array = getStringParamsFromChain(currentVertex, closestVertex);
        NonOrientedCBGraph localGraphic = getOrCreateLocalGraph(array[2]);
        int result = localGraphic.getWeight(array[0], array[1]);
        if (array.length > 3) {
            NonOrientedCBGraph localGraphic2 = getOrCreateLocalGraph(array[5]);
            int result2Equal = localGraphic2.getWeight(array[3], array[4]);
            if ((result2Equal < result) && (result2Equal != 0)) {
                return result2Equal;
            }
        }

        if (result != 0) {
            return result;
        }

        return -1;
    }

    /**
     * Method to get Configuration String, get all keys in path from
     *
     * @currentVertex to @closestVertex.
     */
    private void getConfigurationKeys(int currentVertex, int closestVertex) {
        String[] array = getStringParamsFromChain(currentVertex, closestVertex);
        NonOrientedCBGraph localGraphic = getOrCreateLocalGraph(array[2]);
        int result = localGraphic.getWeight(array[0], array[1]);
        if (array.length > 3) {                                                               // 2nd CB found
            NonOrientedCBGraph localGraphic2 = getOrCreateLocalGraph(array[5]);
            int result2Equal = localGraphic2.getWeight(array[3], array[4]);                  // check if another way is better
            if ((result2Equal < result) && (result2Equal != 0)) {
                localGraphic2.getConfigurationPath(array[3], array[4]);
            }
        }
        if (result != 0) {
            localGraphic.getConfigurationPath(array[0], array[1]);
        }
    }

    /**
     * Method to find current CB's label and elemFrom - elemTo labels,
     *
     * @currentVertex and @closestVertex are connected to one CB block. Get CB's
     * label and outer (e.g. X16-X11) verteces' labels.
     */
    private String[] getStringParamsFromChain(int currentVertex, int closestVertex) {
        String elemFrom = null;
        String elemTo = null;
        String graphLabel = null;
        String elemFrom2EqualCB = null;
        String elemTo2EqualCB = null;
        String graphLabel2EqualCB = null;
        String[] currentSearch = vertexArray[currentVertex].searchForCB();
        String[] closestSearch = vertexArray[closestVertex].searchForCB();
        for (int i = 0; i < currentSearch.length; i += 2) {
            for (int j = 0; j < closestSearch.length; j += 2) {
                if (currentSearch[i].equals(closestSearch[j])) {
                    if (graphLabel != null) {
                        graphLabel2EqualCB = currentSearch[i];
                        elemFrom2EqualCB = currentSearch[i + 1];
                        elemTo2EqualCB = closestSearch[j + 1];
                        break;
                    } else {
                        graphLabel = currentSearch[i];
                        elemFrom = currentSearch[i + 1];
                        elemTo = closestSearch[j + 1];
                    }
                }
            }
        }
        if (graphLabel2EqualCB != null) {
            String[] array = {elemFrom, elemTo, graphLabel, elemFrom2EqualCB, elemTo2EqualCB, graphLabel2EqualCB};
            return array;
        }
        String[] array = {elemFrom, elemTo, graphLabel};
        return array;
    }

    /**
     * Method to get or create (if nothing found) local graph,
     *
     * @Param graphLabel is the "CB<216" type of label.
     */
    private NonOrientedCBGraph getOrCreateLocalGraph(String graphLabel) {
        int graphExists;
        for (int z = 0; z < noCBgList.size(); z++) {
            if (noCBgList.get(z).getLabel().equals(graphLabel)) {
                graphExists = z;
                return noCBgList.get(graphExists);
            }
        }

        NonOrientedCBGraph localGraphic = new NonOrientedCBGraph(graphLabel, this);
        localGraphic.refreshLinksMatrix();
        noCBgList.add(localGraphic);
        return localGraphic;
    }

    /**
     * Method is using extracting keys (ExportKeysFromScheme -- ExportKeys
     * class) method to dynamically change the state of local CB graphs, methods
     * is used only in "point to point".
     */
    private void prepareChains(String blockName) throws IOException {
        String[] p = blockName.split("<");
        assert p.length > 1; 																								// blockName xxx<1234
        Integer localNumber = Integer.valueOf(p[1]);
        try (BufferedReader localConfigurationReader = new BufferedReader(new FileReader(new File(Accessory.CONFIG_WITHOUT_MODELLING_PATH)))) {
            String line;
            if (p[0].equals("CB")) {
                while ((line = localConfigurationReader.readLine()) != null) {
                    Integer f = Integer.valueOf(line);
                    if ((f <= (localNumber + 148)) && (f >= localNumber)) {
                        int key = f - localNumber;
                        try (BufferedReader CBReader = new BufferedReader(new FileReader(new File(Accessory.CB_PATH)))) {
                            String CBline;
                            while ((CBline = CBReader.readLine()) != null) {
                                String[] spl = CBline.split(" : ");
                                if (Integer.valueOf(spl[1]) == key) {
                                    String[] s = spl[0].split(" -- ");
                                    for (String item : s) {
                                        if ((item.substring(0, 1).equals("X")) || (item.substring(0, 1).equals("Y"))) {
                                            deleteChainAndRelatedVerteces(localNumber, item, key);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                prepareChainsForBlock(p[0], localConfigurationReader, localNumber);
            }
        }
    }

    /**
     * Method is using by prepareChains method to deal with other blocks than
     * CB, methods is used only in "point to point".
     */
    private void prepareChainsForBlock(String blockName, BufferedReader localConfigurationReader, Integer localNumber) throws IOException {
        int numberOfKeys = -1;
        switch (blockName) {
            case "PPC":
                numberOfKeys = 29;
                break;
            case "SPM":
                numberOfKeys = 1816;
                break;
            case "CAU":
                numberOfKeys = 31;
                break;
            case "PAU":
                numberOfKeys = 47;
                break;
            default:
                assert false;
        }
        String line;
        while ((line = localConfigurationReader.readLine()) != null) {
            Integer f = Integer.valueOf(line);
            if ((f <= (localNumber + numberOfKeys)) && (f >= localNumber)) {
                int key = f - localNumber;
                try (BufferedReader CBReader = new BufferedReader(new FileReader(new File(Accessory.getPathToDeclaration(blockName))))) {
                    String CBline;
                    while ((CBline = CBReader.readLine()) != null) {
                        String[] spl = CBline.split(" -- ");
                        if (Integer.valueOf(spl[1]) == key) {
                            String s = spl[0];
                            deleteChainFromBlock(localNumber, s, blockName);
                        }
                    }
                }
            }
        }
    }

    /**
     * Method to delete verteces from another block those have direct links with
     * these CB's verteces.
     */
    private void deleteChainFromBlock(int localNumber, String s, String blockName) throws IOException {
        String block = blockName + "<" + String.valueOf(localNumber);
        String newElem = block + ".*" + s;
        for (int i = 0; i < vertexArray.length; i++) {
            if (vertexArray[i] != null) {
                String found = vertexArray[i].searchForPattern(newElem);
                if (found != null) {
                    if ((startingPoint == i) || (endingPoint == i)) {
                        continue;
                    }
                    String[] array = vertexArray[i].searchForCB();
                    if (array.length > 2) {
                        NonOrientedCBGraph localGraphic = getOrCreateLocalGraph(array[2]);
                        localGraphic.deleteKeyFromCBGraph(array[3], false);
                        localGraphic = getOrCreateLocalGraph(array[0]);
                        localGraphic.deleteKeyFromCBGraph(array[1], false);
                    } else if (array.length > 0) {
                        NonOrientedCBGraph localGraphic = getOrCreateLocalGraph(array[0]);
                        localGraphic.deleteKeyFromCBGraph(array[1], false);
                    }
                }
            }
        }
    }

    /**
     * Method to delete verteces from another CB those have direct links with
     * these CB's verteces.
     */
    private void deleteChainAndRelatedVerteces(int localNumber, String key, int keyToDelete) throws IOException {
        String block = "CB" + "<" + String.valueOf(localNumber);
        //String newElem = block + "." + key;
        for (int i = 0; i < vertexArray.length; i++) {
            if (vertexArray[i] != null) {
                // don't delete if it's 1 of the starting points
                if ((startingPoint == i) || (endingPoint == i)) {
                    continue;
                }
                String[] array = vertexArray[i].searchForCB();
                if ((array.length > 4) && (!vertexArray[i].isDeleted())) {
                    for (int j = 0; j < array.length; j += 2) {
                        NonOrientedCBGraph localGraphic = getOrCreateLocalGraph(array[j]);
                        localGraphic.deleteKeyFromCBGraph(array[j + 1], false);
                    }
                    vertexArray[i].setDeleted();
                } else if ((array.length > 2) && (!vertexArray[i].isDeleted())) {
                    if ((array[0].equals(block)) && (array[1].equals(key))) {
                        NonOrientedCBGraph localGraphic = getOrCreateLocalGraph(array[2]);
                        localGraphic.deleteKeyFromCBGraph(array[3], false);
                    } else if ((array[2].equals(block)) && (array[3].equals(key))) {
                        NonOrientedCBGraph localGraphic = getOrCreateLocalGraph(array[0]);
                        localGraphic.deleteKeyFromCBGraph(array[1], false);
                    }
                    vertexArray[i].setDeleted();
                }
            }
        }
    }

    /**
     * Method is used by CB graph to dynamically delete chains when external CB
     * port is used.
     */
    public void deleteChainCozUsedVertex(String CBname, String port) {
        for (int i = 0; i < vertexArray.length; i++) {
            Chain chain = vertexArray[i];
            if (chain != null) {
                String find = CBname + "\\." + port;
                if (chain.searchForPatternMatch(find) != null) {
                    deleteVertex(i);
                    return;
                }
            }
        }
    }

    /**
     * Method to import all chains in graph from file.
     */
    private void integrateChains(File graphList) throws IOException {
        try (BufferedReader graphListBufReader = new BufferedReader(new FileReader(graphList))) {
            String line;
            while ((line = graphListBufReader.readLine()) != null) {
                addVertex(line, line.split(" ")[0]);
            }
        }
    }

}
