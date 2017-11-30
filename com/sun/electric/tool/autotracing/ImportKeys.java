/* Electric(tm) VLSI Design System
 *
 * File: ExportKeys.java
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

import com.sun.electric.database.topology.PortInst;
import com.sun.electric.technology.ArcProto;
import com.sun.electric.technology.technologies.Generic;
import com.sun.electric.database.EditingPreferences;

import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.NodeInst;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

import java.util.Iterator;

import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.user.User;
import java.util.concurrent.TimeUnit;

/**
 * Class uses ImportKeys script algorithm to import keys to scheme from file.
 */
public class ImportKeys {

    private static float getStringCount(File file) {
        float qr = 0;
        BufferedReader bufferedReader;
        try {
            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.readLine() != null) {
                qr += 1;
            }
            bufferedReader.close();
            return qr;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return 0;
    }

    // just sort
    private static int equality(int a, int b, int keyNumber) {
        if ((a >= b) && (a <= keyNumber)) {
            return a;
        } else {
            return b;
        }
    }
    // find diff bw key and instance

    private static int keyFind(int keyNumber, String cname) {
        String[] parts = cname.split("ic");
        String part1 = parts[0];
        char[] charArray = part1.toCharArray();
        int i = 0;
        while (Character.isLetter(charArray[i])) {
            i++;
        }
        String cnumber = "";
        for (i++; i < (charArray.length - 2); i++) {
            cnumber += charArray[i];
        }
        int keyFind = keyNumber - Integer.parseInt(cnumber);
        int keyin = keyFind * 2 + 1;
        return keyin;
    }
    // find instance which is the closest to key but less or equal

    private static int findInst(int alis) {
        int bika = 0;
        String cname;
        try {
            BufferedReader br2 = new BufferedReader(new FileReader("c:\\CYGELENG\\electric\\global_scheme.info"));
            String line;
            while ((line = br2.readLine()) != null) {
                cname = line;
                String[] parts = cname.split("<");
                String part2 = parts[1];
                bika = equality(Integer.parseInt(part2), bika, alis);
            }
            br2.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return bika;
    }
    // return the name of instance that key belongs to

    private static String findbika(int bika) {
        int i = 0;
        String cname;
        try {
            BufferedReader br3 = new BufferedReader(new FileReader("c:\\CYGELENG\\electric\\global_scheme.info"));
            String line;
            while ((line = br3.readLine()) != null) {
                cname = line;
                String[] parts = cname.split("<");
                String part2 = parts[1];
                if (Integer.parseInt(part2) == bika) {
                    //return i;
                    return cname;
                }
                i++;
            }
            br3.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    /**
     * Method to import keys from file. user is specifying file.
     *
     * @param cell
     * @throws java.io.IOException
     */
    public static void controller(Cell cell) throws IOException {
        System.out.println("Script started");
        boolean nextAuto = true;
        // Return cell in given window
        Cell curcell;
        if (cell == null) {
            nextAuto = false;
            curcell = Job.getUserInterface().getCurrentCell();
        } else {
            curcell = cell;
        }

        String DirF = Accessory.CONFIG_PATH;

        File f = new File(DirF);
        float count = getStringCount(f);

        int k = 0;
        double size = 0.5;
        String[] key = new String[50000];
        int keyquant;
        String keyName;
        // each string from import.txt is checking for key and instance(from global_scheme)
        try (BufferedReader br = new BufferedReader(new FileReader(DirF))) {
            float count_cont = 0;
            int percentage_last = 0;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                key[k] = line;
                keyquant = findInst(Integer.parseInt(key[k]));
                keyName = findbika(keyquant);
                int diff_import = Integer.parseInt(key[k]) - keyquant;
                String diff_key_not_SPM = ("n" + ((Integer.parseInt(key[k]) - keyquant) * 2 + 1));
                String diff_key_not_SPM_1 = ("n" + ((Integer.parseInt(key[k]) - keyquant) * 2 + 2));
                String SPM_check = keyName.substring(0, 3);
                String diff_key_SPM = "";
                String diff_key_SPM_1 = "";
                if (SPM_check.equals("SPM")) {
                    if (diff_import <= 107) {
                        diff_key_SPM = diff_key_not_SPM;
                        diff_key_SPM_1 = diff_key_not_SPM_1;
                    } else if ((diff_import > 107) && (diff_import <= 279)) {
                        diff_key_SPM = ("o" + (((Integer.parseInt(key[k]) - keyquant) - 108) * 2 + 1));
                        diff_key_SPM_1 = ("o" + (((Integer.parseInt(key[k]) - keyquant) - 108) * 2 + 2));
                    } else if ((diff_import > 279) && (diff_import <= 507)) {
                        diff_key_SPM = ("p" + (((Integer.parseInt(key[k]) - keyquant) - 280) * 2 + 1));
                        diff_key_SPM_1 = ("p" + (((Integer.parseInt(key[k]) - keyquant) - 280) * 2 + 2));
                    } else if ((diff_import > 507) && (diff_import <= 679)) {
                        diff_key_SPM = ("q" + (((Integer.parseInt(key[k]) - keyquant) - 508) * 2 + 1));
                        diff_key_SPM_1 = ("q" + (((Integer.parseInt(key[k]) - keyquant) - 508) * 2 + 2));
                    } else if ((diff_import > 679) && (diff_import <= 907)) {
                        diff_key_SPM = ("r" + (((Integer.parseInt(key[k]) - keyquant) - 680) * 2 + 1));
                        diff_key_SPM_1 = ("r" + (((Integer.parseInt(key[k]) - keyquant) - 680) * 2 + 2));
                    } else if ((diff_import > 907) && (diff_import <= 1079)) {
                        diff_key_SPM = ("s" + (((Integer.parseInt(key[k]) - keyquant) - 908) * 2 + 1));
                        diff_key_SPM_1 = ("s" + (((Integer.parseInt(key[k]) - keyquant) - 908) * 2 + 2));
                    } else if ((diff_import > 1079) && (diff_import <= 1307)) {
                        diff_key_SPM = ("t" + (((Integer.parseInt(key[k]) - keyquant) - 1080) * 2 + 1));
                        diff_key_SPM_1 = ("t" + (((Integer.parseInt(key[k]) - keyquant) - 1080) * 2 + 2));
                    } else if ((diff_import > 1307) && (diff_import <= 1479)) {
                        diff_key_SPM = ("u" + (((Integer.parseInt(key[k]) - keyquant) - 1308) * 2 + 1));
                        diff_key_SPM_1 = ("u" + (((Integer.parseInt(key[k]) - keyquant) - 1308) * 2 + 2));
                    } else if ((diff_import > 1479) && (diff_import <= 1707)) {
                        diff_key_SPM = ("v" + (((Integer.parseInt(key[k]) - keyquant) - 1480) * 2 + 1));
                        diff_key_SPM_1 = ("v" + (((Integer.parseInt(key[k]) - keyquant) - 1480) * 2 + 2));
                    } else if ((diff_import > 1707) && (diff_import <= 1815)) {
                        diff_key_SPM = ("w" + (((Integer.parseInt(key[k]) - keyquant) - 1708) * 2 + 1));
                        diff_key_SPM_1 = ("w" + (((Integer.parseInt(key[k]) - keyquant) - 1708) * 2 + 2));
                    }
                }

                NodeInst z;
                Iterator<NodeInst> nodeItr = curcell.getNodes();
                while (nodeItr.hasNext()) {
                    z = nodeItr.next();
                    if (z.getProto().getName().equals(keyName)) {
                        int expnum = z.getNumPortInsts();
                        int j = 0;
                        while (j < expnum) {
                            PortInst z_exp1 = z.getPortInst(j);
                            j++;
                            String myString = z_exp1.toString();
                            String[] parts1 = myString.split("].");
                            String name1 = parts1[1];
                            if (name1.equals(diff_key_not_SPM + "'")) {
                                int q = 0;
                                while (q < expnum) {
                                    PortInst z_exp2 = z.getPortInst(q);
                                    String myString2 = z_exp2.toString();
                                    String[] parts2 = myString2.split("].");
                                    String name2 = parts2[1];
                                    if (name2.equals(diff_key_not_SPM_1 + "'")) {
                                        ArcProto arc = Generic.tech().universal_arc;
                                        new CreateNewArc(arc, z_exp1, z_exp2, size);
                                    }
                                    q++;
                                }
                            }
                            if (name1.equals(diff_key_SPM + "'")) {
                                int q = 0;
                                while (q < expnum) {
                                    PortInst z_exp2 = z.getPortInst(q);
                                    String myString2 = z_exp2.toString();
                                    String[] parts2 = myString2.split("].");
                                    String name2 = parts2[1];
                                    if (name2.equals(diff_key_SPM_1 + "'")) {
                                        ArcProto arc = Generic.tech().universal_arc;
                                        Job job = new CreateNewArc(arc, z_exp1, z_exp2, size);
                                        /*while (job.getStatus().equals("done")) {
                                        }*/
                                    }
                                    q++;
                                }
                            }
                        }
                    }
                }
                count_cont += 1;
                if (percentage_last != ((int) ((count_cont / count) * 100))) {
                    System.out.println((int) ((count_cont / count) * 100) + "%");
                    percentage_last = ((int) ((count_cont / count) * 100));
                }

            }
        }
        if (nextAuto) {
            new NextStepForAutotracing(cell);
        }

    }

    /**
     * Class for "CreateNewArc", class realises createNewArc Job to avoid
     * "database changes are forbidden" error.
     */
    private static class CreateNewArc extends Job {

        ArcProto ap;
        double size;
        PortInst firstPort;
        PortInst secondPort;

        public CreateNewArc(ArcProto arc, PortInst firstPort, PortInst secondPort, double size) {
            super("Create New Arc", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.ap = arc;
            this.firstPort = firstPort;
            this.secondPort = secondPort;
            this.size = size;
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            EditingPreferences ep = EditingPreferences.getInstance();
            ArcInst newArc = ArcInst.makeInstance(ap, ep, firstPort, secondPort);
            newArc.setLambdaBaseWidth(size);
            return true;
        }
    }

    /**
     * Class for "CreateNewArc", class realises createNewArc Job to avoid
     * "database changes are forbidden" error.
     */
    private static class NextStepForAutotracing extends Job {

        Cell cell;

        public NextStepForAutotracing(Cell cell) {
            super("Next Step For Autotracing", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
            this.cell = cell;
            startJob();
        }

        @Override
        public boolean doIt() throws JobException {
            Iterator<Job> itrJob = Job.getAllJobs();
            while (itrJob.hasNext()) {
                Job job = itrJob.next();
                if (job.toString().contains("Create New Arc")) {
                    while (!job.isFinished()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            new ExportKeys.ExportAfter(cell);
            return true;
        }
    }

}
