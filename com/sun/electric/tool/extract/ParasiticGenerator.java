/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: ParasiticGenerator.java
 *
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.electric.tool.extract;

import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.network.Netlist;

/**
 * This class should be used to create ExtractedPBucket,
 * depending on tool.
 */
public interface ParasiticGenerator {
    public ExtractedPBucket createBucket(NodeInst ni, ParasiticTool.ParasiticCellInfo info);
}
