/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: SiliconChip.java
 * Written by Jonathan Gainsley, Sun Microsystems.
 *
 * Copyright (c) 2004, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.electric.tool.simulation.test;

import java.util.List;
import java.util.Date;

public class SiliconChip implements ChipModel {

    public SiliconChip() {
    }

    public void wait(float seconds) {
        Infrastructure.wait(seconds);
    }

    public void waitNS(double nanoseconds) {
        Infrastructure.wait((float)(nanoseconds/1e9));
    }

    public void waitPS(double picoseconds) {
        Infrastructure.wait((float)(picoseconds/1e12));
    }
}
