/* Electric(tm) VLSI Design System
 *
 * File: BinaryHeap.java
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

import java.util.ArrayList;

/**
 * This class implements typical BinaryHeap functionality and is used as is.
 * Value sorting was added as extension to basic functionality.
 */
public class BinaryHeapNew {

    private ArrayList<Pair<Integer, Integer>> pairList;

    /**
     *
     */
    public BinaryHeapNew() {
        pairList = new ArrayList<>();
    }

    /**
     * Method to get full size of heap
     */
    private int getKeyHeapSize() {
        return this.pairList.size();
    }

    /**
     * Method to add element to heap, Method is using key-value pair as element.
     *
     * @param value
     * @param key
     */
    public void add(int value, int key) {
        boolean exist = false;
        Pair<Integer, Integer> existingPair = null;
        for (Pair<Integer, Integer> pair : pairList) {
            if (pair.getFirstObject().equals(key)) {
                exist = true;
                existingPair = pair;
            }
        }
        if (exist) {
            int oldVal = existingPair.getSecondObject();
            if (value < oldVal) {
                existingPair.setSecondObject(value);
                heapifyUp(pairList.indexOf(existingPair));
            }
        } else {
            Pair<Integer, Integer> pair = new Pair<>(key, value);
            pairList.add(pair);

            int i = getKeyHeapSize() - 1;
            int parent = (i - 1) / 2;

            while (i > 0 && pairList.get(parent).getSecondObject() > pairList.get(i).getSecondObject()) {
                Pair<Integer, Integer> tempPair = pairList.get(i);
                pairList.set(i, pairList.get(parent));
                pairList.set(parent, tempPair);

                i = parent;
                parent = (i - 1) / 2;
                
            }
        }
    }

    private void heapifyUp(int i) {
        if (i == 0) {
            return;
        }
        int parent = (i - 1) / 2;

        while (i > 0 && pairList.get(parent).getSecondObject() > pairList.get(i).getSecondObject()) {
            Pair<Integer, Integer> tempPair = pairList.get(i);
            pairList.set(i, pairList.get(parent));
            pairList.set(parent, tempPair);

            i = parent;
            parent = (i - 1) / 2;
        }
    }

    /**
     * Typical heapify with key comparator.
     */
    private void heapifyWithKey(int i) {
        int leftChild;
        int rightChild;
        int largestChild;

        for (;;) {
            leftChild = 2 * i + 1;
            rightChild = 2 * i + 2;
            largestChild = i;

            if (leftChild < getKeyHeapSize() && pairList.get(leftChild).getSecondObject() < pairList.get(largestChild).getSecondObject()) {
                largestChild = leftChild;
            }

            if (rightChild < getKeyHeapSize() && pairList.get(rightChild).getSecondObject() < pairList.get(largestChild).getSecondObject()) {
                largestChild = rightChild;
            }

            if (largestChild == i) {
                break;
            }
            
            Pair<Integer, Integer> tempPair = pairList.get(i);
            pairList.set(i, pairList.get(largestChild));
            pairList.set(largestChild, tempPair);

            i = largestChild;
        }
    }

    /**
     * Method to get minimum key of elements.
     *
     * @return
     */
    public int getMinKey() {
        if (getKeyHeapSize() == 0) {
            return -1;
        }
        
        int result = pairList.get(0).getFirstObject();
        pairList.set(0, pairList.get(getKeyHeapSize() - 1));
        pairList.remove(getKeyHeapSize() - 1);
        
        heapifyWithKey(0);
        return result;
    }

}
