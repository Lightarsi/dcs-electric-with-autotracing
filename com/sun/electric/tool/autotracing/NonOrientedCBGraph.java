/* Electric(tm) VLSI Design System
 *
 * File: NonOrientedCBGraph.java
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
import java.util.List;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

/**
 * This class is used to describe local CB graph, this global graph is the
 * complex of verteces and links between them.
 */
public final class NonOrientedCBGraph extends NonOrientedGraph {

    private final int VERTEX_MAX = 54;
    private final int GLOBAL_VERTS = 26;
    private final String[] globVerts = {"X11", "X12", "X13", "X14", "X15", "X16", "X21", "X22", "X23", "X24", "X25", "X26",
        "Y11", "Y12", "Y13", "Y14", "Y15", "Y16", "Y21", "Y22", "Y23", "Y24", "Y25", "Y26",
        "X", "Y"};

    private int[][] matrix; // Adjacency matrix
    private String[][] keyMatrix; // matrix for key values (key number in CB.trc)
    private int linksMatrix[][];

    private List<Integer> VertToDeleteList = new ArrayList<>();
    private final NonOrientedGlobalGraph creator;

    /**
     * Constructor: Parent, graphName required to be type: "CB<216", add adj
     * matrix, initialise Vertex Array, constructor: Child, initialise internal
     * matrix with size of GLOBAL_VERTS. @param graphName @param graphName
     */
    public NonOrientedCBGraph(String graphName, NonOrientedGlobalGraph creator) {
        super(graphName);
        this.creator = creator;
        Init(VERTEX_MAX);
        importGraphFromFile();
        linksMatrix = new int[GLOBAL_VERTS][GLOBAL_VERTS];
        for (int i = 0; i < GLOBAL_VERTS; i++) {
            for (int j = 0; j < GLOBAL_VERTS; j++) {
                this.linksMatrix[i][j] = 0;
            }
        }
        try {
            UseSchemeConfiguration(graphName);
        } catch (IOException ioe) {
            System.out.println("IOException found.");
            ioe.printStackTrace();
        }
    }

    /**
     * Method is deleting verteces from local CB graph according to keys.
     *
     * @param key
     */
    public void deleteKeyFromCBGraph(String key, boolean forAuto) {
        int keyNum = findVertex(key);
        if (keyNum != -1) {
            if(forAuto) {
                deleteVertex(keyNum);
            } else {
                deleteVertexNotForAuto(keyNum);
            }
        }
        refreshLinksMatrix();
    }
    
    /**
     * Only developer method, method to print internal links matrix.
     */
    public void getLinksMatrix() {
        String line = "";
        for (int i = 0; i < GLOBAL_VERTS; i++) {
            for (int j = 0; j < GLOBAL_VERTS; j++) {
                line += String.valueOf(linksMatrix[i][j]);
                line += " ";
            }
            System.out.println("linex " + line);
            line = "";
        }
    }

    /**
     * Method to get the length of path using links matrix,
     *
     * @param elemFrom
     * @param elemTo
     * @return
     * @Params elemFrom, elemTo mean .
     */
    public int getWeight(String elemFrom, String elemTo) {
        return linksMatrix[findIntForLinksMatrix(elemFrom)][findIntForLinksMatrix(elemTo)];
    }

    /**
     * Method to find path and write real keys in CB,
     *
     * @param elemFrom
     * @param elemTo
     * @Params elemFrom, elemTo mean.
     */
    public void getConfigurationPath(String elemFrom, String elemTo) {
        deikstra(findVertex(elemFrom));
        deikstra_backway_with_config(findVertex(elemTo), findVertex(elemFrom), true);  // was false doDelete();
        resetVertices();
    }

    /**
     * Method to delete vertices that was used in previous autotracing steps.
     */
    public void doDeleteUsedVerts() {
        if (!VertToDeleteList.isEmpty()) {
            Iterator<Integer> deleteItr = VertToDeleteList.iterator();
            while (deleteItr.hasNext()) {
                deleteVertex(deleteItr.next());
                deleteItr.remove();
            }
        }
        refreshLinksMatrix();
    }

    /**
     * Method using deikstra algorith to find weights between global CB vertices
     * and refresh links matrix.
     */
    public void refreshLinksMatrix() {
        for (int i = 0; i < GLOBAL_VERTS; i++) {
            for (int j = 0; j < GLOBAL_VERTS; j++) {
                this.linksMatrix[i][j] = 0;
            }
        }
        for (String vert : globVerts) {
            int vertNum = findVertex(vert);
            if (vertNum != -1) {
                deikstraFindAll(vertNum);
            }
        }
    }

    /**
     * Reset for using in another Global graph.
     */
    public void reset() {
        VertToDeleteList = new ArrayList<>();
    }

    /**
     * Initialise matrix and vertexArray, should be overriden for another
     * matrixes.
     *
     * @param VERTEX_MAX
     */
    @Override
    protected void Init(int VERTEX_MAX) {
        matrix = new int[VERTEX_MAX][VERTEX_MAX];
        keyMatrix = new String[VERTEX_MAX][VERTEX_MAX];
        vertexArray = new Vertex[VERTEX_MAX];
        for (int i = 0; i < VERTEX_MAX; i++) {
            for (int j = 0; j < VERTEX_MAX; j++) {
                matrix[i][j] = 0;
                keyMatrix[i][j] = "";
            }
        }
    }

    /**
     * Method to form new vertex in graph, TODO: remove check-for-same-label
     * block.
     *
     * @param vertexInfo
     * @param label
     * @return
     */
    @Override
    protected boolean addVertex(String vertexInfo, String label) {
        //check for same label
        for (int j = 0; j < vertexCount; j++) {
            if (vertexArray[j] != null) {
                if (vertexArray[j].getLabel().equals(label)) {
                    return false;
                }
            }
        }
        vertexArray[vertexCount++] = new Vertex(label);
        return true;
    }

    /**
     * Method to delete vertex from graph,
     *
     * @param count
     * @Param count is the number of vertex which should be deleted.
     */
    @Override
    protected void deleteVertex(int count) {
        if (count == -1) {
            return;
        }
        if (vertexArray[count] != null) {
            for (int i = 0; i < vertexCount; i++) {
                matrix[i][count] = 0;
                matrix[count][i] = 0;
            }
            String label = vertexArray[count].getLabel();
            vertexArray[count] = null;
            Accessory.printLog(graphName);
            Accessory.printLog("label " + label);
            creator.deleteChainCozUsedVertex(graphName, label);
        }
    }
    
    /**
     * Method to delete vertex from graph,
     *
     * @param count
     * @Param count is the number of vertex which should be deleted.
     */
    protected void deleteVertexNotForAuto(int count) {
        if (count == -1) {
            return;
        }
        if (vertexArray[count] != null) {
            for (int i = 0; i < vertexCount; i++) {
                matrix[i][count] = 0;
                matrix[count][i] = 0;
            }
            vertexArray[count] = null;
        }
    }

    /**
     * Method to reset all pathcounts of vertices in graph.
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
     * Method returns the array of integers for position of adj vertices ,
     * Returns null when nothing found,
     *
     * @param v
     * @return
     * @Param v is the current vertex in graph.
     */
    @Override
    protected Integer[] getCloseVerteces(int v) {
        List<Integer> Verts = new ArrayList<>();
        for (int j = 0; j < vertexCount; j++) {
            if (vertexArray[j] != null) {
                if ((matrix[v][j] > 0) && (vertexArray[j].getVisited() == false)) {
                    Verts.add(j);
                }
            }
        }
        if (!Verts.isEmpty()) {
            Integer a[] = new Integer[Verts.size()];
            a = Verts.toArray(a);
            return a;
        } else {
            Integer a[] = new Integer[0];
            return a; //no more verteces here
        }
    }

    /**
     * Method is using extracting keys (ExportKeysFromScheme -- ExportKeys
     * class) method to dynamically change the state of local CB graphs,
     *
     * @Param graphName is used to find the number of block.
     */
    private void UseSchemeConfiguration(String graphName) throws IOException {
        String[] p = graphName.split("<");																					// graphName xxx<1234
        Integer localNumber = Integer.valueOf(p[1]);
        try (BufferedReader localConfigurationReader = new BufferedReader(new FileReader(new File(Accessory.CONFIG_WITHOUT_MODELLING_PATH)))) {
            String line;
            while ((line = localConfigurationReader.readLine()) != null) {
                Integer f = Integer.valueOf(line);
                if ((f < (localNumber + Accessory.CB_PATH_LENGTH)) && (f >= localNumber)) {
                    deleteKeyFromCBGraph(f - localNumber);
                }
            }
        }
    }

    /**
     * Method is deleting verteces from local CB graph according to keys,
     * additive method to UseSchemeConfiguration.
     */
    private void deleteKeyFromCBGraph(Integer key) throws IOException {
        try (BufferedReader autotraReader = new BufferedReader(new FileReader(new File(Accessory.CB_PATH)))) {
            String line;
            while ((line = autotraReader.readLine()) != null) {
                String[] p = line.split(" : ");
                if (Objects.equals(Integer.valueOf(p[1]), key)) {
                    String[] s = p[0].split(" -- ");
                    deleteVertex(findVertex(s[0]));
                    deleteVertex(findVertex(s[1]));
                }
            }
        }
    }

    /**
     * Method is used to cover full graph from 1 point and to count lengths of
     * the ways,
     *
     * @Param startPoint shows the number of vertice in main matrix, method used
     * in cycle to cover all vertices.
     */
    private void deikstraFindAll(int startPoint) {
        deikstra(startPoint);
        renewOneLineForLinksMatrix(startPoint);
        resetVertices();
    }

    /**
     * Method is used to cover full graph from 1 point and to count the length
     * of the ways, one of the local deikstraFindAll methods.
     */
    private void deikstra(int startPoint) {
        BinaryHeapNew heap = new BinaryHeapNew();
        int curPathCount;
        Integer closestVertex;
        int currentVertex = startPoint;

        vertexArray[currentVertex].setVisited(true);
        vertexArray[currentVertex].setPathCount(0);

        heap.add(vertexArray[currentVertex].getPathCount(), currentVertex);

        int counter = 0;
        while ((closestVertex = heap.getMinKey()) != -1) {
            counter++;
            assert counter < 1000;
            vertexArray[closestVertex].setVisited(true);
            Integer[] a = getCloseVerteces(closestVertex);
            if (a == null) {
                continue;
            }

            for (Integer a1 : a) {
                currentVertex = a1;
                if (((vertexArray[currentVertex].getPathCount()) > (curPathCount = (vertexArray[closestVertex].getPathCount()
                        + matrix[currentVertex][closestVertex]))) && (matrix[currentVertex][closestVertex] != 0)) {
                    vertexArray[currentVertex].setPathCount(curPathCount);
                }
                heap.add(vertexArray[currentVertex].getPathCount(), currentVertex);
            }
        }
        for (int j = 0; j < vertexCount; j++) {
            if (vertexArray[j] != null) {
                vertexArray[j].setVisited(false);
            }
        }
    }

    /**
     * This method use deikstra results to find all verteces used by this way,
     * this method can delete vertices to dynamic modification of graph,
     *
     * @Param doDelete is true to delete all vertices on the way,
     * @Params startPoint and endPoint show the edges of needed way.
     */
    private void deikstra_backway_with_config(int endPoint, int startPoint, boolean doDelete) {
        int currentVertex = endPoint;
        VertToDeleteList.add(currentVertex);
        Integer[] a;
        int counter = 0;
        do {
            a = getCloseVerteces(currentVertex);
            for (Integer a1 : a) {
                if (((vertexArray[currentVertex].getPathCount() - vertexArray[a1].getPathCount()) == matrix[currentVertex][a1]) && (matrix[currentVertex][a1] != 0)) {
                    int labelNumber = Integer.parseInt(this.getLabel().split("<")[1]);
                    labelNumber += Integer.parseInt(keyMatrix[currentVertex][a1]);
                    Accessory.write(Accessory.CONFIG_PATH, String.valueOf(labelNumber));
                    currentVertex = a1;
                    VertToDeleteList.add(currentVertex);
                    break;
                }
            }
            counter++;
            assert counter < 1000;
        } while (currentVertex != startPoint);
    }

    /**
     * Overloaded parent method, imports CB graph file.
     */
    private void importGraphFromFile() {
        File fileForImport = new File(Accessory.CB_PATH);
        try {
            importGraphFromFile(fileForImport);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Method renewing links matrix, method implements the additive part to
     * deikstra method function which is used specifically to ready CB block
     * (X11-X16, X21-X26, Y11-Y16, Y21-Y26),
     *
     * @Param startPoint shows the number of vertice in main matrix.
     */
    private void renewOneLineForLinksMatrix(int startPoint) {
        int internalInt = findIntForLinksMatrix(vertexArray[startPoint].getLabel());
        int InternalSecondInt;
        for (int i = 0; i < vertexCount; i++) {
            if ((vertexArray[i] != null) && (i != startPoint)) {
                for (String vert : globVerts) {
                    if (vertexArray[i].getLabel().equals(vert)) {
                        // linksMatrix element is 0 if there is no path.
                        if (vertexArray[i].getPathCount() == vertexArray[i].MAXPATHCOUNT) {
                            continue;
                        }

                        InternalSecondInt = findIntForLinksMatrix(vertexArray[i].getLabel());
                        linksMatrix[internalInt][InternalSecondInt] = vertexArray[i].getPathCount();
                        linksMatrix[InternalSecondInt][internalInt] = vertexArray[i].getPathCount();
                    }
                }
            }
        }
    }

    /**
     * Method finds the number of vertice in internal links matrix,
     *
     * @Param find this is the label of vertex in arrays.
     */
    private int findIntForLinksMatrix(String findThis) {
        for (int j = 0; j < globVerts.length; j++) {
            String vert = globVerts[j];
            if (findThis.equals(vert)) {
                return j;
            }
        }
        Accessory.printLog("Something went wrong, variable is " + findThis);
        return -1;
    }

    // Next will be internal methods to create new objects
    /**
     * Method to add edge to adj matrix.
     */
    private void addPoint(int begin, int end, int weight) {
        matrix[begin][end] = weight;
        matrix[end][begin] = weight;
    }

    /**
     * Method to add edge to key values matrix.
     */
    private void addKeyPoint(int begin, int end, String weight) {
        keyMatrix[begin][end] = weight;
        keyMatrix[end][begin] = weight;
    }

    /**
     * Method to find the number of vertex from it's label.
     */
    private int findVertex(String label) {
        for (int i = 0; i < vertexCount; i++) {
            if ((vertexArray[i] != null) && (vertexArray[i].getLabel().equals(label))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * This method creates graph, adding vertices and points using data from
     * file,
     *
     * @Param graphList is file with adj list.
     */
    private void importGraphFromFile(File graphList) throws IOException {
        BufferedReader graphListBufReader = new BufferedReader(new FileReader(graphList));

        String line;
        while ((line = graphListBufReader.readLine()) != null) {
            String[] connectsAndNumbers = line.split(" : "); 							// X11 - a3 : 35
            String[] connectedVertices = connectsAndNumbers[0].split(" -- ");
            int conVertsLength = connectedVertices.length;
            int[] numConnectedVertices = new int[conVertsLength];
            for (int i = 0; i < conVertsLength; i++) {
                addVertex(null, connectedVertices[i]);
                numConnectedVertices[i] = findVertex(connectedVertices[i]);
                assert (numConnectedVertices[i] >= 0);
            }

            for (int i = 0; i < conVertsLength; i++) {
                for (int j = 0; j < conVertsLength; j++) {
                    if ((numConnectedVertices[i] < numConnectedVertices[j]) && (Math.abs(i - j) == 1)) {
                        addPoint(numConnectedVertices[i], numConnectedVertices[j], 1);
                        addKeyPoint(numConnectedVertices[i], numConnectedVertices[j], connectsAndNumbers[1]);
                    }
                }
            }
        }
    }

}
