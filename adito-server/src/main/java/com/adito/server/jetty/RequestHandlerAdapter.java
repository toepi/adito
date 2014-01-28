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

import com.adito.boot.RequestHandler;
import com.adito.boot.RequestHandlerException;
import com.adito.boot.SystemProperties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.handler.AbstractHttpHandler;

/**
 * Implementation of Jetty's
 * {@link org.mortbay.http.handler.AbstractHttpHandler} that adapts requests to
 * Adito's own registered {@link com.adito.boot.RequestHandler} implementations.
 *
 * @see com.adito.boot.RequestHandler
 */
public class RequestHandlerAdapter extends AbstractHttpHandler {

    private static final long serialVersionUID = 4682392114545977296L;
    private static final Log LOG = LogFactory.getLog(RequestHandlerAdapter.class);
    private final Collection<RequestHandler> requestHandlers = new ArrayList<RequestHandler>();

    /**
     * <p>
     * Add a new {@link RequestHandler}. Every time a request is received that
     * is not serviced by the main webapp, each registered handler will be
     * invoked until one deals with the request.
     *
     * <p>
     * This shouldn't be called directly, but through
     * {@link CustomHttpContext#registerRequestHandler(RequestHandler)}
     *
     * @param requestHandler handler to add
     */
    public void registerRequestHandler(RequestHandler requestHandler) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Registering request handler " + requestHandler.getClass().getName());
        }
        requestHandlers.add(requestHandler);
    }

    /**
     * <p>
     * Remove a {@link RequestHandler} so that is no longer received unhandled
     * requests. See {@link #registerRequestHandler(RequestHandler)}.
     *
     * <p>
     * This shouldn't be called directly, but through
     * {@link CustomHttpContext#deregisterRequestHandler(RequestHandler)}
     *
     * @param requestHandler handler to remove
     */
    public void deregisterRequestHandler(RequestHandler requestHandler) {
        if (LOG.isInfoEnabled()) {
            LOG.info("De-registering request handler " + requestHandler.getClass().getName());
        }
        requestHandlers.remove(requestHandler);
    }

    public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException,
            IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request for " + pathInContext);
        }
        for (RequestHandler handler : requestHandlers) {
            try {
                request.setCharacterEncoding(SystemProperties.get("adito.encoding", "UTF-8"), false);
                ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(handler.getClass().getClassLoader());
                if (handler.handle(pathInContext, pathParams, new RequestAdapter(request), new ResponseAdapter(response))) {
                    request.setHandled(true);
                    break;
                }
                Thread.currentThread().setContextClassLoader(oldLoader);
            } catch (RequestHandlerException e) {
                LOG
                        .error("Failed to handle request. Status code " + e.getCode());
                throw new HttpException(e.getCode(), e.getMessage());
            }
        }
    }
}
