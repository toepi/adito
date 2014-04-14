/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.adito.boot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Get the version of Adito in use. If running in a development environment this
 * will be retrieved from the build.properties file, otherwise. the build
 * process should have replaced the static {@link #VERSION} constant with the
 * real version.
 * <p>
 * Also contains a utility class that may be used to by other software
 * components to represent a version.
 *
 * @author Brett Smith <a href="mailto:
 * brett@localhost">&lt;brett@localhost&gt;</a>
 */
public class VersionInfo {

    private static final String DEV_TAG = "SNAPSHOT";
    private static Version version;

    /**
     * Get the current Adito version
     *
     * @return version
     */
    public static Version getVersion() {
        if (version == null) {
            version = new Version(VersionInfo.class.getPackage().getImplementationVersion());
        }
        return version;
    }

    /**
     * Represents the version number of a software component such as Adito
     * itself or perhaps an extension.
     * <p>
     * The object may be constructed from a dotted version string. An optional
     * <i>tag</i> element may also be provided by appending an underscore (-)
     * then the tag.
     * <p>
     * For example
     * <p>
     * <code>0.1.14_alpha</code></p> would give a major version of 0, a minor
     * version of 1, a build version of 14 and a tag of 'alpha'.
     *
     * @author Brett Smith
     */
    public static class Version implements Comparable<Version> {

        private static final int QUALLIFIER_IDX = 7;
        private final int major;
        private final int minor;
        private final int build;
        private final String quallifier;

        public Version(final String versionStr) {
            final Matcher matcher = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})(\\.(\\d{1,3})((-|\\.|_)(.+))?)?$")
                    .matcher(versionStr);
            if (matcher.find()) {
                major = Integer.parseInt(matcher.group(1));
                minor = Integer.parseInt(matcher.group(2));
                if (matcher.group(4) == null) {
                    build = 0;
                } else {
                    build = Integer.parseInt(matcher.group(4));
                }
                if (matcher.group(QUALLIFIER_IDX) == null) {
                    quallifier = null;
                } else {
                    quallifier = matcher.group(QUALLIFIER_IDX);
                }
            } else {
                throw new IllegalArgumentException(
                        String.format("VersionStr '%s' is not a valid version!", versionStr)
                );
            }
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean result;
            if (obj instanceof Version) {
                result = compareTo((Version) obj) == 0;
            } else {
                result = false;
            }
            return result;
        }

        @Override
        public int hashCode() {
            return asInteger();
        }

        /**
         * Compare to versions. Version are equal if the major, minor and build
         * elements are the same. The tag element is not taken into account.
         *
         * @param otherVersion other version
         * @return comparison
         */
        public int compareTo(final Version otherVersion) {
            final int result;
            if (otherVersion == null) {
                throw new NullPointerException("OtherVersion must be not null!");
            } else {
                if (asInteger().compareTo(otherVersion.asInteger()) == 0) {
                    result = compareQuallifier(otherVersion);
                } else {
                    result = asInteger().compareTo(otherVersion.asInteger());
                }

            }
            return result;
        }

        private int compareQuallifier(final Version otherVersion) {
            int result;
            if (sameQuallifier(otherVersion)) {
                result = 0;
            } else if (isDevelopmentVersion() || otherVersion.getQuallifier() == null) {
                result = -1;
            } else if (otherVersion.isDevelopmentVersion() || getQuallifier() == null) {
                result = 1;
            } else {
                result = getQuallifier().compareTo(otherVersion.getQuallifier());
            }
            return result;
        }

        private boolean sameQuallifier(final Version otherVersion) {
            return getQuallifier() == null && otherVersion.getQuallifier() == null
                    || getQuallifier() != null
                    && getQuallifier().equals(otherVersion.getQuallifier());
        }

        private Integer asInteger() {
            return major * 1000000000 + minor * 1000000 + build * 1000;
        }

        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public int getBuild() {
            return build;
        }

        public boolean isDevelopmentVersion() {
            return DEV_TAG.equals(getQuallifier());
        }

        public String getQuallifier() {
            return quallifier;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder().append(major)
                    .append('.').append(minor)
                    .append('.').append(build);
            if (getQuallifier() != null) {
                sb.append('-').append(quallifier);
            }
            return sb.toString();
        }
    }
}
