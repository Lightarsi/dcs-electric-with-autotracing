/* Electric(tm) VLSI Design System
 *
 * File: SimpleAutotracing.java
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

import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.tool.Job;

import com.sun.electric.tool.user.CellChangeJobs;

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class implements simple autotracing system There are some basic
 * elements: input, output_adr, output_ddr, CAU(CAU, CAU_COMP, CAU_POS_FEEDBACk,
 * CAU_NEG_FEEDBACK), PAU(some variants), PPC, SPM, ION.
 */
public class SimpleAutotracing {

    private HashSet<String> usedPortList = new HashSet<>();
    private ArrayDeque<NodeInst> nodeList = new ArrayDeque<>();
    private HashSet<String> usedNodeList = new HashSet<>();
    private NonOrientedGlobalGraph nogg;
    private NonOrientedGlobalGraph nogg2;

    private boolean exitPressed = false;

    private final Object lock = new Object();

    private static Scheme scheme;
    private static AuxilarySimpleAutotracing auxisa;
    private static SimpleAutotracing simpleAutotracing;
    private static final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);

    /**
     *
     * @return
     */
    public static SimpleAutotracing getSimpleAutotracing() {
        if (simpleAutotracing == null) {
            simpleAutotracing = new SimpleAutotracing();
        }
        return simpleAutotracing;
    }

    public void startTrace() {
        simpleAutotracing = getSimpleAutotracing();
        scheme = Scheme.getInstance();
        auxisa = AuxilarySimpleAutotracing.getAuxilaryOnlyObject();
        simpleAutotracing.makeTrace();
        Accessory.timeFinish();
    }

    /**
     * This method is used as initiation for autotracing system, cleaning files
     * and prepare for work, renew all static objects.
     */
    private void makeTrace() {
        exitPressed = false;
        int imax = 45;               // max amount of iterations
        resetStatics();
        nogg = new NonOrientedGlobalGraph("EighteenAugust");

        try {
            initStart(false);
        } catch (StepFailedException e) {
            Accessory.showMessage("Autotracing proccess failed with non-specific reason.");
            return;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            assert false;
        } catch (FunctionalException fe) {
            fe.printStackTrace();
            return;
        }
        resetStatics();
        nogg.applyWeightChanges();
        nogg2 = new NonOrientedGlobalGraph(nogg);

        for (int i = 0; i < imax; i++) {
            if (exitPressed) {
                exitPressed = false;
                return;
            }
            System.out.println("Step " + i);
            boolean withIncrease = false;
            //if(i!=0) {
            nogg = new NonOrientedGlobalGraph(nogg2);
            //}
            if (i % 4 == 0) {
                withIncrease = true;
            }
            try {
                initStart(true);
                //Accessory.showMessage("Autotracing proccess completed.");
                break;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                assert false;
            } catch (StepFailedException e) {
                e.printStackTrace();
                try {
                    oneMoreStep(withIncrease);
                } catch (FunctionalException fe) {
                    fe.printStackTrace();
                    return;
                }
            } catch (FunctionalException fe) {
                fe.printStackTrace();
                return;
            }
            if (i == (imax - 1)) {
                Accessory.showMessage("Autotracing proccess failed.");
                return;
            }
        }

        simulate();
    }

    /**
     * prepare new Iteration with increased path weight.
     */
    private void oneMoreStep(boolean withIncrease) throws FunctionalException {
        resetStatics();
        nogg = new NonOrientedGlobalGraph(nogg2);
        if (withIncrease) {
            try {
                initStart(false);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (StepFailedException e) {
                e.printStackTrace();
                Accessory.printLog("Step failed");
            }
        }
        resetStatics();
        nogg.applyWeightChanges();
        nogg2 = new NonOrientedGlobalGraph(nogg);
    }

    /**
     * Reset all static variables except nogg.
     */
    private void resetStatics() {
        Accessory.cleanFile(Accessory.CONFIG_PATH);
        Accessory.cleanFile(Accessory.CONFIG_WITHOUT_MODELLING_PATH);
        nodeList = new ArrayDeque<>();
        usedPortList = new HashSet<>();
        usedNodeList = new HashSet<>();
        auxisa.resetAuxilary();

    }

    /**
     * Initiation method, get starting point, set as used and start tracing
     * method.
     */
    private void initStart(boolean doDelete) throws IOException, StepFailedException, FunctionalException {
        scheme.resetScheme();
        prepareSPMBlocks(nogg, doDelete);
        NodeInst[] startNi = Accessory.getStartingNodeInst();
        for (NodeInst ni : startNi) {
            if (!usedNodeList.contains(ni.toString())) {
                PortInst firstPort = ni.getPortInst(0);             // Wow, 0 is luck (input has 2 ports: input* and source)
                setPortAsUsed(firstPort);
                traceFromStartToEnd(firstPort, doDelete);
            }
        }
    }

    /**
     * This method is used as first method for autotracing system, working in
     * autotracing jelib get all ports that are connected to starting port
     * (typically PADDR.PX1), set all used port as used to avoid double-running,
     * 1 combination startPort -> PortInst forms configuration path and adds it
     * to files, delete all these pathes from current object sphere, iteratively
     * starting second method (for all ports and all nodeInsts).
     */
    private void traceFromStartToEnd(PortInst startPort, boolean doDelete) throws IOException, StepFailedException, FunctionalException {
        PortInst[] piArray = Accessory.getNearByPortInsts(startPort, usedPortList);
        for (PortInst pi : piArray) {
            setPortAsUsed(pi);
        }
        String name = auxisa.dealWithBlock(startPort.getNodeInst(), null);
        int firstChain = nogg.findStartingPoint(name);
        String firstChainS = nogg.findStartingPointName(name);
        scheme.addLink(startPort.toString(), firstChainS);
        DeleteChainsFromBlock(nogg, firstChainS);
        addKeyFromMap(firstChainS);
        auxisa.addKey(firstChainS, 8);
        Accessory.printLog(firstChainS);

        if (firstChain == -1) {
            throw new StepFailedException("firstChain");
        }

        for (PortInst pi : piArray) {
            String secondPort = auxisa.dealWithBlock(pi.getNodeInst(), pi);
            nodeList.add(pi.getNodeInst());
            usedNodeList.add(pi.getNodeInst().toString());

            String nextBlock = nogg.deikstra(firstChain, secondPort, null, doDelete, true).getFirstObject(); // woops, should be tripletraced for all ports.
            Accessory.printLog("nextBlock " + nextBlock);
            /* Maybe should be changed */
            if (nextBlock.contains("SPM<")) {  // SPM<, are u sure with SPM1-9?
                auxisa.addSPMBlock(nextBlock);
            }

            scheme.addLink(pi.toString(), nextBlock);

            Accessory.printLog("nextBlock " + nextBlock);
            DeleteChainsFromBlock(nogg, nextBlock);
            addKeyFromMap(nextBlock);
            addNextNode(pi, nextBlock);
        }
        if (doDelete) {
            nogg.doDelete();
        }
        NodeInst ni;
        while ((ni = nodeList.pollFirst()) != null) {
            if (ni.toString().contains("INPUT")) {
                nogg.setBlockAsUsed(auxisa.getParameter(ni.toString()));
                continue;
            }
            Iterator<PortInst> itr2 = ni.getPortInsts();
            PortInst pi;
            while (itr2.hasNext()) {
                pi = itr2.next();
                Accessory.printLog(pi.toString());
                traceFromStartToEnd(pi, auxisa.getParameter(ni.toString()), doDelete);
            }
            //nodeList.remove(ni);
            nogg.setBlockAsUsed(auxisa.getParameter(ni.toString()));
        }
    }

    /**
     * Second method of autotracing system, return if port is used to avoid
     * double-running, get all portInsts in network with current portInst, make
     * trace from name(that is formed from objectName) to existing portInst and
     * add this portInst to array, delete all traces from current object sphere.
     */
    private void traceFromStartToEnd(PortInst startPort, String param, boolean doDelete) throws IOException, StepFailedException, FunctionalException {
        if (isPortUsed(startPort)) {
            return;
        } else {
            setPortAsUsed(startPort);
        }

        PortInst[] piArray = Accessory.getNearByPortInsts(startPort, usedPortList);
        if (piArray.length == 0) {
            return;
        }
        for (PortInst pi : piArray) {
            setPortAsUsed(pi);
        }

        String name = auxisa.dealWithBlock(startPort.getNodeInst(), startPort);

        Accessory.printLog("name " + name);
        int firstChain = nogg.findStartingPoint(name);
        ArrayList<Integer> firstChainsList = nogg.findStartingPointAsList(name);
        if (firstChain == -1) {
            for (Integer z : firstChainsList) {
                if (z != -1) {
                    firstChain = z;
                }
                break;
            }
        }

        if (firstChain == -1) {
            for (int j = 0; j < 5; j++) {
                PortInst pi = piArray[0];
                nogg2.deikstra(name, auxisa.dealWithBlock(pi.getNodeInst(), pi), param, false, false);
            }
            throw new StepFailedException("SecondChain");
        }
        String firstChainS = nogg.getNameFromPoint(firstChain, name);
        assert firstChainS != null;

        for (PortInst pi : piArray) {
            System.out.println("piArray " + pi.toString());
            if (pi.toString().contains("VSS")) {
                if (isPortUsed(pi)) {
                    //continue;
                }
            }
            if (!usedNodeList.contains(pi.getNodeInst().toString())) {
                if (pi.getNodeInst().toString().contains("SPM") || pi.getNodeInst().toString().contains("PPC")) {
                    nodeList.addFirst(pi.getNodeInst());
                    usedNodeList.add(pi.getNodeInst().toString());
                } else {
                    nodeList.add(pi.getNodeInst());
                    usedNodeList.add(pi.getNodeInst().toString());
                }
            }
            String secondPort = auxisa.dealWithBlock(pi.getNodeInst(), pi);
            String nextBlock;

            Accessory.timeStart("s");
            try {
                firstChain = deikstraMultiThreads(firstChainsList, pi, secondPort, param);
            } catch (ExecutionException | InterruptedException ee) {
                ee.printStackTrace();
                assert false;
            }

            Accessory.timeStart("s");

            firstChainS = nogg.getNameFromPoint(firstChain, name);
            if (firstChainS == null) {
                throw new StepFailedException("So sad. Can't do autotracing through this way.");
            }

            scheme.addLink(startPort.toString(), firstChainS);

            Accessory.printLog("firstChainS " + firstChainS);
            Accessory.printLog("secondPort " + secondPort);

            nogg.resetLists();
            nextBlock = nogg.deikstra(firstChain, secondPort, param, doDelete, true, true).getFirstObject(); // param may be null

            if (nextBlock == null) {
                Accessory.printLog("Increase weight for last net");
                for (int j = 0; j < 5; j++) {
                    nogg2.deikstra(firstChain, secondPort, param, false, false);
                }
                throw new StepFailedException("nextBlock");
            }

            if (nextBlock.contains("SPM<")) {
                auxisa.addSPMBlock(nextBlock);
            }
            addKeyFromMap(firstChainS);

            scheme.addLink(pi.toString(), nextBlock);
            Accessory.printLog("nextBlock " + nextBlock);
            DeleteChainsFromBlock(nogg, nextBlock);
            addKeyFromMap(nextBlock);
            addNextNode(pi, nextBlock);

        }
        if (doDelete) {
            Accessory.printLog("!");
            nogg.doDelete();
        }
    }

    /**
     * Deikstra method was paralleled by 3 threads (max 8 in SPM).
     */
    private int deikstraMultiThreads(ArrayList<Integer> firstChainsList, PortInst pi, String secondPort, String param) throws ExecutionException, InterruptedException, StepFailedException {
        final AtomicInteger pathLength = new AtomicInteger(100000);
        final AtomicInteger firstChain = new AtomicInteger(-1);
        List<Future> futures = new ArrayList<>();
        HashMap<Integer, Pair<String, Integer>> map = new HashMap<>();

        for (int firstC : firstChainsList) {
            futures.add(service.submit(new Runnable() {
                public void run() {
                    NonOrientedGlobalGraph noggX = new NonOrientedGlobalGraph(nogg);
                    Pair<String, Integer> pair = noggX.deikstra(firstC, secondPort, param, false, false);
                    synchronized (lock) {
                        map.put(firstC, pair);
                    }
                }
            }));
        }

        for (Future f : futures) {
            f.get();
        }

        for (Map.Entry<Integer, Pair<String, Integer>> entry : map.entrySet()) {
            Pair<String, Integer> pair = entry.getValue();
            Integer firstC = entry.getKey();
            int pathL = pair.getSecondObject();
            if (pathL == -1) {
                continue;
            }
            if (pathLength.get() > pathL) {
                //if (auxisa.checkBlockForExistingOutput(pi, nogg, secondPort)) {
                pathLength.set(pathL);
                Accessory.printLog(String.valueOf(pathLength));
                firstChain.set(firstC);
                //}
            }
        }

        if (pathLength.get() == 100000) {
            throw new StepFailedException("So sad. Can't do autotracing through this way.");
        }
        return firstChain.get();
    }

    /**
     * Method to get array of INPUT-like chain (PADDR.PX1-6).
     *
     * @return
     */
    public static void prepareSPMBlocks(NonOrientedGlobalGraph nogg, boolean doDelete) {
        ArrayDeque<String> firstFiveSPMs = new ArrayDeque<>();
        firstFiveSPMs.add("SPM<2466.Y6");
        firstFiveSPMs.add("SPM<8502.Y6");
        firstFiveSPMs.add("SPM<14538.Y6");
        firstFiveSPMs.add("SPM<20574.Y6");
        firstFiveSPMs.add("SPM<26610.Y6");
        // .Y6 coz setParameter requires nextBlock variable which contains port
        Cell curcell = Job.getUserInterface().getCurrentCell();
        Iterator<NodeInst> itr = curcell.getNodes();
        ArrayList<NodeInst> spmList = new ArrayList<>();
        while (itr.hasNext()) {
            NodeInst ni = itr.next();
            if (ni.toString().contains("SPM")) {
                spmList.add(ni);
            }
        }
        for (NodeInst spm : spmList) {
            if (firstFiveSPMs.isEmpty()) {
                break;
            }
            ArrayList<String> portList = new ArrayList<>();

            if (firstFiveSPMs.isEmpty()) {
                break;
            }
            String parameter = firstFiveSPMs.pollFirst();
            auxisa.setParameter(spm, parameter);

            Iterator<PortInst> piItr = spm.getPortInsts();
            while (piItr.hasNext()) {
                PortInst nextPi = piItr.next();
                if (nextPi.hasConnections()) {
                    String piStr = nextPi.toString();
                    String portName = piStr.substring(piStr.indexOf(".") + 1, piStr.lastIndexOf("'"));
                    portList.add(portName);
                }
            }
            if (doDelete) {
                for (String port : portList) {
                    String newParam = parameter.substring(0,parameter.indexOf("."));
                    Accessory.printLog(newParam + "." + port);
                    nogg.affectVertex(nogg.findStartingPoint(newParam+"."+port)); // affect all connected ports
                }
            }
        }
    }

    /**
     * Method to use getKeyFromMap method without exceptions.
     */
    private void addKeyFromMap(String nextBlock) throws IOException {
        if (!nextBlock.contains("<")) { // don't play with non-sf-blocks (non-extist < in name)
            return;
        }
        getKeyFromMap(nextBlock, nextBlock.substring(0, nextBlock.indexOf("<")));
    }

    /**
     * Method to get key which is connected to input/output of current block
     * using Map from file, e.g. PADDR.PX1 -> n5-n6 -> key(2).
     */
    private String getKeyFromMap(String fullBlock, String blockName) throws IOException {
        String path = Accessory.PATH + "/autotracing/";
        path += blockName;
        // SPM shouldn't add internal keys, avoid it
        if (blockName.equals("SPM")) {
            return null;
        }
        path += ".trc";
        String portName = Accessory.parsePortToPort(fullBlock);
        File mapFile = new File(path);
        try (BufferedReader MapReader = new BufferedReader(new FileReader(mapFile))) {
            String line;
            while ((line = MapReader.readLine()) != null) {
                String[] split = line.split(" -- ");
                if (split[0].equals(portName)) {
                    addKey(fullBlock, split[1]);
                }
            }
        }
        // block is under construction
        return null;
    }

    /**
     * Variation of addKey(string, int) with (string, string) parameters.
     */
    private void addKey(String ni, String number) {
        String absNum = ni.substring(ni.indexOf("<") + 1, ni.lastIndexOf("."));
        int resultKey = Integer.valueOf(absNum) + Integer.valueOf(number);
        Accessory.write(Accessory.CONFIG_PATH, (String.valueOf(resultKey)));
    }

    /**
     * Method to delete used block from global block sphere.
     */
    private void DeleteChainsFromBlock(NonOrientedGlobalGraph nogg, String nextBlock) {
        if (nextBlock.contains("PPC")) {
            nogg.setPPCPartAsUsed(Accessory.parsePortToBlock(nextBlock), Accessory.parsePortToPort(nextBlock));
        } else {
            nogg.setBlockAsUsed(Accessory.parsePortToBlock(nextBlock));
        }
    }

    /**
     * Method to deal with CAP in setKeys method, caps should be handled in
     * simple autotracing because there is influence on used elements lists.
     *
     * @param nextBlock
     * @param ni
     */
    public void handleAuxilaryCapKeys(String nextBlock, NodeInst ni) {
        if (nextBlock.contains(".PX") || nextBlock.contains(".PZ")) {
            Iterator<PortInst> itrCap = ni.getPortInsts();
            while (itrCap.hasNext()) {
                PortInst secondPi = itrCap.next();
                if (!isPortUsed(secondPi)) {
                    PortInst[] piArray = Accessory.getNearByPortInsts(secondPi, usedPortList);
                    for (PortInst newPi : piArray) {
                        if (newPi.toString().contains("VSS")) {
                            String parameter = auxisa.getParameter(ni.toString());
                            String index = parameter.substring(parameter.length() - 2, parameter.length());
                            switch (index) {
                                case "PX":
                                    auxisa.addKey(nextBlock, 12);
                                    usedNodeList.add(newPi.getNodeInst().toString());
                                    setPortAsUsed(newPi);
                                    setPortAsUsed(secondPi);
                                    break;

                                case "PZ":
                                    auxisa.addKey(nextBlock, 35);
                                    usedNodeList.add(newPi.getNodeInst().toString());
                                    setPortAsUsed(newPi);
                                    setPortAsUsed(secondPi);
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method to commit autotracing step results, setting Parameter of NodeInst
     * when you know where trace came.
     */
    private void addNextNode(PortInst pi, String nextBlock) throws FunctionalException {
        NodeInst ni = pi.getNodeInst();
        auxisa.setParameter(ni, nextBlock);
        auxisa.setKeys(ni, nextBlock);
    }

    /**
     * Set port as used to avoid double-running.
     *
     * @param pi
     */
    public void setPortAsUsed(PortInst pi) {
        usedPortList.add(pi.toString());
    }

    /**
     * Check if it port was used to avoid double-running.
     *
     * @param pi
     * @return
     */
    public boolean isPortUsed(PortInst pi) {
        return usedPortList.contains(pi.toString());
    }

    private void simulate() {
        Cell basicCell = Automodelling.getCellFromName(Automodelling.CELL_NAME);
        Cell cellToDelete = Automodelling.getCellFromName(Automodelling.AUTOTRACING_CELL_NAME);
        if (cellToDelete != null) {
            cellToDelete.kill();
        }
        assert basicCell != null;

        new CellChangeJobs.DuplicateCell(basicCell, Automodelling.AUTOTRACING_CELL_NAME, basicCell.getLibrary(), false, false, null);

        Automodelling.writeSPMkeys();
        Automodelling.modelScheme();
    }

    public void setExitPressed() {
        exitPressed = true;
    }

}
