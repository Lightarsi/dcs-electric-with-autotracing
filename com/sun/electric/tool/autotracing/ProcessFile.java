/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.autotracing;

/**
 *
 * @author Astepanov
 */
public class ProcessFile {

    String str = null;

    private static ProcessFile elem;

    private ProcessFile() {
    }

    public static ProcessFile getProcessFile() {
        if (elem == null) {
            elem = new ProcessFile();
        }
        return elem;
    }

    public String cau(String shortNamePin) {
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

    public String cauComp(String shortNamePin) {
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

    public String cauPosFb(String shortNamePin) {
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

    public String cauNegFb(String shortNamePin) {
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

    public String pau(String shortNamePin) {
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

    public String pauDiff(String shortNamePin) {
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

    public String pauDiffFb(String shortNamePin) {
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

    public String pauNegFb(String shortNamePin) {
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

    public String pauComp(String shortNamePin) {
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
