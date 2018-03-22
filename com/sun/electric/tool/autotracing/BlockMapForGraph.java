/* Electric(tm) VLSI Design System
 *
 * File: BlockMapForGraph.java
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

/**
 * This class serves to unite blocks and real pin names.
 */
public class BlockMapForGraph {

    private String str;

    private static BlockMapForGraph blockMap;

    private BlockMapForGraph() {
    }

    public static BlockMapForGraph getBlockMapForGraph() {
        if (blockMap == null) {
            blockMap = new BlockMapForGraph();
        }
        return blockMap;
    }

    /**
     * This method receives a shortNamePin returns real Pin number
     * @param shortNamePin
     * @return 
     */
    public String getAdrForCau(String shortNamePin) {
        switch (shortNamePin) {
            case "INM_1":
            case "INM_2":
            case "INM": // deprecated
                str = "CAU.*PX[123]";
                break;
            case "INP_1":
            case "INP_2":
            case "INP": // deprecated
                str = "CAU.*PY[123]";
                break;
            case "OUT_1":
            case "OUT_2":
            case "OUT": // deprecated
                str = "CAU.*P[VW][123]";
                break;
        }
        return str;
    }

    public String getAdrForCauComp(String shortNamePin) {
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "CAU.*\\.PX[123]";
                break;
            case "INP_1":
            case "INP_2":
            case "INP": // deprecated
                str = "CAU.*\\.PY[123]";
                break;
            case "OUT": // deprecated
            case "OUT_1":
            case "OUT_2":
                str = "CAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    public String getAdrForCauPosFb(String shortNamePin) {
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "CAU.*\\.PX[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "CAU.*\\.PZ[123]";
                break;
            case "INP_3":
            case "INP_4":
                str = "CAU.*\\.PY[123]";
                break;
            case "OUT": // deprecated
            case "OUT_1":
            case "OUT_2":
                str = "CAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    public String getAdrForCauNegFb(String shortNamePin) {
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "CAU.*\\.PZ[123]";
                break;
            case "INM_3":
            case "INM_4":
                str = "CAU.*\\.PX[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "CAU.*\\.PY[123]";
                break;
            case "OUT": // deprecated
            case "OUT_1":
            case "OUT_2":
                str = "CAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    public String getAdrForPau(String shortNamePin) {
        switch (shortNamePin) {
            case "INM_1":
            case "INM_2":
            case "INM": // deprecated
                str = "PAU.*\\.PY[123]";
                break;
            case "INP_1":
            case "INP_2":
            case "INP": // deprecated
                str = "PAU.*\\.PX[123]";
                break;
            case "OUT_1":
            case "OUT_2":
            case "OUT": // deprecated
                str = "PAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    public String getAdrForPauDiff(String shortNamePin) {
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "PAU.*\\.PY[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "PAU.*\\.PX[123]";
                break;
            case "OUTP": // deprecated
            case "OUTP_1":
            case "OUTP_2":
                str = "PAU.*\\.P[VW][123]";
                break;
            case "OUTM": // deprecated
            case "OUTM_1":
            case "OUTM_2":
                str = "PAU.*\\.P[UR][123]";
                break;
        }
        return str;
    }

    public String getAdrForPauDiffFb(String shortNamePin) {
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "PAU.*\\.PZ[123]";
                break;
            case "INM_3":
            case "INM_4":
                str = "PAU.*\\.PY[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "PAU.*\\.PO[123]";
                break;
            case "INP_3":
            case "INP_4":
                str = "PAU.*\\.PX[123]";
                break;
            case "OUTM": // deprecated
            case "OUTM_1":
            case "OUTM_2":
                str = "PAU.*\\.P[UR][123]";
                break;
            case "OUTP": // deprecated
            case "OUTP_1":
            case "OUTP_2":
                str = "PAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    public String getAdrForPauNegFb(String shortNamePin) {
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "PAU.*\\.PZ[123]";
                break;
            case "INM_3":
            case "INM_4":
                str = "PAU.*\\.PY[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "PAU.*\\.PX[123]";
                break;
            case "OUT": // deprecated
            case "OUT_1":
            case "OUT_2":
                str = "PAU.*\\.P[VW][123]";
                break;
        }
        return str;
    }

    public String getAdrForPauComp(String shortNamePin) {
        switch (shortNamePin) {
            case "INM": // deprecated
            case "INM_1":
            case "INM_2":
                str = "PAU.*\\.PY[123]";
                break;
            case "INP": // deprecated
            case "INP_1":
            case "INP_2":
                str = "PAU.*\\.PX[123]";
                break;
            case "OUT": // deprecated
            case "OUT_1":
            case "OUT_2":
                str = "PAU.*\\.PS[123]";
                break;
        }
        return str;
    }

}
