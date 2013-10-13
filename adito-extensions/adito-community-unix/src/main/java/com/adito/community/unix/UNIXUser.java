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
package com.adito.community.unix;

import java.util.Date;

import com.adito.realms.Realm;
import com.adito.security.DefaultUser;
import com.adito.security.Role;

/**
 * Implementation of a {@link com.adito.security.DefaultUser} for <i>Unix
 * users</i>.
 */
public class UNIXUser extends DefaultUser {

    private char[] password;
    private String home;
    private String shell;
    private int uid;
    private int gid;

    public UNIXUser(String username, String email, char[] password, int uid, int gid, String fullname, String home, String shell, Role[] roles, Realm realm) {
        super(username, email, fullname, new Date(), realm);
        setRoles(roles);
        this.uid = uid;
        this.password = password;
        this.gid = gid;
        this.home = home;
        this.shell = shell;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getShell() {
        return shell;
    }

    public void setShell(String shell) {
        this.shell = shell;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
