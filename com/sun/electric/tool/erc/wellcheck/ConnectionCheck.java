/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: ConnectionCheck.java
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
package com.sun.electric.tool.erc.wellcheck;

import com.sun.electric.database.topology.RTNode;
import com.sun.electric.tool.erc.ERCWellCheck.StrategyParameter;
import com.sun.electric.tool.erc.ERCWellCheck.WellBound;
import com.sun.electric.tool.erc.ERCWellCheck.WellType;
import java.util.BitSet;

/**
 * @author Felix Schmidt
 *
 */
public class ConnectionCheck implements WellCheckAnalysisStrategy {

	private StrategyParameter parameter;
	private boolean hasPCon;
	private boolean hasNCon;
	private RTNode<WellBound> pWellRoot;
	private RTNode<WellBound> nWellRoot;
    private BitSet connectedNetValues;

	/**
	 * @param parameter
	 * @param hasPCon
	 * @param hasNCon
	 * @param pWellRoot
	 * @param nWellRoot
	 */
	public ConnectionCheck(StrategyParameter parameter, boolean hasPCon, boolean hasNCon, RTNode<WellBound> pWellRoot,
			RTNode<WellBound> nWellRoot, BitSet connectedNetValues) {
		super();
		this.parameter = parameter;
		this.hasPCon = hasPCon;
		this.hasNCon = hasNCon;
		this.pWellRoot = pWellRoot;
		this.nWellRoot = nWellRoot;
        this.connectedNetValues = connectedNetValues;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sun.electric.tool.erc.wellcheck.WellCheckAnalysisStrategy#execute()
	 */
    @Override
	public void execute() {
		if (parameter.getWellPrefs().pWellCheck != 2)
			findUnconnected(pWellRoot, pWellRoot, WellType.pwell);
		if (parameter.getWellPrefs().nWellCheck != 2)
			findUnconnected(nWellRoot, nWellRoot, WellType.nwell);
		if (parameter.getWellPrefs().pWellCheck == 1 && !hasPCon) {
			parameter.logError("No P-Well contact found in this cell");
		}
		if (parameter.getWellPrefs().nWellCheck == 1 && !hasNCon) {
			parameter.logError("No N-Well contact found in this cell");
		}
	}

	private void findUnconnected(RTNode<WellBound> rtree, RTNode<WellBound> current, WellType type) {
		for (int j = 0; j < current.getTotal(); j++) {
			if (current.getFlag()) {
				WellBound child = current.getChildLeaf(j);
                NetValues nv = child.getNetID();
                if (connectedNetValues != null)
                {
                    if (!connectedNetValues.get(nv.getIndex())) {
                        connectedNetValues.set(nv.getIndex());
                        parameter.logError("No " + type + "-Well contact in this area", child);
                    }
                } else
                {
                    if (nv == null) {
                        Utils.spreadWellSeed(child.getBounds().getCenterX(), child.getBounds().getCenterY(),
                                new NetValues(), rtree, 0);
                        parameter.logError("No " + type + "-Well contact in this area", child);
                    }
                }
			} else {
				RTNode<WellBound> child = current.getChildTree(j);
				findUnconnected(rtree, child, type);
			}
		}
	}

}
