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

import org.junit.Assert;
import org.junit.Test;

public class VersionInfoTest {
    
    @Test
    public void majorFor_0_9_0_ShouldReturn0() throws Exception {
        Assert.assertEquals(0, new VersionInfo.Version("0.9.0").getMajor());
    }
    
    @Test
    public void majorFor_1_0_0_ShouldReturn1() throws Exception {
        Assert.assertEquals(1, new VersionInfo.Version("1.0.0").getMajor());
    }
    
    @Test
    public void minorFor_0_9_0_ShouldReturn9() throws Exception {
        Assert.assertEquals(9, new VersionInfo.Version("0.9.0").getMinor());
    }
    
    @Test
    public void minorFor_1_10_0_ShouldReturn10() throws Exception {
        Assert.assertEquals(10, new VersionInfo.Version("1.10.0").getMinor());
    }
    
    @Test
    public void buildFor_0_9_0_ShouldReturn0() throws Exception {
        Assert.assertEquals(0, new VersionInfo.Version("0.9.0").getBuild());
    }
    
    @Test
    public void buildFor_0_9_2_ShouldReturn2() throws Exception {
        Assert.assertEquals(2, new VersionInfo.Version("0.9.2").getBuild());
    }
    
    @Test
    public void tagFor_0_9_0_ShouldReturnNull() throws Exception {
        Assert.assertNull(new VersionInfo.Version("0.9.0").getQuallifier());
    }
    
    @Test
    public void tagFor_0_9_0_RC1ShouldReturnRC1() throws Exception {
        Assert.assertEquals("RC1", new VersionInfo.Version("0.9.0.RC1").getQuallifier());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createVersionWithIllegalVersionStrShouldThrowIllegalArgumentException() throws Exception {
        new VersionInfo.Version("0.0.9999");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createVersionWithIllegalDeveloperVersionStrShouldThrowIllegalArgumentException() throws Exception {
        new VersionInfo.Version("0.0.9999-SNAPSHOT");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createVersionWithIllegalTaggedVersionStrShouldThrowIllegalArgumentException() throws Exception {
        new VersionInfo.Version("0.0.999RC");
    }
    
    @Test
    public void toStringWithTag() throws Exception {
        final String versionStr = "1.0.0-SNAPSHOT";
        Assert.assertEquals(versionStr, new VersionInfo.Version(versionStr).toString());
    }
    
    @Test
    public void toStringWithoutTag() throws Exception {
        final String versionStr = "1.0.0";
        Assert.assertEquals(versionStr, new VersionInfo.Version(versionStr).toString());
    }
    
    @Test
    public void compareSameVersion() throws Exception {
        final String versionStr = "1.0.0";
        Assert.assertEquals(0, new VersionInfo.Version(versionStr).compareTo(new VersionInfo.Version(versionStr)));
    }
    
    @Test
    public void compareReleaseWithDeveloment() throws Exception {
        final String versionStr = "1.0.0";
        final VersionInfo.Version release = new VersionInfo.Version(versionStr);
        final VersionInfo.Version development = createAsDevelopment(versionStr);
        Assert.assertEquals(1, release.compareTo(development));
        Assert.assertEquals(-1, development.compareTo(release));
    }
    
    @Test
    public void compareReleaseWithTag() throws Exception {
        final String versionStr = "1.0.0";
        final VersionInfo.Version release = new VersionInfo.Version(versionStr);
        final VersionInfo.Version candidate = new VersionInfo.Version(versionStr.concat(".RC1"));
        Assert.assertEquals(1, release.compareTo(candidate));
        Assert.assertEquals(-1, candidate.compareTo(release));
    }
    
    @Test
    public void compareSnaphotWithTag() throws Exception {
        final String versionStr = "1.0.0";
        final VersionInfo.Version candidate = new VersionInfo.Version(versionStr);
        final VersionInfo.Version snaphot = createAsDevelopment(versionStr);
        Assert.assertEquals(1, candidate.compareTo(snaphot));
        Assert.assertEquals(-1, snaphot.compareTo(candidate));
    }
    
    @Test
    public void compareReleaseWithNextDevelopment() throws Exception {
        final VersionInfo.Version release = new VersionInfo.Version("0.9.0");
        final VersionInfo.Version development = createAsDevelopment("0.9.1");
        Assert.assertEquals(-1, release.compareTo(development));
        Assert.assertEquals(1, development.compareTo(release));
    }
    
    @Test
    public void compareReleaseWithNextBuild() throws Exception {
        final VersionInfo.Version release = new VersionInfo.Version("0.9.0");
        final VersionInfo.Version next = createAsDevelopment("0.9.1");
        Assert.assertEquals(-1, release.compareTo(next));
        Assert.assertEquals(1, next.compareTo(release));
    }
    
    @Test
    public void compareReleaseWithNextMinor() throws Exception {
        final VersionInfo.Version release = new VersionInfo.Version("0.9.0");
        final VersionInfo.Version next = createAsDevelopment("0.10.0");
        Assert.assertEquals(-1, release.compareTo(next));
        Assert.assertEquals(1, next.compareTo(release));
    }
    
    @Test
    public void compareReleaseWithNextMajor() throws Exception {
        final VersionInfo.Version release = new VersionInfo.Version("0.9.0");
        final VersionInfo.Version next = createAsDevelopment("1.0.0");
        Assert.assertEquals(-1, release.compareTo(next));
        Assert.assertEquals(1, next.compareTo(release));
    }
    
    @Test
    public void compareToCandidates() throws Exception {
        final VersionInfo.Version candidate = new VersionInfo.Version("0.9.0.RC1");
        final VersionInfo.Version next = new VersionInfo.Version("0.9.0.RC2");
        Assert.assertEquals(-1, candidate.compareTo(next));
        Assert.assertEquals(1, next.compareTo(candidate));
    }
    
    @Test
    public void compareToCandidateAndSnapshot() throws Exception {
        final VersionInfo.Version candidate = new VersionInfo.Version("0.9.0.RC1");
        final VersionInfo.Version next = createAsDevelopment("0.9.0");
        Assert.assertEquals(1, candidate.compareTo(next));
        Assert.assertEquals(-1, next.compareTo(candidate));
    }
    
    @Test
    public void compareToDevelopments() throws Exception {
        Assert.assertEquals(0, createAsDevelopment("0.9.0").compareTo(createAsDevelopment("0.9.0")));
    }
    
    @Test
    public void equalsForTwoReleases() throws Exception {
        Assert.assertEquals(new VersionInfo.Version("0.9.0"), new VersionInfo.Version("0.9.0"));
    }
    
    @Test
    public void equalsForDifferentReleases() throws Exception {
        Assert.assertNotEquals(new VersionInfo.Version("0.9.0"), new VersionInfo.Version("0.9.1"));
    }
    
    @Test
    public void equalsForReleasesAndDevelopment() throws Exception {
        Assert.assertNotEquals(new VersionInfo.Version("0.9.0"), createAsDevelopment("0.9.0"));
    }
    
    @Test
    public void equalsForTwoDevelopments() throws Exception {
        Assert.assertEquals(createAsDevelopment("0.9.0"), createAsDevelopment("0.9.0"));
    }

    private static VersionInfo.Version createAsDevelopment(final String versionStr) {
        return new VersionInfo.Version(versionStr.concat("-SNAPSHOT"));
    }
    
    @Test
    public void isDevelopmentVersionForSnapshotShouldReturnTrue() throws Exception {
        Assert.assertTrue(new VersionInfo.Version("0.9.0-SNAPSHOT").isDevelopmentVersion());
    }

    @Test
    public void isDevelopmentVersionForNonSnapshotShouldReturnFalse() throws Exception {
        Assert.assertFalse(new VersionInfo.Version("0.9.0.RC1").isDevelopmentVersion());
    }
    
    @Test
    public void testCreationWithMajorAndMinorOnly() throws Exception {
        final VersionInfo.Version version = new VersionInfo.Version("0.9");
        Assert.assertEquals(0, version.getMajor());
        Assert.assertEquals(9, version.getMinor());
        Assert.assertEquals(0, version.getBuild());
        Assert.assertEquals("0.9.0", version.toString());
    }
    
    @Test
    public void testQuallifierWithUnderline() throws Exception {
        Assert.assertEquals("alpha", new VersionInfo.Version("0.9.0_alpha").getQuallifier());
    }
    
    @Test
    public void testQuallifierMinus() throws Exception {
        Assert.assertEquals("alpha", new VersionInfo.Version("0.9.0-alpha").getQuallifier());
    }
}
