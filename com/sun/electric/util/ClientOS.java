/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: ClientOS.java
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
package com.sun.electric.util;

public class ClientOS {

    /**
     * OS is a typesafe enum class that describes the current operating system.
     */
    public enum OS {

        /**
         * Describes Windows.
         */
        WINDOWS("Windows"),
        /**
         * Describes UNIX/Linux.
         */
        UNIX("UNIX"),
        /**
         * Describes Macintosh.
         */
        MACINTOSH("Macintosh");
        private String name;

        private OS(String name) {
            this.name = name;
        }

        /**
         * Returns a printable version of this OS.
         *
         * @return a printable version of this OS.
         */
        public String toString() {
            return name;
        }
    }
    /**
     * The current operating system.
     */
    public static final OS os = OSInitialize();

    // return OS prefix
    public static String getOSPrefix() {
        String osPrefix = "";
        if (os == OS.WINDOWS) {
            osPrefix = "Win";
        } else if (os == OS.MACINTOSH) {
            osPrefix = "Mac";
        } else if (os == OS.UNIX) {
            osPrefix = "Linux";
        }
        return osPrefix;
    }

    public static String userDir() {
        return ClientOS.isOSMac() ? System.getProperty("user.home") : System.getProperty("user.dir");
    }

    private static OS OSInitialize() {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.startsWith("windows")) {
                return OS.WINDOWS;

            } else if (osName.startsWith("linux")
                    || osName.startsWith("solaris") || osName.startsWith("sunos")) {
                return OS.UNIX;
            } else if (osName.startsWith("mac")) {
                return OS.MACINTOSH;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("No OS detected");
        return null;
    }

    public static boolean isOSWindows() {
        return os == OS.WINDOWS;
    }

    public static boolean isOSMac() {
        return os == OS.MACINTOSH;
    }

    public static boolean isOSLinux() {
        return os == OS.UNIX;
    }
}
