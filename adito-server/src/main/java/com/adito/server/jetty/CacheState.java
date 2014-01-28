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
package com.adito.server.jetty;

import org.mortbay.util.Resource;

public class CacheState {

    public final static int MISSING = 1;
    public final static int FOUND = 2;

    int state;
    String path;
    Resource resource;

    public CacheState(int state, String path, Resource resource) {
        super();
        this.state = state;
        this.path = path;
        this.resource = resource;
    }

    public String getPath() {
        return path;
    }

    public Resource getResource() {
        return resource;
    }

    public int getState() {
        return state;
    }
}
