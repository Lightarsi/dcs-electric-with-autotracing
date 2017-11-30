/* Electric(tm) VLSI Design System
 *
 * File: NonOrientedGraph.java
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

abstract class NonOrientedGraph {

    protected String graphName;
    protected Vertex[] vertexArray; // Array of Vertices
    protected int vertexCount;
    protected List<? extends Vertex> vertexList;

    /**
     * Constructor: Set graph name.
     */
    public NonOrientedGraph(String graphName) {
        this.graphName = graphName;
        this.vertexCount = 0;
    }

    /**
     * Uniq label
     */
    public String getLabel() {
        return this.graphName;
    }

    /**
     * Initialise matrix and vertexArray.
     */
    abstract protected void Init(int VERTEX_MAX);

    /**
     * Method to reset all pathcounts of vertices in graph.
     */
    abstract protected void resetVertices();

    /**
     * Method to form new vertex in graph, check block should be used only for
     * importing graph and ignoring after that *** DEVELOP ***.
     */
    abstract protected boolean addVertex(String vertexInfo, String label);

    /**
     * Method to delete vertex from graph,
     *
     * @Param count is the number of vertex which should be deleted.
     */
    abstract protected void deleteVertex(int count);

    /**
     * Method returns the array of integers for position of adj vertices ,
     * Returns null when nothing found,
     *
     * @Param v is the current vertex in graph.
     */
    abstract protected Integer[] getCloseVerteces(int v);

}
