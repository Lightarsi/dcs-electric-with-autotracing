/* Electric(tm) VLSI Design System
 *
 * File: Scheme.java
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

import java.util.HashMap;
import java.util.Map;

/**
 * Class keeps some information about last autotracing.
 */
public final class Scheme {

    private static Scheme instance;
    private final Map<String, String> elementsMap = new HashMap<>();     // PortInst

    private Scheme() {
    }

    /**
     * Return Singleton instance.
     *
     * @return
     */
    public static synchronized Scheme getInstance() {
        if (instance == null) {
            instance = new Scheme();
        }
        return instance;
    }

    /**
     * Method to add link to Map.
     *
     * @param key
     * @param value
     */
    public void addLink(String key, String value) {
        String cutValue = Accessory.parsePortToBlock(value) + "." + Accessory.parsePortToPort(value);
        elementsMap.put(key, cutValue);
    }

    /**
     * Method to get internal Port name from autotracing scheme.
     *
     * @param key
     * @return
     */
    public String getNetNameFrom(String key) {
        return elementsMap.get(key);
    }

    /**
     * Method to reset Scheme.
     *
     */
    public void resetScheme() {
        elementsMap.clear();
    }

    /**
     * Method to look around all map.
     *
     */
    public void showMap() {
        for (Map.Entry<String, String> entry : elementsMap.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
    }
}
