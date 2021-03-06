/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: FoldsAndWidth.java
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
package com.sun.electric.tool.generator.layout;

/**
 * This class is used to return the result from StdCellParms.calcFoldsAndWidth.
 */
public class FoldsAndWidth {
	/** number of folds */
	public final int nbFolds;

	/** electrical width of the gate of a single fold */
	public final double gateWid;

	/** for very small gate widths, the physical width of a transistor
	 * is determined by the width of the diffusion contact */
	public final double physWid;

	public FoldsAndWidth(int nbFolds, double gateWid, double physWid) {
		this.nbFolds = nbFolds;
		this.gateWid = gateWid;
		this.physWid = physWid;
	}
}
