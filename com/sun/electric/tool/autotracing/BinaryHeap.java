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
public class BinaryHeap {

    private ArrayList<Integer> list;
    private ArrayList<Integer> keyList;

    /**
     *
     */
    public BinaryHeap() {
        list = new ArrayList<>();
        keyList = new ArrayList<>();
    }

    /**
     * Method to get full size of heap
     */
    private int getHeapSize() {
        return this.list.size();
    }

    /**
     * Method to get full size of heap
     */
    private int getKeyHeapSize() {
        return this.keyList.size();
    }

    /**
     * Method to add element to heap, Method is using key-value pair as element.
     *
     * @param value
     * @param key
     */
    public void add(int value, int key) {
        if (keyList.contains(key)) {
            //heapifyWithKey(keyList.indexOf(key));
            int index = keyList.indexOf(key);
            int oldVal = list.get(index);
            if (value < oldVal) {
                list.set(index, value);
                heapifyUp(index);
            }
        } else {
            list.add(value);
            keyList.add(key);
            int i = getHeapSize() - 1;
            int parent = (i - 1) / 2;

            while (i > 0 && list.get(parent) > list.get(i)) {
                int temp = list.get(i);
                list.set(i, list.get(parent));
                list.set(parent, temp);

                int tempKeys = keyList.get(i);
                keyList.set(i, keyList.get(parent));
                keyList.set(parent, tempKeys);

                i = parent;
                parent = (i - 1) / 2;
            }
        }
    }

    private void heapifyUp(int i) {
        if(i==0) {
            return;
        }
        int parent = (i - 1) / 2;

        while (i > 0 && list.get(parent) > list.get(i)) {
            int temp = list.get(i);
            list.set(i, list.get(parent));
            list.set(parent, temp);

            int tempKeys = keyList.get(i);
            keyList.set(i, keyList.get(parent));
            keyList.set(parent, tempKeys);

            i = parent;
            parent = (i - 1) / 2;
        }
    }

    /**
     * Method to add element to heap, Method is using value as element.
     *
     * @param value
     */
    public void add(int value) {
        list.add(value);
        int i = getHeapSize() - 1;
        int parent = (i - 1) / 2;

        while (i > 0 && list.get(parent) > list.get(i)) {
            int temp = list.get(i);
            list.set(i, list.get(parent));
            list.set(parent, temp);

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

            if (leftChild < getHeapSize() && list.get(leftChild) < list.get(largestChild)) {
                largestChild = leftChild;
            }

            if (rightChild < getHeapSize() && list.get(rightChild) < list.get(largestChild)) {
                largestChild = rightChild;
            }

            if (largestChild == i) {
                break;
            }

            int temp = list.get(i);
            list.set(i, list.get(largestChild));
            list.set(largestChild, temp);

            int tempKeys = keyList.get(i);
            keyList.set(i, keyList.get(largestChild));
            keyList.set(largestChild, tempKeys);

            i = largestChild;
        }
    }

    /**
     * Typical heapify.
     */
    private void heapify(int i) {
        int leftChild;
        int rightChild;
        int largestChild;

        for (;;) {
            leftChild = 2 * i + 1;
            rightChild = 2 * i + 2;
            largestChild = i;

            if (leftChild < getHeapSize() && list.get(leftChild) < list.get(largestChild)) {
                largestChild = leftChild;
            }

            if (rightChild < getHeapSize() && list.get(rightChild) < list.get(largestChild)) {
                largestChild = rightChild;
            }

            if (largestChild == i) {
                break;
            }

            int temp = list.get(i);
            list.set(i, list.get(largestChild));
            list.set(largestChild, temp);

            i = largestChild;
        }
    }

    /**
     * Method to create heap from array of elements.
     */
    private void buildHeap(int[] sourceArray) {
        list = new ArrayList<>();
        for (int i = 0; i < sourceArray.length; i++) {
            list.add(sourceArray[i]);
        }
        for (int i = getHeapSize() / 2; i >= 0; i--) {
            heapify(i);
        }
    }

    /**
     * Method to get minimum value of elements.
     *
     * @return
     */
    public int getMin() {
        if (getHeapSize() == 0) {
            return -1;
        }
        int result = list.get(0);
        list.set(0, list.get(getHeapSize() - 1));
        list.remove(getHeapSize() - 1);
        if (getHeapSize() > 0) {
            heapify(0);
        }
        return result;
    }

    /**
     * Method to get minimum key of elements.
     *
     * @return
     */
    public int getMinKey() {
        if (getHeapSize() == 0) {
            return -1;
        }
        int result = keyList.get(0);
        list.set(0, list.get(getHeapSize() - 1));
        keyList.set(0, keyList.get(getHeapSize() - 1));
        list.remove(getHeapSize() - 1);
        keyList.remove(getKeyHeapSize() - 1);
        if (getHeapSize() > 0) {
            heapifyWithKey(0);
        }
        return result;
    }

}
