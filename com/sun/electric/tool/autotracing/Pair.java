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

/**
 *
 * @author diivanov
 * @param <T>
 * @param <V>
 */
public final class Pair<T, V> {

    private T first;
    private V second;

    /**
     *
     * @param first
     * @param second
     */
    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    public T getFirstObject() {
        return first;
    }

    public V getSecondObject() {
        return second;
    }

    public void setFirstObject(T first) {
        this.first = first;
    }

    public void setSecondObject(V second) {
        this.second = second;
    }
    
    // VERY BAD DECISION, EQUALS ONLY FOR FIRST ELEMENT.
    // THIS BAD DECISION NEEDED TO AVOID DOUBLE_READING SPM_KEYS
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        Pair<T, V> pair = (Pair<T, V>) o;
        if (this.getFirstObject().toString().equals(pair.getFirstObject().toString())) {
            return true;
        }
        return false;
    }
    // VERY BAD DECISION, HASHCODE ONLY FOR FIRST ELEMENT.
    // THIS BAD DECISION NEEDED TO AVOID DOUBLE_READING SPM_KEYS
    @Override
    public int hashCode() {
        return first.toString().hashCode();
    }

}
