/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: AStarMachine.java
 * Written by: Christian Harnisch, Ingo Besenfelder, Michael Neumann (Team 3)
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
package com.sun.electric.tool.routing.experimentalAStar2.machine;

import java.util.List;

import com.sun.electric.tool.routing.experimentalAStar2.algorithm.AStarGoalBase;
import com.sun.electric.tool.routing.experimentalAStar2.algorithm.AStarMapBase;
import com.sun.electric.tool.routing.experimentalAStar2.algorithm.AStarNodeBase;

/**
 * An AStarMachine simplifies path search by providing a ready-to-use
 * configuration behind a minimal interface. (Facade Pattern)
 */
public interface AStarMachine<T extends AStarNodeBase<T>>
{
  public void setUpSearchSpace(AStarMapBase<T> newMap, AStarGoalBase<T> newGoal);

  public List<T> findPath(int startX, int startY, int startZ, int goalX, int goalY, int goalZ);
}
