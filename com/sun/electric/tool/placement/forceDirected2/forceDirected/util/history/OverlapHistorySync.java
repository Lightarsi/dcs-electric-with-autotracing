/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: OverlapHistorySync.java
 * Written by Team 7: Felix Schmidt, Daniel Lechner
 * 
 * This code has been developed at the Karlsruhe Institute of Technology (KIT), Germany, 
 * as part of the course "Multicore Programming in Practice: Tools, Models, and Languages".
 * Contact instructor: Dr. Victor Pankratius (pankratius@ipd.uka.de)
 *
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.electric.tool.placement.forceDirected2.forceDirected.util.history;

import com.sun.electric.tool.placement.PlacementFrame.PlacementNode;

/**
 * Parallel Placement
 */
public class OverlapHistorySync extends OverlapHistory<PlacementNode> {

	private static OverlapHistorySync instance = new OverlapHistorySync();

	public static OverlapHistorySync getInstance() {
		return instance;

	}

	private OverlapHistorySync() {

	}

	public synchronized boolean isMovementInHistory(PlacementNode node1, PlacementNode node2) {
		return super.isMovementInHistory(node1, node2);
	}

	public synchronized void removeHistory(PlacementNode node1, PlacementNode node2) {
		super.removeHistory(node1, node2);
	}

	public synchronized void saveHistory(PlacementNode node1, PlacementNode node2) {
		super.saveHistory(node1, node2);
	}
}
